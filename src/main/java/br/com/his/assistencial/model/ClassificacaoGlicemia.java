package br.com.his.assistencial.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "classificacao_glicemia")
public class ClassificacaoGlicemia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "glicemia_capilar", precision = 6, scale = 2)
    private BigDecimal glicemiaCapilar;

    @Column(name = "hgt")
    private Integer hgt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getGlicemiaCapilar() {
        return glicemiaCapilar;
    }

    public void setGlicemiaCapilar(BigDecimal glicemiaCapilar) {
        this.glicemiaCapilar = glicemiaCapilar;
    }

    public Integer getHgt() {
        return hgt;
    }

    public void setHgt(Integer hgt) {
        this.hgt = hgt;
    }
}
