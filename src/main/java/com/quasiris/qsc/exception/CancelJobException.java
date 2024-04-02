package com.quasiris.qsc.exception;

public class CancelJobException extends RuntimeException {
    public CancelJobException(String message) {
        super(message);
    }

    public CancelJobException(String message, Throwable cause) {
        super(message, cause);
    }
}
