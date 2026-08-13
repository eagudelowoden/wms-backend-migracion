package com.woden.wms_backend.repositories.WmsWdGeneral;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.woden.wms_backend.models.WmsWdGeneral.IntegracionApiKeyModel;

public interface IntegracionApiKeyRepository extends JpaRepository<IntegracionApiKeyModel, Integer> {

  Optional<IntegracionApiKeyModel> findByApiKeyHashAndActivoTrue(String apiKeyHash);
}
