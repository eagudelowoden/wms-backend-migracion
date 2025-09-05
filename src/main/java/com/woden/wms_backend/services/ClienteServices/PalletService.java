package com.woden.wms_backend.services.ClienteServices;

import java.util.*;
import java.util.stream.Collectors;

import com.woden.wms_backend.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.woden.wms_backend.dto.clientDTO.PalletStorageDTO;
import com.woden.wms_backend.models.Entity.PalletModel;
import com.woden.wms_backend.repositories.ClienteRepositories.CodigoSapRepository;
import com.woden.wms_backend.repositories.ClienteRepositories.IngresoRepository;
import com.woden.wms_backend.repositories.ClienteRepositories.MaestroRepository;
import com.woden.wms_backend.repositories.ClienteRepositories.PalletRepository;
import com.woden.wms_backend.services.BaseService;
import com.woden.wms_backend.util.TypeMapper;

@Service
public class PalletService extends BaseService<PalletModel, Integer> {

  @Autowired
  private PalletRepository palletRepository;
  @Autowired
  private IngresoRepository ingresoRepository;
  @Autowired
  private CodigoSapRepository codigoSapRepository;
  @Autowired
  private MaestroRepository maestroRepository;

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

    return pallet;
  }

  @Transactional(propagation = Propagation.REQUIRED)
  public void createPallet(PalletModel p, Boolean kitEntryOn) {
    try {

      // Insertar el pallet
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

      // Si KitIngresoON está activo, actualizar la cantidad en la familia
      if (kitEntryOn) {
        String codigoSap = p.getCodigoSapId().toString();
        int familyId = codigoSapRepository.getFamilyId(codigoSap);
        String newFamilyNumber = String.valueOf(maestroRepository.getFamilyNumberPallet(codigoSap) + 1);

        int filas = 0; // OUT simbólico
        maestroRepository.addCountPalletFamily(newFamilyNumber, familyId, filas);
      }

    } catch (Exception e) {
      throw e; // Relanzar la excepción para manejo en el controlador
    }
  }

  public List<PalletDTO> searchEntry(String numero, String destino, int usuarioId) {
    List<Object[]> results = palletRepository.searchEntry(numero, destino, usuarioId);
    return results.stream().map(obj -> {
      PalletDTO pallet = new PalletDTO();
      pallet.setId((Integer) obj[0]);
      pallet.setNumero((String) obj[1]);
      pallet.setCodigoSap((String) obj[2]);
      pallet.setDescripcion((String) obj[3]);
      pallet.setCantidad((Integer) obj[4]); // Cantidad no está en PalletModel, pero sí en el DTO
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

  public void cerrarPallet(Integer palletId, Integer destinoId, Integer tipologiaId, Integer posicionId,
      Integer estado) {
    Integer filas = 0;
    palletRepository.sendPallet(destinoId, tipologiaId, posicionId, estado, palletId, filas);
  }

  public Integer getCount(Integer palletId, String tabla) {
    return palletRepository.getCountPallet(palletId, tabla);
  }

  public boolean deletePallet(Integer palletId, String tipoEquipo, String tipo) {
    if (!tipoEquipo.equals("") && !tipo.equals("")) {
      String estado = null;
      String tabla = null;
      tabla = getTablaDesdeTipo(tipoEquipo, tipo);
      estado = getEstadoDesdeTipo(tipoEquipo, tipo);
      int cajas = 0;
      try {
        if (estado != null) {
          cajas = palletRepository.getBoxCount(palletId, estado); // Usa el estado para validar dependencias en cajas
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
      // Los otros tipos serializables no tienen lógica de conteo en pa_GetBoxPallet
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

  public List<PalletDTO> searchReceivePallet(String numero, String destino, String tipo) {
    List<Object[]> results = palletRepository.searchReceivePallet(numero, destino, tipo);
    // for (Object[] row : results) {
    // System.out.println("---- Fila storage ----");
    // for (int i = 0; i < row.length; i++) {
    // System.out.println("Posición " + i + ": " + row[i]);
    // }
    // }
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
        List<Object[]> results = palletRepository.SearchPalletBoxPallet(numero, destino);

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


  public boolean eliminarAccesorio(Integer palletId, Integer cantidad, Integer codigoSapId) {
    Integer result = palletRepository.eliminarAccesorio(palletId, cantidad, codigoSapId);
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
    // for (Object[] row : results) {
    // System.out.println("---- Fila storage ----");
    // for (int i = 0; i < row.length; i++) {
    // System.out.println("Posición " + i + ": " + row[i]);
    // }
    // }
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
    // for (Object[] row : results) {
    // System.out.println("---- Fila ----");
    // for (int i = 0; i < row.length; i++) {
    // System.out.println("Posición " + i + ": " + row[i]);
    // }
    // }

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
      pallet.setLote((String) obj[8]);
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
}
