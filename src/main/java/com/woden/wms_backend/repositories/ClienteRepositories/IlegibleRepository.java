package com.woden.wms_backend.repositories.ClienteRepositories;

import java.sql.Timestamp;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.woden.wms_backend.models.Entity.IlegibleModel;
import com.woden.wms_backend.repositories.BaseRepository;

import jakarta.transaction.Transactional;

@Repository
public interface IlegibleRepository extends BaseRepository<IlegibleModel, Integer> {
  @Modifying
  @Transactional
  @Query(value = "EXEC pa_InsertUnreadable :serial, :mac, :serialId, :fecha", nativeQuery = true)
  int insertIlegible(
      @Param("serial") String serial,
      @Param("mac") String mac,
      @Param("serialId") Integer serialId,
      @Param("fecha") Timestamp fecha);
}
