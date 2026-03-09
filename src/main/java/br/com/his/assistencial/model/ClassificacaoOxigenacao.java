package br.com.his.assistencial.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "classificacao_oxigenacao")
public class ClassificacaoOxigenacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "saturacao_o2_com_terapia_o2")
    private Integer saturacaoO2ComTerapiaO2;

    @Column(name = "saturacao_o2_aa")
    private Integer saturacaoO2Aa;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getSaturacaoO2ComTerapiaO2() {
        return saturacaoO2ComTerapiaO2;
    }

    public void setSaturacaoO2ComTerapiaO2(Integer saturacaoO2ComTerapiaO2) {
        this.saturacaoO2ComTerapiaO2 = saturacaoO2ComTerapiaO2;
    }

    public Integer getSaturacaoO2Aa() {
        return saturacaoO2Aa;
    }

    public void setSaturacaoO2Aa(Integer saturacaoO2Aa) {
        this.saturacaoO2Aa = saturacaoO2Aa;
    }
}
