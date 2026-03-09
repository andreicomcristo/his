package br.com.his.assistencial.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "natureza_operacional_leito")
public class NaturezaOperacionalLeito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 40)
    private String codigo;

    @Column(nullable = false, length = 80)
    private String descricao;

    @Column(name = "considera_taxa_nominal", nullable = false)
    private boolean consideraTaxaNominal;

    @Column(name = "considera_taxa_operacional", nullable = false)
    private boolean consideraTaxaOperacional;

    @Column(name = "virtual_superlotacao", nullable = false)
    private boolean virtualSuperlotacao;

    @Column(nullable = false)
    private boolean ativo;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public boolean isConsideraTaxaNominal() {
        return consideraTaxaNominal;
    }

    public void setConsideraTaxaNominal(boolean consideraTaxaNominal) {
        this.consideraTaxaNominal = consideraTaxaNominal;
    }

    public boolean isConsideraTaxaOperacional() {
        return consideraTaxaOperacional;
    }

    public void setConsideraTaxaOperacional(boolean consideraTaxaOperacional) {
        this.consideraTaxaOperacional = consideraTaxaOperacional;
    }

    public boolean isVirtualSuperlotacao() {
        return virtualSuperlotacao;
    }

    public void setVirtualSuperlotacao(boolean virtualSuperlotacao) {
        this.virtualSuperlotacao = virtualSuperlotacao;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }
}
