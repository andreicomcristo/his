package br.com.his.care.attendance.dto;

import java.util.List;

public class DestinoAssistencialAreaForm {

    private List<Long> areaIds;

    public List<Long> getAreaIds() {
        return areaIds;
    }

    public void setAreaIds(List<Long> areaIds) {
        this.areaIds = areaIds;
    }
}
