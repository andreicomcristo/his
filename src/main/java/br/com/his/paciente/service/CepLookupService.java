package br.com.his.paciente.service;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import br.com.his.configuracao.model.Cidade;
import br.com.his.configuracao.model.UnidadeFederativa;
import br.com.his.configuracao.repository.CidadeRepository;
import br.com.his.configuracao.repository.UnidadeFederativaRepository;
import br.com.his.paciente.dto.PacienteCepResponse;

@Service
public class CepLookupService {

    private final RestClient restClient;
    private final UnidadeFederativaRepository unidadeFederativaRepository;
    private final CidadeRepository cidadeRepository;

    public CepLookupService(RestClient.Builder restClientBuilder,
                            UnidadeFederativaRepository unidadeFederativaRepository,
                            CidadeRepository cidadeRepository) {
        this.restClient = restClientBuilder.build();
        this.unidadeFederativaRepository = unidadeFederativaRepository;
        this.cidadeRepository = cidadeRepository;
    }

    public PacienteCepResponse buscarPorCep(String cepInformado) {
        String cep = digitsOnly(cepInformado);
        if (cep.length() != 8) {
            throw new IllegalArgumentException("CEP invalido. Informe 8 digitos.");
        }

        ViaCepResponse viaCep = consultarViaCep(cep);
        if (viaCep == null || Boolean.TRUE.equals(viaCep.erro())) {
            throw new IllegalArgumentException("CEP nao encontrado.");
        }

        UnidadeFederativa uf = unidadeFederativaRepository.findBySiglaIgnoreCase(viaCep.uf())
                .orElseThrow(() -> new IllegalArgumentException("UF retornada pelo CEP nao encontrada no HIS."));
        Cidade cidade = localizarCidade(uf.getId(), viaCep.localidade());

        PacienteCepResponse response = new PacienteCepResponse();
        response.setCep(cep);
        response.setLogradouro(normalize(viaCep.logradouro()));
        response.setBairro(normalize(viaCep.bairro()));
        response.setComplemento(normalize(viaCep.complemento()));
        response.setUnidadeFederativaId(uf.getId());
        response.setUfSigla(uf.getSigla());
        response.setCidadeNome(normalize(viaCep.localidade()));
        if (cidade != null) {
            response.setCidadeId(cidade.getId());
            response.setCidadeNome(cidade.getNome());
        }
        return response;
    }

    private ViaCepResponse consultarViaCep(String cep) {
        try {
            return restClient.get()
                    .uri("https://viacep.com.br/ws/{cep}/json/", cep)
                    .retrieve()
                    .body(ViaCepResponse.class);
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() == HttpStatus.BAD_REQUEST || ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new IllegalArgumentException("CEP invalido ou nao encontrado.");
            }
            throw new IllegalArgumentException("Falha ao consultar CEP no ViaCEP.");
        } catch (Exception ex) {
            throw new IllegalArgumentException("Falha ao consultar CEP no ViaCEP.", ex);
        }
    }

    private Cidade localizarCidade(Long ufId, String localidade) {
        if (localidade == null || localidade.isBlank()) {
            return null;
        }
        String cidadeNormalizada = normalizeForMatch(localidade);
        List<Cidade> cidades = cidadeRepository.findByUnidadeFederativaIdOrderByNome(ufId);
        for (Cidade cidade : cidades) {
            if (normalizeForMatch(cidade.getNome()).equals(cidadeNormalizada)) {
                return cidade;
            }
        }
        for (Cidade cidade : cidades) {
            String nome = normalizeForMatch(cidade.getNome());
            if (nome.contains(cidadeNormalizada) || cidadeNormalizada.contains(nome)) {
                return cidade;
            }
        }
        return null;
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
        String withoutAccents = Normalizer.normalize(normalized, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return withoutAccents.toUpperCase(Locale.ROOT)
                .replaceAll("[^A-Z0-9 ]", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }

    private record ViaCepResponse(
            String cep,
            String logradouro,
            String complemento,
            String bairro,
            String localidade,
            String uf,
            Boolean erro) {
    }
}
