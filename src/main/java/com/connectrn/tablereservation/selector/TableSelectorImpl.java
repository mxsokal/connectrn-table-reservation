package com.connectrn.tablereservation.selector;

import com.connectrn.tablereservation.model.Table;

import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toCollection;
import static java.util.Collections.emptySet;
import static java.util.Objects.requireNonNull;

public final class TableSelectorImpl implements TableSelector {

    @Override
    public Set<Table> select(Set<Table> tables, int capacity) {
        Set<Table> result = emptySet();
        Map<Integer, Set<Table>> tablesByCapacity;
        Layout reservedLayout;
        Layout layout;

        requireNonNull(tables, "tables is null");
        if (capacity <= 0) {
            throw new IllegalArgumentException("invalid capacity value " + capacity);
        }
        tablesByCapacity = getTablesByCapacity(tables);
        layout = getLayout(tablesByCapacity);
        if (layout.getTotalCapacity() >= capacity) {
            reservedLayout = select(layout, capacity, new HashMap<>());
            result = getTables(reservedLayout, tablesByCapacity);
        }
        return result;
    }

    private Map<Integer, Set<Table>> getTablesByCapacity(Set<Table> tables) {
        return tables.stream()
                .collect(
                        groupingBy(
                                Table::getCapacity,
                                toCollection(HashSet::new)
                        )
                );
    }

    private Layout getLayout(Map<Integer, Set<Table>> tablesByCapacity) {
        Map<Integer, Integer> countByCapacity = new HashMap<>();

        tablesByCapacity.forEach((k, v) -> countByCapacity.put(k, v.size()));
        return Layout.valueOf(countByCapacity);
    }

    private Layout select(Layout layout, int capacity, Map<Integer, Map<Layout, Layout>> cache) {
        Layout result;
        Layout subLayout;
        Layout subResult;
        int resultWastedCapacity = 0;
        int wastedCapacity;

        result = cache.computeIfAbsent(capacity, k -> new HashMap<>()).get(layout);
        if (result == null) {
            for (int tableCapacity : layout.getCapacities()) {
                if (tableCapacity == capacity) {
                    result = Layout.ofTable(tableCapacity);
                    break;
                } else if (tableCapacity > capacity) {
                    wastedCapacity = tableCapacity - capacity;
                    if ((result == null) || (resultWastedCapacity > wastedCapacity)) {
                        result = Layout.ofTable(tableCapacity);
                        resultWastedCapacity = wastedCapacity;
                    }
                } else {
                    subLayout = layout.removeTable(tableCapacity);
                    subResult = select(subLayout, capacity - tableCapacity, cache);
                    wastedCapacity = subResult.getTotalCapacity() - (capacity - tableCapacity);
                    if ((result == null) || (resultWastedCapacity > wastedCapacity)) {
                        result = subResult.addTable(tableCapacity);
                        resultWastedCapacity = wastedCapacity;
                    }
                }
            }
            cache.get(capacity).put(layout, result);
        }
        return result;
    }

    private Set<Table> getTables(Layout layout, Map<Integer, Set<Table>> tablesByCapacity) {
        Set<Table> result = new HashSet<>();
        Set<Table> tables;
        int count;

        for (int capacity : layout.getCapacities()) {
            tables = tablesByCapacity.get(capacity);
            count = layout.getCount(capacity);
            for (Table table : tables) {
                result.add(table);
                if ((--count) == 0) {
                    break;
                }
            }
        }
        return result;
    }

}