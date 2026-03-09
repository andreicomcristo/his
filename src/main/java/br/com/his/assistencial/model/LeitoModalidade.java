package br.com.his.assistencial.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "leito_modalidade")
public class LeitoModalidade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leito_id", nullable = false)
    private Leito leito;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "modalidade_tipo_id", nullable = false)
    private LeitoModalidadeTipo modalidadeTipo;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Leito getLeito() {
        return leito;
    }

    public void setLeito(Leito leito) {
        this.leito = leito;
    }

    public LeitoModalidadeTipo getModalidadeTipo() {
        return modalidadeTipo;
    }

    public void setModalidadeTipo(LeitoModalidadeTipo modalidadeTipo) {
        this.modalidadeTipo = modalidadeTipo;
    }
}
