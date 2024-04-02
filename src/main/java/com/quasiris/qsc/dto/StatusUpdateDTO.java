package com.quasiris.qsc.dto;

public class StatusUpdateDTO {
    private Boolean cancelPipelineInitiated = Boolean.FALSE;

    public Boolean getCancelPipelineInitiated() {
        return cancelPipelineInitiated;
    }

    public void setCancelPipelineInitiated(Boolean cancelPipelineInitiated) {
        this.cancelPipelineInitiated = cancelPipelineInitiated;
    }
}
