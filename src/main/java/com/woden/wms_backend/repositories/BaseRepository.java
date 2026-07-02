package com.woden.wms_backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.woden.wms_backend.repositories.BaseRepository;
import org.springframework.data.repository.NoRepositoryBean;
@NoRepositoryBean
public interface BaseRepository<T, ID> extends JpaRepository<T, ID> {
    
}
