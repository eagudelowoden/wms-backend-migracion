package com.woden.wms_backend.services.ClienteServices;

import org.springframework.stereotype.Service;

import com.woden.wms_backend.models.Entity.IlegibleModel;
import com.woden.wms_backend.repositories.ClienteRepositories.IlegibleRepository;
import com.woden.wms_backend.services.BaseService;

@Service
public class IlegibleService extends BaseService<IlegibleModel, Integer> {
    public IlegibleService(IlegibleRepository ilegibleRepository) {
    }

}
