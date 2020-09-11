package com.connectrn.tablereservation.service;

public final class ReservationException extends Exception {

    public ReservationException() {
        super();
    }

    public ReservationException(String message) {
        super(message);
    }

    public ReservationException(Throwable cause) {
        super(cause);
    }

    public ReservationException(String message, Throwable cause) {
        super(message, cause);
    }

}