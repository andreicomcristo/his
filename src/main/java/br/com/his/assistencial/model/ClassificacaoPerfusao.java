package br.com.his.assistencial.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "classificacao_perfusao")
public class ClassificacaoPerfusao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "perfusao_capilar_periferica_seg")
    private Integer perfusaoCapilarPerifericaSeg;

    @Column(name = "preenchimento_capilar_central_seg")
    private Integer preenchimentoCapilarCentralSeg;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getPerfusaoCapilarPerifericaSeg() {
        return perfusaoCapilarPerifericaSeg;
    }

    public void setPerfusaoCapilarPerifericaSeg(Integer perfusaoCapilarPerifericaSeg) {
        this.perfusaoCapilarPerifericaSeg = perfusaoCapilarPerifericaSeg;
    }

    public Integer getPreenchimentoCapilarCentralSeg() {
        return preenchimentoCapilarCentralSeg;
    }

    public void setPreenchimentoCapilarCentralSeg(Integer preenchimentoCapilarCentralSeg) {
        this.preenchimentoCapilarCentralSeg = preenchimentoCapilarCentralSeg;
    }
}
