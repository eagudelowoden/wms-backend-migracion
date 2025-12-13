package com.woden.wms_backend.services.ClienteServices;

import com.woden.wms_backend.dto.AccesorioSearchDTO;
import com.woden.wms_backend.dto.SendPalletDTO;
import com.woden.wms_backend.dto.clientDTO.AccesorioSeparateDTO;
import com.woden.wms_backend.dto.clientDTO.SendAccesoryDTO;
import com.woden.wms_backend.models.Entity.AccesorioModel;
import com.woden.wms_backend.repositories.ClienteRepositories.AccesorioRepository;
import com.woden.wms_backend.repositories.ClienteRepositories.PalletRepository;
import com.woden.wms_backend.services.BaseService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AccesorioService extends BaseService<AccesorioModel, Integer> {
    public AccesorioService(AccesorioRepository repository) {

    }

    @Autowired
    private org.springframework.jdbc.core.JdbcTemplate jdbcTemplate; // 👈 agrega esto arriba, junto con tus otros

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

    public List<Map<String, Object>> searchPackingCodigoSapAccesory(String estado, Integer palletId,
                                                                    Integer codigoSapId) {
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
        String sql = "EXEC pa_GetPackedAccesoriesSerials ?, ?, ?";
        try {
            // long start = System.currentTimeMillis();

            List<Map<String, Object>> resultados = jdbcTemplate.queryForList(sql, codigoSap, tipoAccesorio, palletId);
            int total = resultados.size();

            return total;
        } catch (Exception e) {
            System.err.println("❌ Error en getPackedAccesoriesSerials: " + e.getMessage());
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
            /* hola mundo */
            formattedResults.add(map);
        }

        return formattedResults;
    }

    public void UpdateSerialAccesory(String serialNuevo,
                                     String serialAnterior) {
        Integer filas = 4;
        accesorioRepository.UpdateSerialAccesory(serialNuevo, serialAnterior, filas);
    }

    @Transactional
    public int updatePackingBatch(List<String[]> packingDataList) {
        String sql = "EXEC pa_UpdatePackingAccesory ?, ?, ?, ?, ?, ?, ?";

        int batchSize = 200; // 🔹 puedes ajustar (100–500 según tu entorno)

        try {
            for (int i = 0; i < packingDataList.size(); i += batchSize) {
                List<String[]> batch = packingDataList.subList(i, Math.min(i + batchSize, packingDataList.size()));

                jdbcTemplate.batchUpdate(sql, batch, batch.size(), (ps, data) -> {
                    ps.setString(1, data[0]); // CodigoSap
                    ps.setString(2, data[1]); // Tipo
                    ps.setString(3, data[2]); // Estado
                    ps.setInt(4, Integer.parseInt(data[3])); // DestinoId
                    ps.setString(5, data[4]); // Serial
                    ps.setInt(6, Integer.parseInt(data[5])); // PalletId
                    ps.setInt(7, Integer.parseInt(data[6])); // Cantidad
                });

                // total += batch.size();
            }

            // System.out.println("✅ Batch ejecutado correctamente. Total registros
            // procesados: " + total);
            return 1;

        } catch (Exception e) {
            System.err.println("❌ Error durante batch update: " + e.getMessage());
            return 0;
        }
    }

    public List<Map<String, Object>> SearchAllPackedAccesory(String estado) {
        List<Object[]> resultados = accesorioRepository.SearchAllPackedAccesory(estado);
        List<Map<String, Object>> formattedResults = new ArrayList<>();

        for (Object[] row : resultados) {
            Map<String, Object> map = new HashMap<>();

            map.put("codigo", row[0] != null ? row[0].toString() : "");
            map.put("descripcion", row[1] != null ? row[1].toString() : "");
            map.put("nuevos", row[2] != null ? row[2].toString() : "0");
            map.put("reacondicionados", row[3] != null ? row[3].toString() : "0");
            /* hola mundo */
            formattedResults.add(map);
        }

        return formattedResults;
    }

    public List<AccesorioModel> getModelDispatchAccesory(Integer palletId) {
        List<AccesorioModel> accesorios = new ArrayList<>();
        List<Object[]> results = accesorioRepository.getModelDispatchAccesory(palletId);
        for (Object[] obj : results) {
            AccesorioModel accesorio = new AccesorioModel();
            accesorio.setId((Integer) obj[0]);
            accesorio.setCodigoSapId((Integer) obj[1]);
            accesorio.setTipoAccesorio((String) obj[2]);
            accesorio.setTipoOrigenId((Integer) obj[3]);
            accesorio.setOrigenId((Integer) obj[4]);
            accesorio.setPalletId((Integer) obj[5]);
            accesorio.setEstadoLimpiezaId((Integer) obj[6]);
            accesorio.setDocumento((String) obj[7]);
            accesorio.setObservacion((String) obj[8]);
            accesorio.setGuia((String) obj[9]);
            accesorio.setFecha(obj[10] != null ? ((Timestamp) obj[10]).toString() : null);
            accesorio.setSerialEmpaque((String) obj[11]);
            accesorio.setCaja((Integer) obj[12]);
            accesorio.setFechaLimpieza((String) obj[13]);
            accesorio.setFechaEmpaque((String) obj[14]);
            accesorio.setUsuarioLimpiezaId((Integer) obj[15]);
            accesorios.add(accesorio);
        }
        return accesorios;
    }

    public Integer packOffPalletAccesory(Integer palletId) {
        try {
            accesorioRepository.packOffPalletAccesory(palletId);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    public List<AccesorioModel> getModelDispatchBoxAccesory(Integer palletId, List<Integer> cajasDespachoIds) {
        List<AccesorioModel> accesorios = new ArrayList<>();
        for (Integer cajaDespachoId : cajasDespachoIds) {
            List<Object[]> results = accesorioRepository.getModelDispatchBoxAccesory(palletId, cajaDespachoId);

            for (Object[] obj : results) {
                AccesorioModel accesorio = new AccesorioModel();
                accesorio.setId((Integer) obj[0]);
                accesorio.setCodigoSapId((Integer) obj[1]);
                accesorio.setTipoAccesorio((String) obj[2]);
                accesorio.setTipoOrigenId((Integer) obj[3]);
                accesorio.setOrigenId((Integer) obj[4]);
                accesorio.setPalletId((Integer) obj[5]);
                accesorio.setEstadoLimpiezaId((Integer) obj[6]);
                accesorio.setDocumento((String) obj[7]);
                accesorio.setObservacion((String) obj[8]);
                accesorio.setGuia((String) obj[9]);
                accesorio.setFecha(obj[10] != null ? ((Timestamp) obj[10]).toString() : null);
                accesorio.setSerialEmpaque((String) obj[11]);
                accesorio.setCaja((Integer) obj[12]);
                accesorio.setFechaLimpieza(obj[13] != null ? ((Timestamp) obj[13]).toString() : null);
                accesorio.setFechaEmpaque(obj[14] != null ? ((Timestamp) obj[14]).toString() : null);
                accesorios.add(accesorio);
            }
        }
        return accesorios;
    }

    public Integer packOffPalletAccesoryBox(Integer palletId, Integer cajaDespachoId) {
        try {
            accesorioRepository.packOffPalletAccesoryBox(palletId, cajaDespachoId);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    public List<Map<String, Object>> searchGroupAccesory(String codigoSap, String estado, String tipo) {
        List<Object[]> accesorios = accesorioRepository.searchGroupAccesory(codigoSap, estado, tipo);
        List<Map<String, Object>> lista = new ArrayList<>();

        if (accesorios == null || accesorios.isEmpty()) {
            return lista;
        }

        for (Object[] fila : accesorios) {
            Map<String, Object> item = new HashMap<>();
            item.put("codigo", fila[0] != null ? fila[0].toString() : "");
            item.put("descripcion", fila[1] != null ? fila[1].toString() : "");
            item.put("cantidad", fila[2] != null ? fila[2].toString() : "0");
            lista.add(item);
        }

        return lista;
    }



    public List<Map<String, Object>> searchCleaningAccesory(String codigoSap, String tipoAccesorio) {
        List<Object[]> results = accesorioRepository.searchCleaningAccesory(tipoAccesorio, codigoSap);
        List<Map<String, Object>> lista = new ArrayList<>();

        if (results == null || results.isEmpty()) return lista;

        for (Object[] fila : results) {
            Map<String, Object> item = new HashMap<>();
            item.put("codigo", fila[0] != null ? fila[0].toString() : "");
            item.put("descripcion", fila[1] != null ? fila[1].toString() : "");
            item.put("estadoLimpieza", fila[2] != null ? fila[2].toString() : "");
            item.put("esuarioLimpieza", fila[3] != null ? fila[3].toString() : "");
            item.put("codigoSapId", fila[4] != null ? fila[4].toString() : "");
            item.put("estadoLimpiezaId", fila[5] != null ? fila[5].toString() : "");
            item.put("usuarioLimpiezaId", fila[6] != null ? fila[6].toString() : "");
            lista.add(item);
        }

        return lista;
    }

    public void updateAccesory(Integer cantidad, Integer destinoId, Integer estadoLimpiezaId,
                               String codigo, String estado, String tipoAccesorio, Integer UsuarioLimpiezaId  ) {
        Integer filas = 4;
        accesorioRepository.updateAccesory(cantidad, destinoId,estadoLimpiezaId,codigo, estado,
                                          tipoAccesorio,UsuarioLimpiezaId, filas);
    }

    public List<Map<String, Object>> searchProcessAccesory(String estadoLimpieza) {
        List<Object[]> accesorios = accesorioRepository.searchProcessAccesory(estadoLimpieza);
        List<Map<String, Object>> lista = new ArrayList<>();

        if (accesorios == null || accesorios.isEmpty()) {
            return lista;
        }
        for (Object[] fila : accesorios) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", fila[0] != null ? fila[0].toString() : "");
            item.put("codigo", fila[1] != null ? fila[1].toString() : "");
            item.put("descripcion", fila[2] != null ? fila[2].toString() : "");
            item.put("estadoLimpieza", fila[3] != null ? fila[3].toString() : "");
            lista.add(item);
        }
        return lista;
    }


    public List<Map<String, Object>>  searchDeliveryAccesory(int palletId) {
        List<Object[]> resultados = accesorioRepository.searchDeliveryAccesory(palletId);
        List<Map<String, Object>> lista = new ArrayList<>();

        if (resultados == null || resultados.isEmpty()) {
            return lista;
        }
        for (Object[] fila : resultados) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", fila[0] != null ? fila[0].toString() : "");
            item.put("codigo", fila[1] != null ? fila[1].toString() : "");
            item.put("descripcion", fila[2] != null ? fila[2].toString() : "");
            lista.add(item);
        }
        return lista;
    }

    // CORRECCIÓN: Ordenamos los parámetros igual que en el Repo
    public void updateDeliveryAccesory(Integer id, Integer destinoId, Integer palletId) {
        // Pasamos exactamente los 3 que pide el repositorio en el orden correcto
        accesorioRepository.updateDeliveryAccesory(id, destinoId, palletId);
    }






}
