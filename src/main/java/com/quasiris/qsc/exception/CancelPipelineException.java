package com.quasiris.qsc.exception;

public class CancelPipelineException extends RuntimeException {
    public CancelPipelineException(String message) {
        super(message);
    }

    public CancelPipelineException(String message, Throwable cause) {
        super(message, cause);
    }
}
