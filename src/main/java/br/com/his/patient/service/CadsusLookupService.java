package br.com.his.patient.service;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.his.reference.location.model.Municipio;
import br.com.his.reference.location.model.UnidadeFederativa;
import br.com.his.reference.location.repository.MunicipioRepository;
import br.com.his.reference.location.repository.UnidadeFederativaRepository;
import br.com.his.patient.config.CadsusProperties;
import br.com.his.patient.dto.PacienteCpfSUSResponse;
import br.com.his.patient.model.lookup.RacaCor;
import br.com.his.patient.repository.RacaCorRepository;

@Service
public class CadsusLookupService {

    private final CadsusProperties properties;
    private final ResourceLoader resourceLoader;
    private final UnidadeFederativaRepository unidadeFederativaRepository;
    private final MunicipioRepository MunicipioRepository;
    private final RacaCorRepository racaCorRepository;
    private final CepLookupService cepLookupService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public CadsusLookupService(CadsusProperties properties,
                               ResourceLoader resourceLoader,
                               UnidadeFederativaRepository unidadeFederativaRepository,
                               MunicipioRepository MunicipioRepository,
                               RacaCorRepository racaCorRepository,
                               CepLookupService cepLookupService) {
        this.properties = properties;
        this.resourceLoader = resourceLoader;
        this.unidadeFederativaRepository = unidadeFederativaRepository;
        this.MunicipioRepository = MunicipioRepository;
        this.racaCorRepository = racaCorRepository;
        this.cepLookupService = cepLookupService;
    }

    public boolean isEnabled() {
        return properties.isEnabled();
    }

    public PacienteCpfSUSResponse buscarPorCpf(String cpfInformado) {
        if (!properties.isEnabled()) {
            throw new IllegalArgumentException("Integracao CADSUS desabilitada.");
        }
        String cpf = digitsOnly(cpfInformado);
        if (cpf.length() != 11) {
            throw new IllegalArgumentException("CPF invalido. Informe 11 digitos.");
        }
        String token = obterToken();
        String xml = consultarCpf(cpf, token);
        return parseResponse(xml, cpf);
    }

