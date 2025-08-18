package org.chapchap.be.global.exception;

public class GoogleApiException extends RuntimeException {
    private final int status;
    public GoogleApiException(int status, String msg) {
        super(msg);
        this.status = status;
    }
    public int getStatus() { return status; }
}