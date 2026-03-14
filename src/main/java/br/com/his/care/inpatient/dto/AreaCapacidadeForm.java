package br.com.his.care.inpatient.dto;

import java.util.ArrayList;
import java.util.List;

public class AreaCapacidadeForm {

    private List<Long> capacidadeIds = new ArrayList<>();

    public List<Long> getCapacidadeIds() {
        return capacidadeIds;
    }

    public void setCapacidadeIds(List<Long> capacidadeIds) {
        this.capacidadeIds = capacidadeIds;
    }
}


