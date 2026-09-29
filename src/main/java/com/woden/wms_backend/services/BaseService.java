package com.woden.wms_backend.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;

import com.woden.wms_backend.models.Activable;

public abstract class BaseService<T, ID> {
    @Autowired
    protected JpaRepository<T, ID> repository;

    public T save(T entity) {
        return repository.save(entity);
    }

    public T update(T entity, ID id) {
        if (repository.existsById(id)) {
            return repository.save(entity);
        }
        throw new RuntimeException("Entidad no encontrada");
    }

    public void delete(ID id) {
        T entity = repository.findById(id).orElseThrow(() -> new RuntimeException("Entidad no encontrada"));
        
        // Comprobamos si la entidad tiene el campo 'activo' y cambiamos su valor a 0
        if (entity instanceof Activable) {
            Activable activableEntity = (Activable) entity;
            activableEntity.setActivo(false); // Cambiamos 'activo' de 1 a 0 para marcar como eliminado lógicamente
            repository.save(entity); // Guardamos la entidad con el cambio
        } else {
            throw new RuntimeException("Entidad no tiene soporte para eliminación lógica");
        }
    }
    

    public T getById(ID id) {
        return repository.findById(id).orElse(null);
    }

    public List<T> getAll() {
        return repository.findAll();
    }
}