    private String obterToken() {
        if (properties.getCertPath() == null || properties.getCertPath().isBlank()
                || properties.getCertPassword() == null || properties.getCertPassword().isBlank()) {
            throw new IllegalArgumentException("Certificado CADSUS nao configurado.");
        }
        try {
            KeyStore clientKeyStore = KeyStore.getInstance(properties.getCertType());
            try (InputStream is = loadCertResource().getInputStream()) {
                clientKeyStore.load(is, properties.getCertPassword().toCharArray());
            }

            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(clientKeyStore, properties.getCertPassword().toCharArray());

            SSLContext sslContext = SSLContext.getInstance(properties.getSslProtocol());
            sslContext.init(keyManagerFactory.getKeyManagers(), null, null);

            URL url = new URL(properties.getTokenUrl());
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setSSLSocketFactory(sslContext.getSocketFactory());

            X509Certificate clientCert = (X509Certificate) clientKeyStore
                    .getCertificate(clientKeyStore.aliases().nextElement());
            String base64Cert = Base64.getEncoder().encodeToString(clientCert.getEncoded());
            connection.setRequestProperty("X-CLIENT-CERTIFICATE", base64Cert);

            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw new IllegalArgumentException("Falha ao obter token CADSUS.");
            }

            try (BufferedReader in = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                String response = in.readLine();
                TokenResponse dto = objectMapper.readValue(response, TokenResponse.class);
                if (dto.access_token == null || dto.access_token.isBlank()) {
                    throw new IllegalArgumentException("Token CADSUS nao retornado.");
                }
                return dto.access_token;
            }
        } catch (Exception ex) {
            throw new IllegalArgumentException("Falha ao autenticar no CADSUS: " + rootCauseMessage(ex), ex);
        }
    }

    private String consultarCpf(String cpf, String token) {
        String script = """
                <soap:Envelope xmlns:soap="http://www.w3.org/2003/05/soap-envelope">
                  <soap:Body>
                    <PRPA_IN201305UV02 xsi:schemaLocation="urn:hl7-org:v3 ./schema/HL7V3/NE2008/multicacheschemas/PRPA_IN201305UV02.xsd" ITSVersion="XML_1.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns="urn:hl7-org:v3">
                      <id root="2.16.840.1.113883.4.714" extension="123456"/>
                      <creationTime value="20070428150301"/>
                      <interactionId root="2.16.840.1.113883.1.6" extension="PRPA_IN201305UV02"/>
                      <processingCode code="T"/>
                      <processingModeCode code="T"/>
                      <acceptAckCode code="AL"/>
                      <receiver typeCode="RCV">
                        <device classCode="DEV" determinerCode="INSTANCE">
                          <id root="2.16.840.1.113883.3.72.6.5.100.85"/>
                        </device>
                      </receiver>
                      <sender typeCode="SND">
                        <device classCode="DEV" determinerCode="INSTANCE">
                          <id root="2.16.840.1.113883.3.72.6.2"/>
                          <name>CADSUS</name>
                        </device>
                      </sender>
                      <controlActProcess classCode="CACT" moodCode="EVN">
                        <code code="PRPA_TE201305UV02" codeSystem="2.16.840.1.113883.1.6"/>
                        <queryByParameter>
                          <queryId root="1.2.840.114350.1.13.28.1.18.5.999" extension="1840997084"/>
                          <statusCode code="new"/>
                          <responseModalityCode code="R"/>
                          <responsePriorityCode code="I"/>
                          <parameterList>
                            <livingSubjectId>
                              <value root="2.16.840.1.113883.13.237" extension="%s"/>
                              <semanticsText>LivingSubject.id</semanticsText>
                            </livingSubjectId>
                          </parameterList>
                        </queryByParameter>
                      </controlActProcess>
                    </PRPA_IN201305UV02>
                  </soap:Body>
                </soap:Envelope>
                """.formatted(cpf);

        try {
            URL url = new URL(properties.getSupplierUrl());
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/soap+xml; charset=utf-8");
            connection.setRequestProperty("Authorization", "jwt " + token);

            connection.getOutputStream().write(script.getBytes(StandardCharsets.UTF_8));
            connection.getOutputStream().flush();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IllegalArgumentException("Falha na consulta CADSUS.");
            }
            try (InputStream input = connection.getInputStream()) {
                return new String(input.readAllBytes(), StandardCharsets.UTF_8);
            }
        } catch (Exception ex) {
            throw new IllegalArgumentException("Falha na consulta CADSUS: " + rootCauseMessage(ex), ex);
        }
    }

    private PacienteCpfSUSResponse parseResponse(String xml, String cpf) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(false);
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Element envelope = builder.parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)))
                    .getDocumentElement();

            PacienteCpfSUSResponse response = new PacienteCpfSUSResponse();
            response.setCpf(cpf);

            response.setCns(findIdByRoot(envelope, "2.16.840.1.113883.13.236"));
            response.setRg(findIdByRoot(envelope, "2.16.840.1.113883.13.243"));
            response.setNome(firstGiven(envelope, 0));
            response.setNomeMae(firstGiven(envelope, 1));
            response.setNomePai(firstGiven(envelope, 2));
            response.setSexo(parseSexo(findAttribute(envelope, "administrativeGenderCode", "code")));
            response.setRacaCorId(resolveRaca(findAttribute(envelope, "raceCode", "code")));
            response.setDataNascimento(parseData(findAttribute(envelope, "birthTime", "value")));
            response.setEmail(findTelecom(envelope, "NET"));
            response.setCep(formatCep(textOf(envelope, "postalCode")));
            response.setLogradouro(textOf(envelope, "streetName"));
            response.setNumero(textOf(envelope, "houseNumber"));
            response.setComplemento(normalizeComplemento(textOf(envelope, "unitID")));
            response.setBairro(textOf(envelope, "additionalLocator"));

            String ufSigla = textOf(envelope, "state");
            String municipioNome = firstCity(envelope);
            UnidadeFederativa uf = null;
            if (ufSigla != null && !ufSigla.isBlank()) {
                uf = unidadeFederativaRepository.findBySiglaIgnoreCase(ufSigla).orElse(null);
            } else if (municipioNome != null) {
                Municipio Municipio = MunicipioRepository.findAll().stream()
                        .filter(c -> normalizeForMatch(c.getNome()).equals(normalizeForMatch(municipioNome)))
                        .findFirst()
                        .orElse(null);
                if (Municipio != null) {
                    uf = Municipio.getUnidadeFederativa();
                    response.setMunicipioId(Municipio.getId());
                }
            }
            if (uf != null) {
                response.setUnidadeFederativaId(uf.getId());
                if (response.getMunicipioId() == null && municipioNome != null) {
                    Municipio Municipio = matchCity(uf.getId(), municipioNome);
                    if (Municipio != null) {
                        response.setMunicipioId(Municipio.getId());
                    }
                }
            }
            if (response.getMunicipioId() == null && response.getCep() != null) {
                applyFallbackByCep(response);
            }

            if ((response.getNome() == null || response.getNome().isBlank()) && (response.getCns() == null || response.getCns().isBlank())) {
                throw new IllegalArgumentException("CPF nao encontrado no CADSUS.");
            }
            return response;
        } catch (IllegalArgumentException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new IllegalArgumentException("Falha ao interpretar resposta do CADSUS.", ex);
        }
    }

    private Municipio matchCity(Long ufId, String municipioNome) {
        List<Municipio> Municipios = MunicipioRepository.findByUnidadeFederativaIdOrderByNome(ufId);
        String target = normalizeForMatch(municipioNome);
        return Municipios.stream()
                .filter(c -> normalizeForMatch(c.getNome()).equals(target))
                .findFirst()
                .orElseGet(() -> Municipios.stream()
                        .filter(c -> normalizeForMatch(c.getNome()).contains(target) || target.contains(normalizeForMatch(c.getNome())))
                        .findFirst()
                        .orElse(null));
    }

    private void applyFallbackByCep(PacienteCpfSUSResponse response) {
        try {
            var cepData = cepLookupService.buscarPorCep(response.getCep());
            if (response.getUnidadeFederativaId() == null && cepData.getUnidadeFederativaId() != null) {
                response.setUnidadeFederativaId(cepData.getUnidadeFederativaId());
            }
            if (cepData.getMunicipioId() != null) {
                response.setMunicipioId(cepData.getMunicipioId());
            }
            if ((response.getLogradouro() == null || response.getLogradouro().isBlank()) && cepData.getLogradouro() != null) {
                response.setLogradouro(cepData.getLogradouro());
            }
            if ((response.getBairro() == null || response.getBairro().isBlank()) && cepData.getBairro() != null) {
                response.setBairro(cepData.getBairro());
            }
            if ((response.getComplemento() == null || response.getComplemento().isBlank()) && cepData.getComplemento() != null) {
                response.setComplemento(cepData.getComplemento());
            }
        } catch (IllegalArgumentException ex) {
            // Fallback opportunistic only. Keep CADSUS response even if CEP lookup fails.
        }
    }

    private Long resolveRaca(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            return null;
        }
        return racaCorRepository.findByCodigo(codigo)
                .map(RacaCor::getId)
                .orElse(null);
    }

    private String parseData(String value) {
        if (value == null || value.length() < 8) {
            return null;
        }
        LocalDate data = LocalDate.parse(value.substring(0, 8), DateTimeFormatter.ofPattern("yyyyMMdd"));
        return data.toString();
    }

    private String parseSexo(String code) {
        if ("M".equalsIgnoreCase(code)) {
            return "M";
        }
        if ("F".equalsIgnoreCase(code)) {
            return "F";
        }
        return null;
    }

    private String firstGiven(Element envelope, int index) {
        NodeList list = envelope.getElementsByTagName("given");
        if (list.getLength() <= index || !(list.item(index) instanceof Element element)) {
            return null;
        }
        return normalize(element.getTextContent());
    }

    private String firstCity(Element envelope) {
        NodeList list = envelope.getElementsByTagName("city");
        if (list.getLength() == 0 || !(list.item(0) instanceof Element element)) {
            return null;
        }
        return normalize(element.getTextContent());
    }

    private String findIdByRoot(Element envelope, String root) {
        NodeList ids = envelope.getElementsByTagName("id");
        for (int i = 0; i < ids.getLength(); i++) {
            if (ids.item(i) instanceof Element element && root.equals(element.getAttribute("root"))) {
                String value = element.getAttribute("extension");
                if (value != null && !value.isBlank()) {
                    return value;
                }
            }
        }
        return null;
    }

    private String findTelecom(Element envelope, String use) {
        NodeList list = envelope.getElementsByTagName("telecom");
        for (int i = 0; i < list.getLength(); i++) {
            if (list.item(i) instanceof Element element && use.equalsIgnoreCase(element.getAttribute("use"))) {
                return normalize(element.getAttribute("value"));
            }
        }
        return null;
    }

    private String findAttribute(Element envelope, String tagName, String attribute) {
        NodeList list = envelope.getElementsByTagName(tagName);
        if (list.getLength() == 0 || !(list.item(0) instanceof Element element)) {
            return null;
        }
        return normalize(element.getAttribute(attribute));
    }

    private String textOf(Element envelope, String tagName) {
        NodeList list = envelope.getElementsByTagName(tagName);
        if (list.getLength() == 0 || !(list.item(0) instanceof Element element)) {
            return null;
        }
        return normalize(element.getTextContent());
    }

    private String formatCep(String value) {
        String digits = digitsOnly(value);
        if (digits.length() != 8) {
            return normalize(value);
        }
        return digits.substring(0, 5) + "-" + digits.substring(5);
    }

    private String normalizeComplemento(String value) {
        String normalized = normalize(value);
        if (normalized == null) {
            return null;
        }
        if ("NA".equalsIgnoreCase(normalized) || "SEM INFORMACAO".equalsIgnoreCase(normalized)) {
            return null;
        }
        return normalized;
    }

    private Resource loadCertResource() {
        return resourceLoader.getResource(properties.getCertPath());
    }

    private static String digitsOnly(String value) {
        return value == null ? "" : value.replaceAll("\\D", "");
    }

    private static String normalize(String value) {
        if (value == null) {
            return null;
        }
        String normalized = value.trim();
        return normalized.isBlank() ? null : normalized;
    }

    private static String normalizeForMatch(String value) {
        String normalized = normalize(value);
        if (normalized == null) {
            return "";
        }
        String withoutAccents = java.text.Normalizer.normalize(normalized, java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return withoutAccents.toUpperCase(java.util.Locale.ROOT)
                .replaceAll("[^A-Z0-9 ]", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class TokenResponse {
        public String access_token;
    }

    private static String rootCauseMessage(Throwable ex) {
        Throwable current = ex;
        while (current.getCause() != null) {
            current = current.getCause();
        }
        String message = current.getMessage();
        return (message == null || message.isBlank()) ? current.getClass().getSimpleName() : message;
    }
}

