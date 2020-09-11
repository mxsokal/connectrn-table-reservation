package com.connectrn.tablereservation.model;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;
import static java.util.stream.Collectors.toSet;
import static java.util.Objects.requireNonNull;

@Entity
public class Restaurant {

    public static final int MAX_NAME_LENGTH = 128;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "name is blank")
    @Size(max = MAX_NAME_LENGTH, message = "name is too long")
    private String name;
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "restaurant_id")
    private Set<Table> tables = new HashSet<>();
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "restaurant_id")
    private Set<Reservation> reservations = new HashSet<>();
    @Version
    @Column(name = "update_ver_no")
    private long version;

    public Restaurant() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Table> getTables() {
        return tables;
    }

    public void setTables(Set<Table> tables) {
        this.tables = tables;
    }

    public Set<Reservation> getReservations() {
        return reservations;
    }

    public void setReservations(Set<Reservation> reservations) {
        this.reservations = reservations;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public Set<Table> getTables(Table.Status status) {
        requireNonNull(status, "status is null");
        return tables.stream()
                .filter(e -> e.getStatus() == status)
                .collect(toSet());
    }

    @Override
    public String toString() {
        return "Restaurant{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", tables=" + tables +
                ", reservations=" + reservations +
                ", version=" + version +
                '}';
    }

}