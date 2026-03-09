package br.com.his.assistencial.dto;

import br.com.his.assistencial.model.TipoAtendimento;
import br.com.his.paciente.dto.PacienteForm;

public class AtendimentoWizardForm {

    private int currentStep = 1;
    private String chegadaToken;

    private Boolean pacienteIdentificado;
    private String cpfBusca;
    private Long pacienteSelecionadoId;
    private boolean cadastrarNovoPaciente;
    private final PacienteForm novoPacienteForm = new PacienteForm();

    private String sexoTemporario = "NI";
    private Integer idadeAparenteTemporario;

    private TipoAtendimento tipoAtendimento;

    private final EntradaForm entradaForm = new EntradaForm();
    private final TriagemForm triagemForm = new TriagemForm();

    public int getCurrentStep() {
        return currentStep;
    }

    public void setCurrentStep(int currentStep) {
        this.currentStep = currentStep;
    }

    public String getChegadaToken() {
        return chegadaToken;
    }

    public void setChegadaToken(String chegadaToken) {
        this.chegadaToken = chegadaToken;
    }

    public Boolean getPacienteIdentificado() {
        return pacienteIdentificado;
    }

    public void setPacienteIdentificado(Boolean pacienteIdentificado) {
        this.pacienteIdentificado = pacienteIdentificado;
    }

    public String getCpfBusca() {
        return cpfBusca;
    }

    public void setCpfBusca(String cpfBusca) {
        this.cpfBusca = cpfBusca;
    }

    public Long getPacienteSelecionadoId() {
        return pacienteSelecionadoId;
    }

    public void setPacienteSelecionadoId(Long pacienteSelecionadoId) {
        this.pacienteSelecionadoId = pacienteSelecionadoId;
    }

    public boolean isCadastrarNovoPaciente() {
        return cadastrarNovoPaciente;
    }

    public void setCadastrarNovoPaciente(boolean cadastrarNovoPaciente) {
        this.cadastrarNovoPaciente = cadastrarNovoPaciente;
    }

    public PacienteForm getNovoPacienteForm() {
        return novoPacienteForm;
    }

    public String getNovoPacienteNome() {
        return novoPacienteForm.getNome();
    }

    public void setNovoPacienteNome(String novoPacienteNome) {
        novoPacienteForm.setNome(novoPacienteNome);
    }

    public String getNovoPacienteCpf() {
        return novoPacienteForm.getCpf();
    }

    public void setNovoPacienteCpf(String novoPacienteCpf) {
        novoPacienteForm.setCpf(novoPacienteCpf);
    }

    public String getNovoPacienteCns() {
        return novoPacienteForm.getCns();
    }

    public void setNovoPacienteCns(String novoPacienteCns) {
        novoPacienteForm.setCns(novoPacienteCns);
    }

    public java.time.LocalDate getNovoPacienteDataNascimento() {
        return novoPacienteForm.getDataNascimento();
    }

    public void setNovoPacienteDataNascimento(java.time.LocalDate novoPacienteDataNascimento) {
        novoPacienteForm.setDataNascimento(novoPacienteDataNascimento);
    }

    public String getNovoPacienteSexo() {
        return novoPacienteForm.getSexo();
    }

    public void setNovoPacienteSexo(String novoPacienteSexo) {
        novoPacienteForm.setSexo(novoPacienteSexo);
    }

    public String getNovoPacienteTelefone() {
        return novoPacienteForm.getTelefone();
    }

    public void setNovoPacienteTelefone(String novoPacienteTelefone) {
        novoPacienteForm.setTelefone(novoPacienteTelefone);
    }

    public String getNovoPacienteNomeMae() {
        return novoPacienteForm.getNomeMae();
    }

    public void setNovoPacienteNomeMae(String novoPacienteNomeMae) {
        novoPacienteForm.setNomeMae(novoPacienteNomeMae);
    }

    public String getNovoPacienteNomePai() {
        return novoPacienteForm.getNomePai();
    }

    public void setNovoPacienteNomePai(String novoPacienteNomePai) {
        novoPacienteForm.setNomePai(novoPacienteNomePai);
    }

    public String getSexoTemporario() {
        return sexoTemporario;
    }

    public void setSexoTemporario(String sexoTemporario) {
        this.sexoTemporario = sexoTemporario;
    }

    public Integer getIdadeAparenteTemporario() {
        return idadeAparenteTemporario;
    }

    public void setIdadeAparenteTemporario(Integer idadeAparenteTemporario) {
        this.idadeAparenteTemporario = idadeAparenteTemporario;
    }

    public TipoAtendimento getTipoAtendimento() {
        return tipoAtendimento;
    }

    public void setTipoAtendimento(TipoAtendimento tipoAtendimento) {
        this.tipoAtendimento = tipoAtendimento;
    }

    public EntradaForm getEntradaForm() {
        return entradaForm;
    }

    public TriagemForm getTriagemForm() {
        return triagemForm;
    }
}
