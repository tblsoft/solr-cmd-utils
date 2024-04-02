package com.quasiris.qsc.dto;

public class StatusUpdateDTO {
    private Boolean cancelInitiated = Boolean.FALSE;

    public Boolean getCancelInitiated() {
        return cancelInitiated;
    }

    public void setCancelInitiated(Boolean cancelInitiated) {
        this.cancelInitiated = cancelInitiated;
    }
}
