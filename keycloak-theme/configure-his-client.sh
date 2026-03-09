#!/usr/bin/env bash
set -euo pipefail

# Configure Keycloak OIDC client redirect/logout settings for the HIS app.
#
# Required tools:
# - curl
# - jq
#
# Example:
#   KEYCLOAK_URL=http://localhost:8081 \
#   KEYCLOAK_REALM=master \
#   KEYCLOAK_ADMIN=admin \
#   KEYCLOAK_ADMIN_PASSWORD=admin \
#   KEYCLOAK_CLIENT_ID=his-web \
#   ./keycloak-theme/configure-his-client.sh

require_cmd() {
  if ! command -v "$1" >/dev/null 2>&1; then
    echo "Erro: comando '$1' nao encontrado." >&2
    exit 1
  fi
}

require_cmd curl
require_cmd jq

KEYCLOAK_URL="${KEYCLOAK_URL:-http://localhost:8081}"
KEYCLOAK_REALM="${KEYCLOAK_REALM:-master}"
KEYCLOAK_ADMIN="${KEYCLOAK_ADMIN:-admin}"
KEYCLOAK_ADMIN_PASSWORD="${KEYCLOAK_ADMIN_PASSWORD:-admin}"
KEYCLOAK_CLIENT_ID="${KEYCLOAK_CLIENT_ID:-his-web}"

APP_BASE_URL="${APP_BASE_URL:-http://localhost:8080}"
REDIRECT_URI_PATTERN="${REDIRECT_URI_PATTERN:-${APP_BASE_URL}/login/oauth2/code/*}"
POST_LOGOUT_URIS="${POST_LOGOUT_URIS:-${APP_BASE_URL}/login##${APP_BASE_URL}/}"
WEB_ORIGIN="${WEB_ORIGIN:-${APP_BASE_URL}}"

echo "Obtendo token de administrador..."
token_response="$(curl -sS --fail \
  -X POST "${KEYCLOAK_URL}/realms/${KEYCLOAK_REALM}/protocol/openid-connect/token" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  --data-urlencode "grant_type=password" \
  --data-urlencode "client_id=admin-cli" \
  --data-urlencode "username=${KEYCLOAK_ADMIN}" \
  --data-urlencode "password=${KEYCLOAK_ADMIN_PASSWORD}")"

access_token="$(echo "${token_response}" | jq -r '.access_token')"
if [[ -z "${access_token}" || "${access_token}" == "null" ]]; then
  echo "Erro: nao foi possivel obter access token do Keycloak." >&2
  exit 1
fi

echo "Buscando cliente '${KEYCLOAK_CLIENT_ID}'..."
client_json="$(curl -sS --fail \
  "${KEYCLOAK_URL}/admin/realms/${KEYCLOAK_REALM}/clients?clientId=${KEYCLOAK_CLIENT_ID}" \
  -H "Authorization: Bearer ${access_token}")"

internal_client_id="$(echo "${client_json}" | jq -r '.[0].id // empty')"
if [[ -z "${internal_client_id}" ]]; then
  echo "Erro: cliente '${KEYCLOAK_CLIENT_ID}' nao encontrado no realm '${KEYCLOAK_REALM}'." >&2
  exit 1
fi

current_client="$(curl -sS --fail \
  "${KEYCLOAK_URL}/admin/realms/${KEYCLOAK_REALM}/clients/${internal_client_id}" \
  -H "Authorization: Bearer ${access_token}")"

echo "Atualizando redirect/logout/web origin..."
updated_client="$(echo "${current_client}" | jq \
  --arg redirect "${REDIRECT_URI_PATTERN}" \
  --arg origin "${WEB_ORIGIN}" \
  --arg postLogout "${POST_LOGOUT_URIS}" \
  '
  .redirectUris = [$redirect]
  | .webOrigins = [$origin]
  | .attributes = (.attributes // {})
  | .attributes["post.logout.redirect.uris"] = $postLogout
  ')"

curl -sS --fail \
  -X PUT "${KEYCLOAK_URL}/admin/realms/${KEYCLOAK_REALM}/clients/${internal_client_id}" \
  -H "Authorization: Bearer ${access_token}" \
  -H "Content-Type: application/json" \
  --data "${updated_client}" >/dev/null

echo "OK. Cliente '${KEYCLOAK_CLIENT_ID}' atualizado com sucesso."
echo "redirectUris: ${REDIRECT_URI_PATTERN}"
echo "post.logout.redirect.uris: ${POST_LOGOUT_URIS}"
echo "webOrigins: ${WEB_ORIGIN}"
