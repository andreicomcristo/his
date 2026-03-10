package br.com.his.care.inpatient.dto;

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

import java.util.ArrayList;
import java.util.List;

public class LeitoMapaAreaDto {

    private Long areaId;
    private String areaNome;
    private int totalLeitos;
    private int leitosFixos;
    private int leitosVirtuais;
    private int leitosLivres;
    private int leitosOcupados;
    private int ocupadosObservacao;
    private int ocupadosInternacao;
    private Double taxaNominalPercentual;
    private List<LeitoMapaItemDto> leitos = new ArrayList<>();

    public Long getAreaId() {
        return areaId;
    }

    public void setAreaId(Long areaId) {
        this.areaId = areaId;
    }

    public String getAreaNome() {
        return areaNome;
    }

    public void setAreaNome(String areaNome) {
        this.areaNome = areaNome;
    }

    public int getTotalLeitos() {
        return totalLeitos;
    }

    public void setTotalLeitos(int totalLeitos) {
        this.totalLeitos = totalLeitos;
    }

    public int getLeitosFixos() {
        return leitosFixos;
    }

    public void setLeitosFixos(int leitosFixos) {
        this.leitosFixos = leitosFixos;
    }

    public int getLeitosVirtuais() {
        return leitosVirtuais;
    }

    public void setLeitosVirtuais(int leitosVirtuais) {
        this.leitosVirtuais = leitosVirtuais;
    }

    public int getLeitosLivres() {
        return leitosLivres;
    }

    public void setLeitosLivres(int leitosLivres) {
        this.leitosLivres = leitosLivres;
    }

    public int getLeitosOcupados() {
        return leitosOcupados;
    }

    public void setLeitosOcupados(int leitosOcupados) {
        this.leitosOcupados = leitosOcupados;
    }

    public int getOcupadosObservacao() {
        return ocupadosObservacao;
    }

    public void setOcupadosObservacao(int ocupadosObservacao) {
        this.ocupadosObservacao = ocupadosObservacao;
    }

    public int getOcupadosInternacao() {
        return ocupadosInternacao;
    }

    public void setOcupadosInternacao(int ocupadosInternacao) {
        this.ocupadosInternacao = ocupadosInternacao;
    }

    public Double getTaxaNominalPercentual() {
        return taxaNominalPercentual;
    }

    public void setTaxaNominalPercentual(Double taxaNominalPercentual) {
        this.taxaNominalPercentual = taxaNominalPercentual;
    }

    public List<LeitoMapaItemDto> getLeitos() {
        return leitos;
    }

    public void setLeitos(List<LeitoMapaItemDto> leitos) {
        this.leitos = leitos;
    }
}
