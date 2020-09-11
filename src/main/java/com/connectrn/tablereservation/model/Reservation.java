package com.connectrn.tablereservation.model;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
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
    @NotBlank(message = "userName is blank")
    @Size(max = UserInfo.MAX_NAME_LENGTH, message = "userName is too long")
    private String userName;

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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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
    public boolean equals(Object o) {
        Reservation reservation;
        boolean result = true;

        if (o != this) {
            result = false;
            if (o instanceof Reservation) {
                reservation = (Reservation) o;
                result = Objects.equals(id, reservation.getId());
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
        return "Reservation{" +
                "id=" + id +
                ", capacity=" + capacity +
                ", tables=" + tables +
                ", userName='" + userName + '\'' +
                '}';
    }

}