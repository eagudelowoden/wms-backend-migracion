package com.woden.wms_backend.services.ClienteServices;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.woden.wms_backend.dto.AbrirPalletDTO;
import com.woden.wms_backend.dto.ConfirmarPalletDTO;
import com.woden.wms_backend.dto.PalletDTO;
import com.woden.wms_backend.models.Entity.PalletModel;
import com.woden.wms_backend.repositories.ClienteRepositories.IngresoRepository;
import com.woden.wms_backend.repositories.ClienteRepositories.PalletRepository;
import com.woden.wms_backend.services.BaseService;

@Service
public class PalletService extends BaseService<PalletModel, Integer> {

  private static final Logger logger = LoggerFactory.getLogger(PalletService.class);
  @Autowired
  private PalletRepository palletRepository;
  @Autowired
  private IngresoRepository ingresoRepository;

  // @Transactional
  // @Transactional(propagation = Propagation.REQUIRED)
  // public void createPallet(PalletModel p) {
  // logger.info("Insertando pallet con datos: {}", p);
  // Integer loteId = (p.getLoteId() != 0) ? p.getLoteId() : null;
  // palletRepository.insertPallet(
  // p.getNumero(),
  // p.getPosicionId(),
  // p.getCodigoSapId(),
  // p.getTipologiaId(),
  // p.getOrigenId(),
  // p.getDestinoId(),
  // p.getUsuarioId(),
  // loteId);
  // logger.info("Pallet insertado correctamente en la base de datos.");
  // }
  @Transactional(propagation = Propagation.REQUIRED)
  public void createPallet(PalletModel p) {
    try {
      logger.info("Insertando pallet con datos: {}", p);
      Integer loteId = (p.getLoteId() != 0) ? p.getLoteId() : null;
      palletRepository.insertPallet(
          p.getNumero(),
          p.getPosicionId(),
          p.getCodigoSapId(),
          p.getTipologiaId(),
          p.getOrigenId(),
          p.getDestinoId(),
          p.getUsuarioId(),
          loteId);
      logger.info("Pallet insertado correctamente en la base de datos.");
    } catch (Exception e) {
      logger.error("Error al insertar el pallet: ", e);
      throw e; // Relanzar la excepción para ver más detalles en la respuesta HTTP
    }
  }

  public List<PalletDTO> searchEntry(String numero, String destino, int usuarioId) {
    List<Object[]> results = palletRepository.searchEntry(numero, destino, usuarioId);

    return results.stream().map(obj -> {
      PalletDTO pallet = new PalletDTO();
      pallet.setId((Integer) obj[0]);
      pallet.setNumero((String) obj[1]);
      pallet.setCodigo((String) obj[2]);
      pallet.setCantidad((Integer) obj[3]); // Cantidad no está en PalletModel, pero sí en el DTO
      pallet.setTipologia((String) obj[4]);
      pallet.setLote((String) obj[5]);
      return pallet;
    }).collect(Collectors.toList());
  }

  public List<Map<String, Object>> searchTransitPallet(String numero) {
    List<Object[]> resultado = palletRepository.searchTransitPallet(numero);
    List<Map<String, Object>> pallets = new ArrayList<>();

    for (Object[] row : resultado) {
      Map<String, Object> map = new HashMap<>();
      map.put("id", row[0]);
      map.put("numero", row[1]);
      map.put("cantidad", row[2]);
      map.put("origen", row[3]);
      map.put("tipologia", row[4]);
      map.put("lote", row[5]);
      // map.put("accion1", "Confirmar");
      // map.put("accion2", "Abrir");
      pallets.add(map);
    }
    return pallets;
  }

  public void cerrarPallet(Integer palletId, Integer destinoId, Integer tipologiaId, Integer posicionId,
      Integer estado) {
    Integer filas = 0; // OUT simbólico
    palletRepository.sendPallet(destinoId, tipologiaId, posicionId, estado, palletId, filas);
  }

  public Integer getCount(Integer palletId, String tabla) {
    return palletRepository.getCountPallet(palletId, tabla);
  }

  public boolean deletePallet(Integer palletId, String tipoEquipo) {
    String tabla = tipoEquipo.equalsIgnoreCase("Serializable") ? "INGRESO" : "ACCESORIO";

    int cajas = palletRepository.getBoxCount(palletId, tabla);
    int registros = palletRepository.getCountEntries(palletId, tabla);

    if (cajas > 0 || registros > 0) {
      return false; // ❌ No eliminar, tiene dependencias
    }

    palletRepository.deletePallet(palletId); // ✅ Procedimiento de eliminación
    return true;
  }

  public boolean confirmarPalletTransito(ConfirmarPalletDTO dto) {
    ingresoRepository.sendIngreso(dto.getDestinoId(), dto.getTipologiaId(), dto.getUsuarioId(), dto.getPalletId(), 0,
        0);
    palletRepository.sendPallet(dto.getDestinoId(), dto.getTipologiaId(), dto.getPosicionId(), 1, dto.getPalletId(), 0);
    return true;
  }

  public boolean abrirPalletTransito(AbrirPalletDTO dto) {
    ingresoRepository.sendIngreso(dto.getOrigenId(), dto.getTipologiaId(), dto.getUsuarioId(), dto.getPalletId(), 0, 0);
    palletRepository.sendPallet(dto.getOrigenId(), dto.getTipologiaId(), dto.getPosicionId(), 1, dto.getPalletId(), 0);
    return true;
  }

  public void regularizarLotePallet(int palletId, int loteId, int usuarioIdMovimiento) {
    palletRepository.updateBatchPallet(palletId, loteId, usuarioIdMovimiento, 1);
  }

  public List<Map<String, Object>> searchReceivePallet(String numero, String destino, String tipo) {
    List<Object[]> results = palletRepository.searchReceivePallet(numero, destino, tipo);
    List<Map<String, Object>> pallets = new ArrayList<>();

    for (Object[] row : results) {
      Map<String, Object> pallet = new HashMap<>();
      pallet.put("id", row[0]);
      pallet.put("numero", row[1]);
      pallet.put("cantidad", row[2]);
      pallet.put("origen", row[3]);
      pallet.put("tipologia", row[4]);
      pallet.put("posicion", row[5]);
      pallet.put("accionConfirmar", "Confirmar");
      pallet.put("accionDevolver", "Devolver");
      pallets.add(pallet);
    }
    return pallets;
  }
}
