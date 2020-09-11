package com.connectrn.tablereservation.controller;

import com.connectrn.tablereservation.model.Reservation;
import com.connectrn.tablereservation.service.ConcurrentDataAccessException;
import com.connectrn.tablereservation.service.ReservationException;
import com.connectrn.tablereservation.service.ReservationService;
import com.connectrn.tablereservation.service.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.constraints.Positive;
import java.util.Set;

@RestController
@RequestMapping(path="/api/reservations")
@Validated
public class ReservationController {

    private final ReservationService service;

    @Autowired
    public ReservationController(ReservationService service) {
        this.service = service;
    }

    @GetMapping
    public Set<Reservation> getReservations() {
        Set<Reservation> reservations;

        try {
            reservations = service.getReservations();
        } catch (ServiceException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
        return reservations;
    }

    @PostMapping
    @Retryable(value = {ConcurrentDataAccessException.class}, maxAttempts = 3, backoff = @Backoff(delay = 3000))
    @ResponseStatus(code = HttpStatus.CREATED)
    public Reservation makeReservation(@RequestParam @Positive int capacity) throws ConcurrentDataAccessException {
        Reservation reservation;

        try {
            reservation = service.makeReservation(capacity);
        } catch (ReservationException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (ConcurrentDataAccessException e) {
            throw e;
        } catch (ServiceException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
        return reservation;
    }

    @DeleteMapping
    @Retryable(value = {ConcurrentDataAccessException.class}, maxAttempts = 3, backoff = @Backoff(delay = 3000))
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void deleteReservation(@RequestParam @Positive long id) throws ConcurrentDataAccessException {
        try {
            service.removeReservation(id);
        } catch (ReservationException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (ConcurrentDataAccessException e) {
            throw e;
        } catch (ServiceException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

}