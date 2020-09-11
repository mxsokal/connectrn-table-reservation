package com.connectrn.tablereservation.service;

public final class ConcurrentDataAccessException extends ServiceException {

    public ConcurrentDataAccessException() {
        super();
    }

    public ConcurrentDataAccessException(String message) {
        super(message);
    }

    public ConcurrentDataAccessException(Throwable cause) {
        super(cause);
    }

    public ConcurrentDataAccessException(String message, Throwable cause) {
        super(message, cause);
    }

}