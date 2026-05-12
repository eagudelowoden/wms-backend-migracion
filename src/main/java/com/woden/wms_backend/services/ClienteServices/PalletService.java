package com.woden.wms_backend.services.ClienteServices;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import com.woden.wms_backend.dto.*;
import com.woden.wms_backend.repositories.ClienteRepositories.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.woden.wms_backend.dto.clientDTO.PalletStorageDTO;
import com.woden.wms_backend.dto.ModeloPalletDto;
import com.woden.wms_backend.dto.SendModelsDto;
import com.woden.wms_backend.models.Entity.PalletModel;
import com.woden.wms_backend.services.BaseService;
import com.woden.wms_backend.util.TypeMapper;

@Service
public class PalletService extends BaseService<PalletModel, Integer> {

  private static final Logger logger = LoggerFactory.getLogger(PalletService.class);

  @Autowired
  private PalletRepository palletRepository;
  @Autowired
  private IngresoRepository ingresoRepository;
  @Autowired
  private CodigoSapRepository codigoSapRepository;
  @Autowired
  private MaestroRepository maestroRepository;

  @Autowired
  private PosicionService posicionService;
  @Autowired
  private PosicionRepository posicionRepository;

  private static final Logger log = LoggerFactory.getLogger(PalletService.class);




  public PalletModel getModel(int id) {
    List<Object[]> results = palletRepository.getPalletById(id);

    if (results.isEmpty()) {
      return null;
    }

    Object[] row = results.get(0);
    PalletModel pallet = new PalletModel();

    pallet.setId((Integer) row[0]);
    pallet.setNumero((String) row[1]);
    pallet.setPosicionId((Integer) row[2]);
    pallet.setPosicion((String) row[3]);
    pallet.setTipologiaId((Integer) row[4]);
    pallet.setTipologia((String) row[5]);
    pallet.setCodigoSapId((Integer) row[6]);
    pallet.setCodigoSap((String) row[7]);
    pallet.setDescripcion((String) row[8]);
    pallet.setOrigenId((Integer) row[9]);
    pallet.setOrigen((String) row[10]);
    pallet.setDestinoId((Integer) row[11]);
    pallet.setActivo(TypeMapper.toBoolean(row[12]));
    pallet.setUsuario((String) row[13]);
    pallet.setLoteId((Integer) row[14]);
    pallet.setFecha((String) row[15]);
    pallet.setMultimodelo(TypeMapper.toBoolean(row[16]));
    pallet.setCantidadCaja((Integer) row[17]);
    pallet.setSmartCard(TypeMapper.toBoolean(row[18]));

    return pallet;
  }

  @Transactional(propagation = Propagation.REQUIRED)
  public String createPallet(PalletModel p, Boolean kitEntryOn) {
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
    String numeroPallet = palletRepository.getLastInsertedPalletNumber();

    if (kitEntryOn) {
      String codigoSap = p.getCodigoSapId().toString();
      List<Integer> familyId = codigoSapRepository.getFamilyId(codigoSap);
      String newFamilyNumber = String.valueOf(maestroRepository.getFamilyNumberPallet(codigoSap) + 1);
      int filas = 0;
      maestroRepository.addCountPalletFamily(newFamilyNumber, familyId.get(0), filas);
    }

    logger.info("[createPallet] Pallet creado: {}", numeroPallet);
    return numeroPallet;
  }

