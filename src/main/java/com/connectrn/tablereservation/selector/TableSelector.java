package com.connectrn.tablereservation.selector;

import com.connectrn.tablereservation.model.Table;

import java.util.Set;

public interface TableSelector {

    Set<Table> select(Set<Table> tables, int capacity);

}