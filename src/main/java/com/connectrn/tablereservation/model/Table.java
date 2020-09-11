package com.connectrn.tablereservation.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.validation.constraints.Positive;
import javax.persistence.*;
import java.util.Objects;

@Entity
@javax.persistence.Table(name = "guest_table")
public class Table {

    public enum Status {FREE, RESERVED}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Positive(message = "capacity is not positive")
    private int capacity;
    @JsonIgnore
    @ManyToOne
    private Reservation reservation;

    public Table() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }

    public Status getStatus() {
        return (reservation == null) ? Status.FREE : Status.RESERVED;
    }

    @Override
    public boolean equals(Object o) {
        Table table;
        boolean result = true;

        if (o != this) {
            result = false;
            if (o instanceof Table) {
                table = (Table) o;
                result = Objects.equals(id, table.getId());
            }
        }
        return result;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Table{" +
                "id=" + id +
                ", capacity=" + capacity +
                ", reservation=" + ((reservation != null) ? reservation.getId() : null) +
                '}';
    }

}