package org.chapchap.be.global.exception;

public class ExternalApiException extends RuntimeException {
    private final int status;
    public ExternalApiException(int status, String msg) {
        super(msg);
        this.status = status;
    }
    public int getStatus() { return status; }
}