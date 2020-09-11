package com.connectrn.tablereservation.selector;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import static java.util.Objects.requireNonNull;
import static java.util.Collections.unmodifiableSet;
import static java.lang.Math.addExact;
import static java.lang.Math.multiplyExact;

public final class Layout {

    public static Layout valueOf(Map<Integer, Integer> countByCapacity) {
        Map<Integer, Integer> layoutCountByCapacity = new HashMap<>();
        int totalCapacity = 0;
        Integer count;

        requireNonNull(countByCapacity, "countByCapacity is null");
        for (Integer capacity : countByCapacity.keySet()) {
            count = countByCapacity.get(capacity);
            if ((capacity == null) || (capacity <= 0)) {
                throw new IllegalArgumentException("invalid capacity value " + capacity);
            } else if ((count == null) || (count < 0)) {
                throw new IllegalArgumentException("invalid count value " + capacity + " for capacity " + capacity);
            }
            if (count > 0) {
                totalCapacity = addExact(totalCapacity, multiplyExact(capacity, count));
                layoutCountByCapacity.put(capacity, count);
            }
        }
        if (totalCapacity == 0) {
            throw new IllegalArgumentException("result total capacity is zero");
        }
        return new Layout(layoutCountByCapacity, totalCapacity);
    }

    public static Layout ofTable(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("invalid capacity value " + capacity);
        }
        return new Layout(Map.of(capacity, 1), capacity);
    }

    private final Map<Integer, Integer> countByCapacity;
    private final Set<Integer> capacities;
    private final int totalCapacity;

    private Layout(Map<Integer, Integer> countByCapacity, int totalCapacity) {
        this.countByCapacity = countByCapacity;
        this.capacities = unmodifiableSet(countByCapacity.keySet());
        this.totalCapacity = totalCapacity;
    }

    public Set<Integer> getCapacities() {
        return capacities;
    }

    public int getCount(int capacity) {
        if (!countByCapacity.containsKey(capacity)) {
            throw new IllegalArgumentException("no tables of capacity " + capacity + " is found");
        }
        return countByCapacity.get(capacity);
    }

    public int getTotalCapacity() {
        return totalCapacity;
    }

    public Layout addTable(int capacity) {
        Layout layout;

        if (capacity <= 0) {
            throw new IllegalArgumentException("invalid capacity value " + capacity);
        }
        layout = new Layout(new HashMap<>(countByCapacity), addExact(totalCapacity, capacity));
        layout.countByCapacity.merge(capacity, 1, (o, n) -> o + 1);
        return layout;
    }

    public Layout removeTable(int capacity) {
        Layout layout;

        if (!countByCapacity.containsKey(capacity)) {
            throw new IllegalArgumentException("no tables of capacity " + capacity + " is found");
        } else if (capacity == totalCapacity) {
            throw new IllegalArgumentException("result total capacity is zero");
        }
        layout = new Layout(new HashMap<>(countByCapacity), totalCapacity - capacity);
        layout.countByCapacity.merge(capacity, 1, (o, n) -> (o == 1) ? null : o - 1);
        return layout;
    }

    @Override
    public boolean equals(Object o) {
        Layout layout;
        boolean result = true;

        if (o != this) {
            result = false;
            if (o instanceof Layout) {
                layout = (Layout) o;
                result = countByCapacity.equals(layout.countByCapacity);
            }
        }
        return result;
    }

    @Override
    public int hashCode() {
        return countByCapacity.hashCode();
    }

    @Override
    public String toString() {
        return "Layout{" +
                "countByCapacity=" + countByCapacity +
                ", totalCapacity=" + totalCapacity +
                '}';
    }

}