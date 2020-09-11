package com.connectrn.tablereservation.model;

import javax.persistence.*;
import javax.validation.constraints.Positive;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import static java.util.Objects.requireNonNull;

@Entity
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Positive(message = "capacity is not positive")
    private int capacity;
    @OneToMany(mappedBy = "reservation")
    private Set<Table> tables = new HashSet<>();

    public Reservation() {}

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

    public Set<Table> getTables() {
        return tables;
    }

    public void setTables(Set<Table> tables) {
        this.tables = tables;
    }

    public void addTables(Collection<Table> tables) {
        requireNonNull(tables, "tables is null");
        for (Table table : tables) {
            if (table == null) {
                throw new IllegalArgumentException("tables contains null");
            }
            this.tables.add(table);
            table.setReservation(this);
        }
    }

    public void removeTables() {
        tables.forEach(e -> e.setReservation(null));
        tables.clear();
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "id=" + id +
                ", capacity=" + capacity +
                ", tables=" + tables +
                '}';
    }

}