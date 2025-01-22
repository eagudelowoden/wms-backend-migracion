package com.woden.wms_backend.controllers;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.woden.wms_backend.services.BaseService;
import java.util.List;

public class BaseController<T, ID> {
    protected final BaseService<T, ID> service;

    public BaseController(BaseService<T, ID> service) {
        this.service = service;
    }

    @PostMapping
    public T save (@RequestBody T entidad) {
        return service.save(entidad);
    }

    @PutMapping("/{id}")
    public T update(@RequestBody T entidad, @PathVariable ID id) {
        return service.update(entidad, id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable ID id) {
        service.delete(id);
    }

    @GetMapping("/{id}")
    public T getById(@PathVariable ID id) {
        return service.getById(id);
    }

    @GetMapping
    public List<T> getAll() {
        return service.getAll();
    }
}
