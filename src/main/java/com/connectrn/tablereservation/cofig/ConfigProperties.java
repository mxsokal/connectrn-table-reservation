package com.connectrn.tablereservation.cofig;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Component
@Validated
@ConfigurationProperties(prefix = "app")
public class ConfigProperties {

    @NotNull
    private String restaurantName;
    @Positive
    private int maxReservationCount;
    @Positive
    private int maxReservationCapacity;

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public int getMaxReservationCount() {
        return maxReservationCount;
    }

    public void setMaxReservationCount(int maxReservationCount) {
        this.maxReservationCount = maxReservationCount;
    }

    public int getMaxReservationCapacity() {
        return maxReservationCapacity;
    }

    public void setMaxReservationCapacity(int maxReservationCapacity) {
        this.maxReservationCapacity = maxReservationCapacity;
    }

    @Override
    public String toString() {
        return "ConfigProperties{" +
                "restaurantName='" + restaurantName + '\'' +
                ", maxReservationCount=" + maxReservationCount +
                ", maxReservationCapacity=" + maxReservationCapacity +
                '}';
    }
}
