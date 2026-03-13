package br.com.his.access.context;

import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpSession;

@Component
public class UnidadeContext {

    private static final String SESSION_UNIDADE_KEY = "unidade_atual_id";
    private static final String SESSION_ATUACAO_KEY = "colaborador_unidade_atuacao_id";

    public Optional<Long> getUnidadeAtual() {
        return readLongFromSession(SESSION_UNIDADE_KEY);
    }

    public void setUnidadeAtual(Long id) {
        if (id == null) {
            clearUnidadeAtual();
            return;
        }
        HttpSession session = getSession(true);
        if (session != null) {
            Long unidadeAtual = readLong(session.getAttribute(SESSION_UNIDADE_KEY)).orElse(null);
            session.setAttribute(SESSION_UNIDADE_KEY, id);
            if (!Objects.equals(unidadeAtual, id)) {
                session.removeAttribute(SESSION_ATUACAO_KEY);
            }
        }
    }

    public Optional<Long> getAtuacaoAtual() {
        return readLongFromSession(SESSION_ATUACAO_KEY);
    }

    public void setAtuacaoAtual(Long colaboradorUnidadeAtuacaoId) {
        HttpSession session = getSession(true);
        if (session == null) {
            return;
        }
        if (colaboradorUnidadeAtuacaoId == null) {
            session.removeAttribute(SESSION_ATUACAO_KEY);
            return;
        }
        session.setAttribute(SESSION_ATUACAO_KEY, colaboradorUnidadeAtuacaoId);
    }

    public void clearAtuacaoAtual() {
        HttpSession session = getSession(false);
        if (session != null) {
            session.removeAttribute(SESSION_ATUACAO_KEY);
        }
    }

    public void clearUnidadeAtual() {
        HttpSession session = getSession(false);
        if (session != null) {
            session.removeAttribute(SESSION_UNIDADE_KEY);
            session.removeAttribute(SESSION_ATUACAO_KEY);
        }
    }

    public void clear() {
        clearUnidadeAtual();
    }

    private Optional<Long> readLongFromSession(String key) {
        HttpSession session = getSession(false);
        if (session != null) {
            return readLong(session.getAttribute(key));
        }
        return Optional.empty();
    }

    private Optional<Long> readLong(Object value) {
        if (value instanceof Long id) {
            return Optional.of(id);
        }
        if (value instanceof Number number) {
            return Optional.of(number.longValue());
        }
        return Optional.empty();
    }

    private HttpSession getSession(boolean create) {
        RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
        if (attrs instanceof ServletRequestAttributes servletAttrs) {
            return servletAttrs.getRequest().getSession(create);
        }
        return null;
    }
}
