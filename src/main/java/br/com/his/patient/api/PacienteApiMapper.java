package br.com.his.patient.api;

import org.springframework.stereotype.Component;

import br.com.his.patient.dto.PacienteForm;
import br.com.his.patient.model.Paciente;
import br.com.his.patient.api.dto.PacienteRequest;
import br.com.his.patient.api.dto.PacienteResponse;
import br.com.his.patient.api.dto.PacienteTemporarioRequest;

@Component
public class PacienteApiMapper {

    public PacienteForm toForm(PacienteRequest request) {
        PacienteForm form = new PacienteForm();
        form.setNome(request.getNome());
        form.setNomeSocial(request.getNomeSocial());
        form.setCpf(request.getCpf());
        form.setCns(request.getCns());
        form.setRg(request.getRg());
        form.setDataNascimento(request.getDataNascimento());
        form.setSexo(request.getSexo());
        form.setTelefone(request.getTelefone());
        form.setNomeMae(request.getNomeMae());
        form.setNomePai(request.getNomePai());
        form.setRacaCorId(request.getRacaCorId());
        form.setEtniaIndigenaId(request.getEtniaIndigenaId());
        form.setNacionalidadeId(request.getNacionalidadeId());
        form.setNaturalidadeId(request.getNaturalidadeId());
        form.setEstadoCivilId(request.getEstadoCivilId());
        form.setEscolaridadeId(request.getEscolaridadeId());
        form.setTipoSanguineoId(request.getTipoSanguineoId());
        form.setOrientacaoSexualId(request.getOrientacaoSexualId());
        form.setIdentidadeGeneroId(request.getIdentidadeGeneroId());
        form.setDeficienciaId(request.getDeficienciaId());
        form.setProfissaoId(request.getProfissaoId());
        form.setProcedenciaId(request.getProcedenciaId());
        form.setEmail(request.getEmail());
        form.setObservacoes(request.getObservacoes());
        form.setCep(request.getCep());
        form.setLogradouro(request.getLogradouro());
        form.setNumero(request.getNumero());
        form.setComplemento(request.getComplemento());
        form.setBairro(request.getBairro());
        form.setUnidadeFederativaId(request.getUnidadeFederativaId());
        form.setCidadeId(request.getCidadeId());
        form.setTemporario(false);
        return form;
    }

    public PacienteForm toForm(PacienteTemporarioRequest request) {
        PacienteForm form = new PacienteForm();
        form.setNome(request.getNome());
        form.setNomeSocial(request.getNomeSocial());
        form.setCpf(request.getCpf());
        form.setCns(request.getCns());
        form.setRg(request.getRg());
        form.setDataNascimento(request.getDataNascimento());
        form.setSexo(request.getSexo());
        form.setTelefone(request.getTelefone());
        form.setNomeMae(request.getNomeMae());
        form.setNomePai(request.getNomePai());
        form.setRacaCorId(request.getRacaCorId());
        form.setEtniaIndigenaId(request.getEtniaIndigenaId());
        form.setNacionalidadeId(request.getNacionalidadeId());
        form.setNaturalidadeId(request.getNaturalidadeId());
        form.setEstadoCivilId(request.getEstadoCivilId());
        form.setEscolaridadeId(request.getEscolaridadeId());
        form.setTipoSanguineoId(request.getTipoSanguineoId());
        form.setOrientacaoSexualId(request.getOrientacaoSexualId());
        form.setIdentidadeGeneroId(request.getIdentidadeGeneroId());
        form.setDeficienciaId(request.getDeficienciaId());
        form.setProfissaoId(request.getProfissaoId());
        form.setProcedenciaId(request.getProcedenciaId());
        form.setEmail(request.getEmail());
        form.setObservacoes(request.getObservacoes());
        form.setCep(request.getCep());
        form.setLogradouro(request.getLogradouro());
        form.setNumero(request.getNumero());
        form.setComplemento(request.getComplemento());
        form.setBairro(request.getBairro());
        form.setUnidadeFederativaId(request.getUnidadeFederativaId());
        form.setCidadeId(request.getCidadeId());
        form.setTemporario(true);
        form.setIdadeAparente(request.getIdadeAparente());
        return form;
    }

    public PacienteResponse toResponse(Paciente paciente) {
        PacienteResponse response = new PacienteResponse();
        response.setId(paciente.getId());
        response.setNome(paciente.getNome());
        response.setNomeSocial(paciente.getNomeSocial());
        response.setCpf(paciente.getCpf());
        response.setCns(paciente.getCns());
        response.setRg(paciente.getRg());
        response.setDataNascimento(paciente.getDataNascimento());
        response.setSexo(paciente.getSexo());
        response.setTelefone(paciente.getTelefone());
        response.setNomeMae(paciente.getNomeMae());
        response.setNomePai(paciente.getNomePai());
        response.setRacaCorId(paciente.getRacaCor() == null ? null : paciente.getRacaCor().getId());
        response.setEtniaIndigenaId(paciente.getEtniaIndigena() == null ? null : paciente.getEtniaIndigena().getId());
        response.setNacionalidadeId(paciente.getNacionalidade() == null ? null : paciente.getNacionalidade().getId());
        response.setNaturalidadeId(paciente.getNaturalidade() == null ? null : paciente.getNaturalidade().getId());
        response.setEstadoCivilId(paciente.getEstadoCivil() == null ? null : paciente.getEstadoCivil().getId());
        response.setEscolaridadeId(paciente.getEscolaridade() == null ? null : paciente.getEscolaridade().getId());
        response.setTipoSanguineoId(paciente.getTipoSanguineo() == null ? null : paciente.getTipoSanguineo().getId());
        response.setOrientacaoSexualId(paciente.getOrientacaoSexual() == null ? null : paciente.getOrientacaoSexual().getId());
        response.setIdentidadeGeneroId(paciente.getIdentidadeGenero() == null ? null : paciente.getIdentidadeGenero().getId());
        response.setDeficienciaId(paciente.getDeficiencia() == null ? null : paciente.getDeficiencia().getId());
        response.setProfissaoId(paciente.getProfissao() == null ? null : paciente.getProfissao().getId());
        response.setProcedenciaId(paciente.getProcedencia() == null ? null : paciente.getProcedencia().getId());
        response.setEmail(paciente.getEmail());
        response.setObservacoes(paciente.getObservacoes());
        response.setCep(paciente.getCep());
        response.setLogradouro(paciente.getLogradouro());
        response.setNumero(paciente.getNumero());
        response.setComplemento(paciente.getComplemento());
        response.setBairro(paciente.getBairro());
        response.setCidadeId(paciente.getCidade() == null ? null : paciente.getCidade().getId());
        response.setCidade(paciente.getCidade() == null ? null : paciente.getCidade().getNome());
        response.setUnidadeFederativaId(paciente.getCidade() == null ? null : paciente.getCidade().getUnidadeFederativa().getId());
        response.setUf(paciente.getCidade() == null ? null : paciente.getCidade().getUnidadeFederativa().getSigla());
        response.setTemporario(paciente.isTemporario());
        response.setIdadeAparente(paciente.getIdadeAparente());
        response.setAtivo(paciente.isAtivo());
        response.setDataCancelamento(paciente.getDataCancelamento());
        response.setMergedIntoId(paciente.getMergedInto() == null ? null : paciente.getMergedInto().getId());
        response.setCriadoEm(paciente.getCriadoEm());
        response.setAtualizadoEm(paciente.getAtualizadoEm());
        return response;
    }
}
