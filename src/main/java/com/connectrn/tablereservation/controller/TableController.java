package com.connectrn.tablereservation.controller;

import com.connectrn.tablereservation.model.Table;
import com.connectrn.tablereservation.service.ServiceException;
import com.connectrn.tablereservation.service.TableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Set;

@RestController
@RequestMapping(path="/api/tables")
@Validated
public class TableController {

    private final TableService service;

    @Autowired
    public TableController(TableService service) {
        this.service = service;
    }

    @GetMapping
    public Set<Table> getTables(@RequestParam(required = false) Table.Status status) {
        Set<Table> tables;

        try {
            tables = service.getTables(status);
        } catch (ServiceException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
        return tables;
    }

}