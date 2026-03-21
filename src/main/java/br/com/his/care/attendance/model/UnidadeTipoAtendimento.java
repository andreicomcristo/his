package br.com.his.care.attendance.model;

import br.com.his.access.model.Unidade;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "unidade_tipo_atendimento")
public class UnidadeTipoAtendimento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unidade_id", nullable = false)
    private Unidade unidade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_atendimento_id", nullable = false)
    private TipoAtendimentoCadastro tipoAtendimento;

    @Column(nullable = false)
    private boolean ativo = true;

    @Column(name = "triagem_obrigatoria", nullable = false)
    private boolean triagemObrigatoria;

    @Column(name = "passa_consultorio", nullable = false)
    private boolean passaConsultorio = true;

    @Column(name = "permite_agendamento", nullable = false)
    private boolean permiteAgendamento = true;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Unidade getUnidade() {
        return unidade;
    }

    public void setUnidade(Unidade unidade) {
        this.unidade = unidade;
    }

    public TipoAtendimentoCadastro getTipoAtendimento() {
        return tipoAtendimento;
    }

    public void setTipoAtendimento(TipoAtendimentoCadastro tipoAtendimento) {
        this.tipoAtendimento = tipoAtendimento;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public boolean isTriagemObrigatoria() {
        return triagemObrigatoria;
    }

    public void setTriagemObrigatoria(boolean triagemObrigatoria) {
        this.triagemObrigatoria = triagemObrigatoria;
    }

    public boolean isPassaConsultorio() {
        return passaConsultorio;
    }

    public void setPassaConsultorio(boolean passaConsultorio) {
        this.passaConsultorio = passaConsultorio;
    }

    public boolean isPermiteAgendamento() {
        return permiteAgendamento;
    }

    public void setPermiteAgendamento(boolean permiteAgendamento) {
        this.permiteAgendamento = permiteAgendamento;
    }
}
