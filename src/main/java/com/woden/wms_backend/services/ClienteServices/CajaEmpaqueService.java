package com.woden.wms_backend.services.ClienteServices;

import com.woden.wms_backend.models.Entity.CajaEmpaqueModel;
import com.woden.wms_backend.repositories.ClienteRepositories.*;
import com.woden.wms_backend.services.BaseService;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.woden.wms_backend.dto.AbrirPalletDTO;
import com.woden.wms_backend.dto.ConfirmarPalletDTO;
import com.woden.wms_backend.dto.PalletDTO;
import com.woden.wms_backend.dto.SendPalletDTO;
import com.woden.wms_backend.dto.clientDTO.PalletStorageDTO;
import com.woden.wms_backend.models.Entity.PalletModel;
import com.woden.wms_backend.services.BaseService;
import com.woden.wms_backend.util.TypeMapper;
import com.woden.wms_backend.models.Entity.CajaEmpaqueModel;
import com.woden.wms_backend.dto.CajaEmpaqueDTO;


@Service
public class CajaEmpaqueService extends BaseService<CajaEmpaqueModel, Integer> {
@Autowired
private CajaEmpaqueRepository cajaEmpaqueRepository;

    public void updateStatusBoxPacking(Integer cajaEmpaqueId, Integer estadoId) {
        Integer filas = 0;
        cajaEmpaqueRepository.updateStatusBoxPacking(cajaEmpaqueId, estadoId, filas);
    }

    public List<CajaEmpaqueDTO> SearchReceivePacking(String numero, String pallet) {
        List<Object[]> results = cajaEmpaqueRepository.SearchReceivePacking(numero, pallet);

        return results.stream().map(obj -> {
            CajaEmpaqueDTO caja = new CajaEmpaqueDTO();
            caja.setId((Integer) obj[0]);        // ID
            caja.setNumero((String) obj[1]);     // NUMERO
            caja.setPallet((String) obj[2]);  // PALLET
            caja.setCantidad((Integer) obj[3]);  // CANTIDAD
            return caja;
        }).collect(Collectors.toList());
    }


}
