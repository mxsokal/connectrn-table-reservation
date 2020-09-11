package com.connectrn.tablereservation.service;

import com.connectrn.tablereservation.config.ConfigProperties;
import com.connectrn.tablereservation.model.Restaurant;
import com.connectrn.tablereservation.model.Table;
import com.connectrn.tablereservation.repository.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.Set;

import static java.util.Objects.requireNonNull;

@Service
public class TableService {

    private final String restaurantName;
    private final RestaurantRepository repository;

    @Autowired
    public TableService(RestaurantRepository repository, ConfigProperties properties) {
        requireNonNull(properties, "properties is null");
        this.restaurantName = properties.getRestaurantName();
        this.repository = requireNonNull(repository, "repository is null");
    }

    public Set<Table> getTables(Table.Status status) throws ServiceException {
        Restaurant restaurant;

        restaurant = getRestaurant();
        return (status == null) ? restaurant.getTables() : restaurant.getTables(status);
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
}