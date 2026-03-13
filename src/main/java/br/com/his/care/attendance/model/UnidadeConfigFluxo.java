package br.com.his.care.attendance.model;

import br.com.his.care.attendance.api.dto.*;
import br.com.his.care.attendance.dto.*;
import br.com.his.care.attendance.model.*;
import br.com.his.care.attendance.repository.*;
import br.com.his.care.attendance.service.*;
import br.com.his.care.admission.dto.*;
import br.com.his.care.admission.model.*;
import br.com.his.care.admission.repository.*;
import br.com.his.care.triage.dto.*;
import br.com.his.care.triage.model.*;
import br.com.his.care.triage.repository.*;
import br.com.his.care.inpatient.dto.*;
import br.com.his.care.inpatient.model.*;
import br.com.his.care.inpatient.repository.*;
import br.com.his.care.inpatient.service.*;
import br.com.his.care.episode.model.*;
import br.com.his.care.episode.repository.*;
import br.com.his.care.timeline.dto.*;
import br.com.his.care.timeline.model.*;
import br.com.his.care.timeline.repository.*;

import br.com.his.access.model.Unidade;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PostPersist;
import jakarta.persistence.Transient;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import org.springframework.data.domain.Persistable;

@Entity
@Table(name = "unidade_config_fluxo")
public class UnidadeConfigFluxo implements Persistable<Long> {

    @Id
    @Column(name = "unidade_id")
    private Long unidadeId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "unidade_id")
    private Unidade unidade;

    @Enumerated(EnumType.STRING)
    @Column(name = "primeiro_passo", nullable = false, length = 20)
    private PrimeiroPassoFluxo primeiroPasso;

    @Column(name = "exige_ficha_para_medico", nullable = false)
    private boolean exigeFichaParaMedico;

    @Column(name = "cria_episodio_automatico", nullable = false)
    private boolean criaEpisodioAutomatico;

    @Column(name = "permite_agendamento", nullable = false)
    private boolean permiteAgendamento;

    @Transient
    private boolean newEntity = true;

    public Long getUnidadeId() {
        return unidadeId;
    }

    public void setUnidadeId(Long unidadeId) {
        this.unidadeId = unidadeId;
    }

    public Unidade getUnidade() {
        return unidade;
    }

    public void setUnidade(Unidade unidade) {
        this.unidade = unidade;
    }

    public PrimeiroPassoFluxo getPrimeiroPasso() {
        return primeiroPasso;
    }

    public void setPrimeiroPasso(PrimeiroPassoFluxo primeiroPasso) {
        this.primeiroPasso = primeiroPasso;
    }

    public boolean isExigeFichaParaMedico() {
        return exigeFichaParaMedico;
    }

    public void setExigeFichaParaMedico(boolean exigeFichaParaMedico) {
        this.exigeFichaParaMedico = exigeFichaParaMedico;
    }

    public boolean isCriaEpisodioAutomatico() {
        return criaEpisodioAutomatico;
    }

    public void setCriaEpisodioAutomatico(boolean criaEpisodioAutomatico) {
        this.criaEpisodioAutomatico = criaEpisodioAutomatico;
    }

    public boolean isPermiteAgendamento() {
        return permiteAgendamento;
    }

    public void setPermiteAgendamento(boolean permiteAgendamento) {
        this.permiteAgendamento = permiteAgendamento;
    }

    @Override
    public Long getId() {
        return unidadeId;
    }

    @Override
    public boolean isNew() {
        return newEntity;
    }

    @PostLoad
    @PostPersist
    void markNotNew() {
        this.newEntity = false;
    }
}
