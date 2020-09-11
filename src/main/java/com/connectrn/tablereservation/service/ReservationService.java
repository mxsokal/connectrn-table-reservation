package com.connectrn.tablereservation.service;

import com.connectrn.tablereservation.config.ConfigProperties;
import com.connectrn.tablereservation.model.UserInfo;
import com.connectrn.tablereservation.model.Reservation;
import com.connectrn.tablereservation.model.Restaurant;
import com.connectrn.tablereservation.model.Table;
import com.connectrn.tablereservation.repository.RestaurantRepository;
import com.connectrn.tablereservation.selector.TableSelector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.function.Predicate.not;
import static java.util.Objects.requireNonNull;

@Service
@Scope("session")
public class ReservationService {

    private static final Logger log = LoggerFactory.getLogger(ReservationService.class);

    private final String restaurantName;
    private final int maxCount;
    private final int maxCapacity;
    private final TableSelector tableSelector;
    private final RestaurantRepository repository;
    private final UserInfo userInfo;;

    @Autowired
    public ReservationService(TableSelector tableSelector, RestaurantRepository repository, ConfigProperties properties,
                              UserInfo userInfo) {
        requireNonNull(properties, "properties is null");
        this.restaurantName = properties.getRestaurantName();
        this.maxCount = properties.getMaxReservationCount();
        this.maxCapacity = properties.getMaxReservationCapacity();
        this.tableSelector = requireNonNull(tableSelector, "tableSelector is null");
        this.repository = requireNonNull(repository, "repository is null");
        this.userInfo = requireNonNull(userInfo, "userInfo is null");
    }

    public Reservation makeReservation(int capacity) throws ReservationException, ServiceException {
        Restaurant restaurant;
        Set<Reservation> reservations;
        Set<Reservation> existingReservations;
        Set<Table> tables;
        Reservation reservation;

        checkCapacity(capacity);
        restaurant = getRestaurant();
        reservations = restaurant.getReservations();
        existingReservations = new HashSet<>(reservations);
        if (reservations.size() == maxCount) {
            throw new ReservationException("restaurant is fully reserved");
        }
        tables = selectTables(restaurant, capacity);
        reservation = createReservation(tables, capacity);
        reservations.add(reservation);
        save(restaurant);
        return reservations.stream()
                .filter(not(existingReservations::contains))
                .findFirst().orElseThrow(() -> new IllegalStateException("unable to find saved reservation"));
    }

    private void checkCapacity(int capacity) throws ReservationException {
        if (capacity <= 0) {
            throw new IllegalArgumentException("capacity value " + capacity + " is not positive");
        }
        if (capacity > maxCapacity) {
            throw new ReservationException("capacity value " + capacity + " exceeds " + maxCapacity);
        }
    }

    private Set<Table> selectTables(Restaurant restaurant, int capacity) throws ReservationException {
        Set<Table> tables;
        Set<Table> result;

        tables = restaurant.getTables(Table.Status.FREE);
        result = tableSelector.select(tables, capacity);
        if (result.isEmpty()) {
            throw new ReservationException("unable to reserve tables for " + capacity + " capacity");
        }
        return result;
    }

    private Reservation createReservation(Set<Table> tables, int capacity) {
        Reservation result = new Reservation();

        result.setCapacity(capacity);
        result.addTables(tables);
        result.setUserName(userInfo.getName());
        return result;
    }

    public void removeReservation(long id) throws ReservationException, ServiceException {
        Restaurant restaurant;
        Set<Reservation> reservations;
        Reservation reservation;

        restaurant = getRestaurant();
        reservations = restaurant.getReservations();
        reservation = getReservation(reservations, id);
        reservation.removeTables();
        reservations.remove(reservation);
        save(restaurant);
    }

    private Reservation getReservation(Set<Reservation> reservations, Long id) throws ReservationException {
        Reservation reservation;

        reservation = reservations.stream()
                .filter(e -> Objects.equals(e.getId(), id))
                .findAny().orElse(null);
        if (reservation == null) {
            throw new ReservationException("unable to find reservation "+ id);
        }
        if ((!userInfo.isAdmin()) && (!Objects.equals(userInfo.getName(), reservation.getUserName()))) {
            throw new ReservationException("unable to remove reservation "+ id);
        }
        return reservation;
    }

    private Restaurant getRestaurant() throws ServiceException {
        Restaurant restaurant;

        try {
            restaurant = repository.findByName(restaurantName);
        } catch (DataAccessException e) {
            throw new ServiceException("unable to obtain restaurant", e);
        }
        if (restaurant == null) {
            throw new ServiceException("unable to find restaurant");
        }
        return restaurant;
    }

    private void save(Restaurant restaurant) throws ServiceException {
        try {
            repository.saveAndFlush(restaurant);
        } catch (ObjectOptimisticLockingFailureException e) {
            throw new ConcurrentDataAccessException(e);
        } catch (DataAccessException e) {
            throw new ServiceException("unable to save changes", e);
        }
    }

    public Set<Reservation> getReservations() throws ServiceException {
        Restaurant restaurant;

        restaurant = getRestaurant();
        return (userInfo.isAdmin())
                ? restaurant.getReservations()
                : restaurant.getReservations().stream()
                    .filter(e -> Objects.equals(userInfo.getName(), e.getUserName()))
                    .collect(Collectors.toSet());
    }

}