  @Transactional
  public Map<String, Object> reservePallet(Integer origenId, Integer destinoId, Integer usuarioId, String zonaHoraria) {
    // En vez de ZonedDateTime, usa LocalDateTime directo en la zona horaria
    LocalDateTime ahora = LocalDateTime.now(ZoneId.of(zonaHoraria));
    String minutoActual = ahora.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    logger.debug("[reservePallet] zonaHoraria={}, minutoActual={}", zonaHoraria, minutoActual);
    String numero = palletRepository.incrementarYObtenerConsecutivo(minutoActual);
    Integer defaultCodigoSapId = codigoSapRepository.findAll(
        org.springframework.data.domain.PageRequest.of(0, 1)).getContent().get(0).getId();
    List<String> tipologias = maestroRepository.getListByTipo("tipologias");
    Integer defaultTipologiaId = (tipologias != null && !tipologias.isEmpty())
        ? maestroRepository.getIdMaster(tipologias.get(0), "tipologias").get(0)
        : 1;
    PalletModel pallet = new PalletModel();
    pallet.setNumero(numero);
    pallet.setOrigenId(origenId);
    pallet.setDestinoId(destinoId);
    pallet.setUsuarioId(usuarioId);
    pallet.setCodigoSapId(defaultCodigoSapId);
    pallet.setTipologiaId(defaultTipologiaId);
    pallet.setPosicionId(0);
    pallet.setActivo(true);
    pallet.setFecha(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    PalletModel saved = palletRepository.save(pallet);
    logger.info("[reservePallet] Pallet reservado: id={}, numero={}", saved.getId(), saved.getNumero());
    Map<String, Object> result = new HashMap<>();
    result.put("id", saved.getId());
    result.put("numero", saved.getNumero());
    return result;
  }

  @Transactional
  public void updatePalletData(Integer palletId, Integer codigoSapId, Integer tipologiaId,
      Integer posicionId, Integer loteId, Integer usuarioId) {
    PalletModel pallet = palletRepository.findById(palletId).orElseThrow();
    String ahora = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    pallet.setCodigoSapId(codigoSapId);
    pallet.setTipologiaId(tipologiaId);
    pallet.setActivo(true);
    pallet.setFecha(ahora);
    pallet.setFechaModifica(ahora);
    pallet.setUsuarioId(usuarioId);
    pallet.setUsuarioIdModifica(usuarioId); // necesario para el trigger que inserta en Movimiento
    if (posicionId != null && posicionId > 0) {
      pallet.setPosicionId(posicionId);
    }
    if (loteId != null && loteId > 0) {
      pallet.setLoteId(loteId);
    }
    palletRepository.save(pallet);
  }

  public List<PalletDTO> searchEntry(String numero, String destino, int usuarioId) {
    List<Object[]> results = palletRepository.searchEntry(numero, destino, usuarioId);
    return results.stream().map(obj -> {
      PalletDTO pallet = new PalletDTO();
      pallet.setId((Integer) obj[0]);
      pallet.setNumero((String) obj[1]);
      pallet.setCodigoSap((String) obj[2]);
      pallet.setDescripcion((String) obj[3]);
      pallet.setCantidad((Integer) obj[4]);
      pallet.setTipologia((String) obj[5]);
      pallet.setLote((String) obj[6]);
      return pallet;
    }).collect(Collectors.toList());
  }

  public List<PalletDTO> searchAccesory(String numero, String destino, int usuarioId) {
    List<Object[]> results = palletRepository.searchAccesory(numero, destino, usuarioId);

    return results.stream().map(obj -> {
      PalletDTO pallet = new PalletDTO();
      pallet.setId((Integer) obj[0]);
      pallet.setNumero((String) obj[1]);
      pallet.setCodigoSap((String) obj[2]);
      pallet.setCantidad((Integer) obj[3]);
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

  public void enviarPallet(Integer palletId, Integer destinoId, Integer estadoId, Integer tipologiaId,
      Integer posicionId, Integer usuarioId, Integer opcion, Integer estado) {
    palletRepository.enviarPallet(palletId, destinoId, estadoId, tipologiaId, posicionId, estado, usuarioId, opcion, 0,
        0);
  }

  public Integer getCount(Integer palletId, String tabla) {
    return palletRepository.getCountPallet(palletId, tabla);
  }

  public boolean deletePallet1(Integer palletId, String tipoEquipo, String tipo) {
    if (!tipoEquipo.equals("") && !tipo.equals("")) {
      String estado = null;
      String tabla = null;
      tabla = getTablaDesdeTipo(tipoEquipo, tipo);
      estado = getEstadoDesdeTipo(tipoEquipo, tipo);
      int cajas = 0;
      try {
        if (estado != null) {
          cajas = palletRepository.getBoxPallet(palletId, estado); // Usa el estado para validar dependencias en cajas
        }
      } catch (Exception e) {
        cajas = 0; // En caso de fallo en el procedimiento almacenado
      }

      int registros = palletRepository.getCountPallet(palletId, tabla);

      if (cajas > 0 || registros > 0) {
        return false; // ❌ No eliminar, tiene dependencias
      }
    }

    palletRepository.deletePallet(palletId);
    return true;
  }

  public void deletePallet(Integer palletId) {
    palletRepository.deletePallet(palletId);
    logger.info("[deletePallet] Pallet eliminado: id={}", palletId);
  }

  private String getTablaDesdeTipo(String tipoEquipo, String tipo) {
    if ("serializable".equalsIgnoreCase(tipoEquipo)) {
      switch (tipo.toLowerCase()) {
        case "ingreso":
          return "INGRESO";
        case "accesorio":
          return "ACCESORIO";
        case "partes":
          return "PARTES";
        default:
          throw new IllegalArgumentException("Tipo no válido para equipo serializable: " + tipo);
      }
    } else {
      switch (tipo.toLowerCase()) {
        case "accesorio":
          return "ACCESORIO";
        case "empaque":
          return "EMPAQUE";
        case "despacho":
          return "DESPACHO";
        case "ingreso":
          return "INGRESO";
        default:
          throw new IllegalArgumentException("Tipo no válido para equipo no serializable: " + tipo);
      }
    }
  }

  private String getEstadoDesdeTipo(String tipoEquipo, String tipo) {
    if ("serializable".equalsIgnoreCase(tipoEquipo)) {
      if ("ingreso".equalsIgnoreCase(tipo))
        return "Ingreso";
      return null;
    } else {
      switch (tipo.toLowerCase()) {
        case "empaque":
          return "Empaque";
        case "despacho":
          return "Despacho";
        case "ingreso":
          return "Ingreso";
        default:
          return null;
      }
    }
  }

  public boolean confirmarPalletTransito(ConfirmarPalletDTO dto) {
    palletRepository.enviarPallet(
        dto.getPalletId(), dto.getDestinoId(), dto.getDestinoId(),
        dto.getTipologiaId(), dto.getPosicionId(), 1,
        dto.getUsuarioId(), 0, 0, 0);
    return true;
  }

  public boolean abrirPalletTransito(AbrirPalletDTO dto) {
    palletRepository.enviarPallet(
        dto.getPalletId(), dto.getOrigenId(), dto.getOrigenId(),
        dto.getTipologiaId(), dto.getPosicionId(), 1,
        dto.getUsuarioId(), 0, 0, 0);
    return true;
  }

  public void regularizarLotePallet(int palletId, int loteId, int usuarioIdMovimiento) {
    palletRepository.updateBatchPallet(palletId, loteId, usuarioIdMovimiento, 1);
  }

  public List<PalletDTO> searchReceivePallet(String numero, String destino, String tipo) {
    List<Object[]> results = palletRepository.searchReceivePallet(numero, destino, tipo);
    return results.stream().map(obj -> {
      PalletDTO pallet = new PalletDTO();
      pallet.setId((Integer) obj[0]);
      pallet.setNumero((String) obj[1]);
      pallet.setCantidad((Integer) obj[2]);
      pallet.setOrigen((String) obj[3]);
      pallet.setTipologia((String) obj[4]);
      pallet.setCodigoSap((String) obj[5]);
      pallet.setDescripcion((String) obj[6]);
      return pallet;
    }).collect(Collectors.toList());
  }

  public List<PalletDTO> searchPackingDeliveryPallet(String numero, String tipologia, String tipo) {
    List<Object[]> results = palletRepository.SearchPackingDeliveryPallet(numero, tipologia, tipo);
    return results.stream().map(obj -> {
      PalletDTO pallet = new PalletDTO();
      pallet.setId((Integer) obj[0]);
      pallet.setNumero((String) obj[1]);
      pallet.setCantidad((Integer) obj[2]);
      pallet.setOrigen((String) obj[3]);
      pallet.setTipologia((String) obj[4]);
      pallet.setCodigoSap((String) obj[5]);
      pallet.setDescripcion((String) obj[6]);
      return pallet;
    }).collect(Collectors.toList());
  }

  public List<Map<String, String>> SearchPalletBoxPallet(String numero, String destino, int page, int size) {
    List<Object[]> results = palletRepository.SearchPalletBoxPalletWeb(numero, destino);

    int fromIndex = page * size;
    int toIndex = Math.min(fromIndex + size, results.size());

    if (fromIndex >= results.size()) {
      return Collections.emptyList();
    }

    List<Object[]> paged = results.subList(fromIndex, toIndex);

    List<Map<String, String>> palletBox = new ArrayList<>();
    for (Object[] result : paged) {
      Map<String, String> pallet = new HashMap<>();
      pallet.put("id", String.valueOf(result[0]));
      pallet.put("numero", String.valueOf(result[1]));
      pallet.put("cantidadCaja", String.valueOf(result[2]));
      pallet.put("descripcion", String.valueOf(result[3]));
      pallet.put("tipologia", String.valueOf(result[4]));
      palletBox.add(pallet);
    }

    return palletBox;
  }

  public List<Map<String, String>> searchPalletsBoxesAll(String numero, String destino) {
    List<Object[]> results = palletRepository.SearchPalletBoxPalletWeb(numero, destino);

    List<Map<String, String>> palletBox = new ArrayList<>();
    for (Object[] result : results) {
      Map<String, String> pallet = new HashMap<>();
      pallet.put("id", String.valueOf(result[0]));
      pallet.put("numero", String.valueOf(result[1]));
      pallet.put("cantidadCaja", String.valueOf(result[2]));
      pallet.put("descripcion", String.valueOf(result[3]));
      pallet.put("tipologia", String.valueOf(result[4]));
      palletBox.add(pallet);
    }

    return palletBox;
  }

  public List<Map<String, String>> SearchPalletBoxPalletWeb(String numero, String destino, int page, int size) {
    List<Object[]> results = palletRepository.SearchPalletBoxPalletWeb(numero, destino);

    int fromIndex = page * size;
    int toIndex = Math.min(fromIndex + size, results.size());

    if (fromIndex >= results.size()) {
      return Collections.emptyList();
    }

    List<Object[]> paged = results.subList(fromIndex, toIndex);

    List<Map<String, String>> palletBox = new ArrayList<>();
    for (Object[] result : paged) {
      Map<String, String> pallet = new HashMap<>();
      pallet.put("id", String.valueOf(result[0]));
      pallet.put("numero", String.valueOf(result[1]));
      pallet.put("cantidadCaja", String.valueOf(result[2]));
      pallet.put("descripcion", String.valueOf(result[3]));
      pallet.put("tipologia", String.valueOf(result[4]));
      palletBox.add(pallet);
    }

    return palletBox;
  }

  public boolean eliminarAccesorio(Integer palletId, Integer cantidad, Integer codigoSapId, Integer usuarioId) {
    Integer result = palletRepository.eliminarAccesorio(palletId, cantidad, codigoSapId, usuarioId);
    return result > 0;
  }

  public void sendPallet(SendPalletDTO dto) {
    palletRepository.sendPallet(
        dto.getDestinoId(),
        dto.getTipologiaId(),
        dto.getPosicionId(),
        dto.getEstado(),
        dto.getPalletId(),
        0);
  }

  public List<PalletStorageDTO> searchStoragePallet(String numero, String tipo, String tipoAccesorio) {
    List<Object[]> results = palletRepository.searchStoragePallet(numero, tipo, tipoAccesorio);
    return results.stream().map(obj -> {
      PalletStorageDTO pallet = new PalletStorageDTO();
      pallet.setId((Integer) obj[0]);
      pallet.setNumero((String) obj[1]);
      pallet.setCantidad((Integer) obj[2]);
      pallet.setOrigen((String) obj[3]);
      pallet.setTipologia((String) obj[4]);
      pallet.setCodigoSap((String) obj[5]);
      pallet.setDescripcion((String) obj[6]);
      pallet.setLote((String) obj[7]);
      pallet.setPosicion((String) obj[8]);
      return pallet;
    }).collect(Collectors.toList());
  }

  public void updatePosition(Integer palletId, Integer posicionId) {
    palletRepository.updatePosition(palletId, posicionId, 0);
  }

  public void inactivatePallet(int palletId) {
    int dummyFilas = 0; // Este valor no se actualiza, es solo decorativo
    palletRepository.innactivatePallet(palletId, dummyFilas);
  }

  public void updateTipologia(Integer palletId, Integer tipologiaId) {
    palletRepository.updateTipologia(palletId, tipologiaId, 0);
  }

  public List<PalletStorageDTO> searchStorageGroupPalletAccesory(String numero, String tipoAccesorio) {
    List<Object[]> results = palletRepository.searchStorageGroupPalletAccesory(numero, tipoAccesorio);
    return results.stream().map(obj -> {
      PalletStorageDTO pallet = new PalletStorageDTO();
      pallet.setId((Integer) obj[0]);
      pallet.setNumero((String) obj[1]);
      pallet.setCantidad((Integer) obj[2]);
      pallet.setCodigoSap((String) obj[3]);
      pallet.setDescripcion((String) obj[4]);
      pallet.setTipologia((String) obj[5]);
      pallet.setPosicion((String) obj[6]);
      pallet.setOrigen((String) obj[7]);
      return pallet;
    }).collect(Collectors.toList());
  }

  public List<PalletStorageDTO> searchStorageGroupPalletAccesoryCreated(String numero, String tipoAccesorio) {
    List<Object[]> results = palletRepository.searchStorageGroupPalletAccesoryCreated(numero, tipoAccesorio);
    return results.stream().map(obj -> {
      PalletStorageDTO pallet = new PalletStorageDTO();
      pallet.setId((Integer) obj[0]);
      pallet.setNumero((String) obj[1]);
      pallet.setCantidad((Integer) obj[2]);
      pallet.setCodigoSap((String) obj[3]);
      pallet.setTipologia((String) obj[4]);
      pallet.setPosicion((String) obj[5]);
      pallet.setOrigen((String) obj[6]);
      return pallet;
    }).collect(Collectors.toList());
  }

  public List<PalletStorageDTO> searchStorageGroupPallet(String numero, String tipoEquipo) {
    List<Object[]> results = palletRepository.searchStorageGroupPallet(numero, tipoEquipo);
    return results.stream().map(obj -> {
      PalletStorageDTO pallet = new PalletStorageDTO();
      pallet.setId((Integer) obj[0]);
      pallet.setNumero((String) obj[1]);
      pallet.setCantidad((Integer) obj[2]);
      pallet.setCodigoSap((String) obj[3]);
      pallet.setDescripcion((String) obj[4]);
      pallet.setTipologia((String) obj[5]);
      pallet.setPosicion((String) obj[6]);
      pallet.setOrigen((String) obj[7]);
      return pallet;
    }).collect(Collectors.toList());
  }

  public void unifyPallet(List<Integer> palletId) {
    palletId.forEach(id -> palletRepository.unifyPallet(id));
  }

  public List<String> getListPallets(String destino) {
    List<Object[]> results = palletRepository.getListPallets(destino);
    List<String> pallets = new ArrayList<>();
    for (Object[] row : results) {
      pallets.add((String) row[0]);
    }
    return pallets;
  }

  public Integer getIdPallet(String numero) {
    return palletRepository.getIdPallet(numero);
  }

  public Integer updateSapCodePallet(Integer palletId, Integer codigoSapId, Integer filas) {
    return palletRepository.updateSapCodePallet(palletId, codigoSapId, filas);
  }

  public Integer getBoxPallet(Integer palletId, String tabla) {
    return palletRepository.getBoxPallet(palletId, tabla);
  }

  public void innactivatePallet(Integer palletId) {
    int filas = 0;
    palletRepository.innactivatePallet(palletId, filas);
  }

  public List<Map<String, String>> searchQualityDeliveryPallet() {
    List<Object[]> results = palletRepository.searchQualityDeliveryPallet();
    return results.stream().map(result -> {
      Map<String, String> pallet = new HashMap<>();
      pallet.put("id", String.valueOf(result[0]));
      pallet.put("numero", String.valueOf(result[1]));
      pallet.put("cantidadCaja", String.valueOf(result[2]));
      return pallet;
    }).collect(Collectors.toList());
  }

  public List<Map<String, String>> searchPalletBoxDispatchPallet(String numero) {
    List<Object[]> results = palletRepository.searchPalletBoxDispatchPallet(numero);

    return results.stream().map(result -> {
      Map<String, String> pallet = new HashMap<>();
      pallet.put("id", String.valueOf(result[0]));
      pallet.put("numero", String.valueOf(result[1]));
      pallet.put("codigoSap", String.valueOf(result[2]));
      pallet.put("descripcion", String.valueOf(result[3]));
      pallet.put("cajas", String.valueOf(result[4]));
      pallet.put("cantidadEquipos", String.valueOf(result[5]));
      pallet.put("cantidadAccesorios", String.valueOf(result[6]));
      pallet.put("cantidadSmartcard", String.valueOf(result[7]));
      pallet.put("tipologia", String.valueOf(result[8]));
      return pallet;
    }).collect(Collectors.toList());
  }

  public int sendAllPallet(Integer destinoId, Integer palletId) {
    palletRepository.sendAllPallet(destinoId, palletId);
    return 1;
  }

  public List<Map<String, Object>> getPalletsInventory(Integer usuarioId) {
    List<Object[]> results = palletRepository.getPalletsInventory(usuarioId);
    return results.stream().map(record -> {
      Map<String, Object> map = new HashMap<>();
      map.put("id", record[0]);
      map.put("numero", record[1]);
      map.put("cantidad", record[2]);
      map.put("posicion", record[3]);
      map.put("codigoSapId", record[4]);
      map.put("codigoSap", record[5]);
      map.put("descripcion", record[6]);
      return map;
    }).collect(Collectors.toList());
  }

  public Integer innactivatePalletInventory(Integer palletId) {
    return palletRepository.innactivatePalletInventory(palletId);
  }

  public List<Map<String, Object>> searchReceivePartsPallet(String destino, String numero) {
    List<Object[]> results = palletRepository.searchReceivePartsPallet(destino, numero);
    return results.stream().map(record -> {
      Map<String, Object> map = new HashMap<>();
      map.put("id", record[0]);
      map.put("numero", record[1]);
      map.put("cantidad", record[2]);
      map.put("posicion", record[3]);
      map.put("codigoSapId", record[4]);
      map.put("codigoSap", record[5]);
      map.put("descripcion", record[6]);
      return map;
    }).collect(Collectors.toList());
  }

  public List<Map<String, Object>> searchGeneralInventoryPallet(String numero, String estado, Integer usuarioId) {
    List<Object[]> results = palletRepository.searchGeneralInventoryPallet(numero, estado, usuarioId);
    return results.stream().map(record -> {
      Map<String, Object> map = new HashMap<>();
      map.put("id", record[0]);
      map.put("numero", record[1]);
      map.put("cantidad", record[2]);
      map.put("posicion", record[3]);
      map.put("codigoSapId", record[4]);
      map.put("codigoSap", record[5]);
      map.put("descripcion", record[6]);
      return map;
    }).collect(Collectors.toList());
  }

  public void updateStateInventoryPallet(Integer palletId, Integer estadoInventario) {
    palletRepository.updateStateInventoryPallet(palletId, estadoInventario, 4);
  }

  public List<Map<String, Object>> searchGeneralSettingsPallet(String nuemro, String estado) {
    List<Object[]> results = palletRepository.searchGeneralSettingsPallet(nuemro, estado);
    return results.stream().map(record -> {
      Map<String, Object> map = new HashMap<>();
      map.put("id", record[0]);
      map.put("numero", record[1]);
      map.put("cantidad", record[2]);
      map.put("posicion", record[3]);
      map.put("codigoSapId", record[4]);
      map.put("codigoSap", record[5]);
      map.put("descripcion", record[6]);
      return map;
    }).collect(Collectors.toList());
  }

  public Boolean getPalletNumero(String palletNumero) {
    return palletRepository.getPalletNumero(palletNumero);
  }

  public void cerrarPallet(Integer palletId, Integer destinoId, Integer estadoId, Integer tipologiaId,
      Integer posicionId, Integer usuarioId, Integer opcion) {
    palletRepository.enviarPallet(palletId, destinoId, estadoId, tipologiaId, posicionId, estadoId, usuarioId, opcion, 0, 0);
  }


  public List<ModeloPalletDto> searchModels(Integer palletId) {
    List<Object[]> rows = palletRepository.searchModels(palletId);
    List<ModeloPalletDto> result = new ArrayList<>();
    for (Object[] row : rows) {
      ModeloPalletDto dto = new ModeloPalletDto();
      dto.setCodigoSap(row[0] != null ? row[0].toString() : "");
      dto.setCantidad(row[1] != null ? Integer.valueOf(row[1].toString()) : 0);
      dto.setPosicion(null);
      result.add(dto);
    }
    return result;
  }

  @Transactional
  public int sendModels(SendModelsDto dto) {
    try {
      // 1. Validar si ya existe el pallet en esa posición
      List<Object[]> existente = palletRepository.getModelByNumber(dto.getPosicionNumero());

      Integer palletDestinoId;

      if (existente == null || existente.isEmpty()) {
        // Buscar datos maestros necesarios para el nuevo pallet
        Integer codigoSapMultimodelo = codigoSapRepository.getIdByCodigo("MULTIMODELO");
        Integer tipologiaLibre = maestroRepository.getIdByDescripcionAndTipo("LIBRE UTILIZACION", "Tipologias");
        Integer posicionId = posicionRepository.getIdByNumero(dto.getPosicionNumero());

        // Insertar nuevo pallet
        palletRepository.insertPallet(
                dto.getPosicionNumero(),
                posicionId,
                codigoSapMultimodelo,
                tipologiaLibre,
                0, 0,
                dto.getUsuarioId(),
                null
        );
        // Obtener el ID del pallet recién creado
        palletDestinoId = palletRepository.getIdPallet(dto.getPosicionNumero());
      } else {
        // Si ya existe, usamos el ID que viene del DTO o lo buscamos
        palletDestinoId = palletRepository.getIdPallet(dto.getPosicionNumero());
      }

      // 2. Ejecutar la transferencia de modelos
      palletRepository.sendModels(
              dto.getDestinoId(),
              dto.getUsuarioId(),
              palletDestinoId,
              dto.getPalletOrigenId(),
              dto.getCodigoSapId()
      );

      return 1;
    } catch (Exception e) {

      log.error("Error en sendModels: ", e); // Mejor usar un logger
      return 0;
    }
  }
}
