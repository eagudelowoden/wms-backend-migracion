package com.woden.wms_backend.services.ClienteServices;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.woden.wms_backend.dto.AccesorioSearchDTO;
import com.woden.wms_backend.dto.SendPalletDTO;
import com.woden.wms_backend.dto.clientDTO.AccesorioSeparateDTO;
import com.woden.wms_backend.dto.clientDTO.SendAccesoryDTO;
import com.woden.wms_backend.models.Entity.AccesorioModel;
import com.woden.wms_backend.repositories.ClienteRepositories.AccesorioRepository;
import com.woden.wms_backend.repositories.ClienteRepositories.PalletRepository;
import com.woden.wms_backend.services.BaseService;

import jakarta.transaction.Transactional;

@Service
public class AccesorioService extends BaseService<AccesorioModel, Integer> {
  public AccesorioService(AccesorioRepository repository) {

  }

  @Autowired
  private AccesorioRepository accesorioRepository;
  @Autowired
  private PalletRepository palletRepository;

  @Transactional
  public void guardarAccesorios(List<AccesorioModel> accesorios) {
    for (AccesorioModel a : accesorios) {
      accesorioRepository.insertarAccesorio(
          a.getCodigoSapId(),
          a.getTipoAccesorio(),
          a.getTipoOrigenId(),
          a.getOrigenId(),
          a.getPalletId(),
          a.getEstadoId(),
          a.getDocumento(),
          a.getObservacion(),
          a.getGuia(),
          a.getUsuarioId(),
          a.getCaja(),
          a.getPrealertaId(),
          a.getCruce());
    }
  }

  public List<AccesorioSearchDTO> buscarAccesoriosPorPallet(int palletId) {
    List<Object[]> resultados = accesorioRepository.buscarPorPalletId(palletId);
    List<AccesorioSearchDTO> accesorios = new ArrayList<>();

    for (Object[] fila : resultados) {
      AccesorioSearchDTO dto = new AccesorioSearchDTO();
      dto.setCodigo((String) fila[0]);
      dto.setDescripcion((String) fila[1]);
      dto.setTipoAccesorio((String) fila[2]);
      dto.setOrigen((String) fila[3]);
      dto.setDocumento((String) fila[4]);
      dto.setGuia((String) fila[5]);
      dto.setCaja(fila[6] != null ? fila[6].toString() : null);
      accesorios.add(dto);
    }

    return accesorios;
  }

  public int eliminarAccesorio(Integer palletId, Integer cantidad, Integer codigoSapId) {
    return accesorioRepository.deleteAccesorio(palletId, cantidad, codigoSapId);
  }

  public boolean cerrarPalletAccesorio(SendPalletDTO dto) {
    int filas = 0; // OUT simbólico
    accesorioRepository.sendAccesory(dto.getPalletId(), dto.getDestinoId(), filas);
    palletRepository.sendPallet(dto.getDestinoId(), dto.getTipologiaId(), dto.getPosicionId(), 1, dto.getPalletId(), 0);
    return true;
  }

  public void sendAccesory(SendAccesoryDTO dto) {
    accesorioRepository.sendAccesory(dto.getPalletId(), dto.getEstadoId(), 0);
  }

  public void unificarAccesorio(Integer palletIdDestino, List<Integer> palletIds) {
    for (Integer palletId : palletIds) {
      accesorioRepository.unifyAccesory(palletIdDestino, palletId);
    }
  }

  public void updatePalletAccesory(List<Integer> accesoriosId, Integer palletId) {
    accesoriosId.forEach(accesorioId -> accesorioRepository.updatePalletAccesory(accesorioId, palletId));
  }

  public List<AccesorioSeparateDTO> searchSeparatePalletAccesory(Integer cantidad, Integer palletId) {
    List<Object[]> resultados = accesorioRepository.searchSeparatePalletAccesory(cantidad, palletId);
    List<AccesorioSeparateDTO> accesorios = new ArrayList<>();

    for (Object[] fila : resultados) {
      AccesorioSeparateDTO dto = new AccesorioSeparateDTO();
      dto.setId((Integer) fila[0]);
      dto.setCodigo((String) fila[1]);
      dto.setDescripcion((String) fila[2]);
      dto.setTipologia((String) fila[3]);
      accesorios.add(dto);
    }

    return accesorios;
  }

    public List<Map<String, Object>> searchPackingCodigoSapAccesory(String estado, Integer palletId, Integer codigoSapId) {
        List<Object[]> resultados = accesorioRepository.searchPackingCodigoSapAccesory(estado, palletId, codigoSapId);
        List<Map<String, Object>> formattedResults = new ArrayList<>();

        for (Object[] row : resultados) {
            Map<String, Object> map = new HashMap<>();

            map.put("codigo", row[0] != null ? row[0].toString() : "");
            map.put("descripcion", row[1] != null ? row[1].toString() : "");
            map.put("familia", row[2] != null ? row[2].toString() : "");
            map.put("nuevos", row[3] != null ? row[3].toString() : "0");
            map.put("reacondicionados", row[4] != null ? row[4].toString() : "0");


            formattedResults.add(map);
        }

        return formattedResults;
    }
    public int getPackedAccesoriesSerials(String codigoSap, String tipoAccesorio, Integer palletId) {
        try {
            List<Object[]> resultados = accesorioRepository.GetPackedAccesoriesSerials(codigoSap, tipoAccesorio, palletId);
            if (resultados == null) {
                return 0;
            }
            int count = resultados.size();
            return count;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }





    public List<Map<String, Object>> searchPackedAccesory(String estado, String serial) {
        List<Object[]> resultados = accesorioRepository.SearchPackedAccesory(estado, serial);
        List<Map<String, Object>> formattedResults = new ArrayList<>();

        for (Object[] row : resultados) {
            Map<String, Object> map = new HashMap<>();

            map.put("codigo", row[0] != null ? row[0].toString() : "");
            map.put("descripcion", row[1] != null ? row[1].toString() : "");
            map.put("nuevos", row[2] != null ? row[2].toString() : "0");
            map.put("reacondicionados", row[3] != null ? row[3].toString() : "0");
            /*hola mundo*/
            formattedResults.add(map);
        }

        return formattedResults;
    }
    public void UpdateSerialAccesory(String serialNuevo,
                              String serialAnterior) {
        Integer filas = 4;
        accesorioRepository.UpdateSerialAccesory(serialNuevo, serialAnterior, filas);
    }

    public int updatePackingBatch(List<String[]> packingDataList) {
        int status = 0;
        for (String[] data : packingDataList) {
            try {
                int filas = accesorioRepository.updatePackingAccesory(
                        data[0],                         // codigoSap
                        data[1],                         // tipo
                        data[2],                         // estado (EMPAQUE)
                        Integer.parseInt(data[3]),        // destino
                        data[4],                         // serial
                        Integer.parseInt(data[5]),        // palletId
                        Integer.parseInt(data[6])         // cantidad
                );
                if (filas > 0) status = 1;
            } catch (Exception e) {
                System.err.println("❌ Error al ejecutar pa_UpdatePackingAccesory: " + e.getMessage());
                return 0;
            }
        }
        return status;
    }


}
