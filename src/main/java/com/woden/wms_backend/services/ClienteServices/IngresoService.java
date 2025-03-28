package com.woden.wms_backend.services.ClienteServices;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.woden.wms_backend.dto.IngresoDTO;
import com.woden.wms_backend.dto.IngresoIlegibleDTO;
import com.woden.wms_backend.dto.IngresoTransitoDTO;
import com.woden.wms_backend.models.Entity.IngresoModel;
import com.woden.wms_backend.repositories.ClienteRepositories.IlegibleRepository;
import com.woden.wms_backend.repositories.ClienteRepositories.IngresoRepository;
import com.woden.wms_backend.services.BaseService;

import jakarta.transaction.Transactional;

@Service
public class IngresoService extends BaseService<IngresoModel, Integer> {
  @Autowired
  private IngresoRepository ingresoRepository;
  private IlegibleRepository ilegibleRepository;
  private ConsecutiveService consecutiveService;

  @Transactional
  public void createIngreso(IngresoModel ingreso) {
    ingresoRepository.insertIngreso(
        ingreso.getSerial(),
        ingreso.getMac(),
        ingreso.getSerial3(),
        ingreso.getSerial4(),
        ingreso.getSerial5(),
        ingreso.getCodigoSapId(),
        ingreso.getPalletId(),
        ingreso.getEstadoId(),
        ingreso.getTipoOrigenId(),
        ingreso.getOrigenId(),
        ingreso.getTipologiaId(),
        ingreso.getNivelId(),
        ingreso.getTramite(),
        ingreso.getDocumento(),
        ingreso.getGuia(),
        ingreso.getCaja(),
        ingreso.getFalla(),
        ingreso.getTecnicoCliente(),
        ingreso.getPrealertaId(),
        ingreso.getCruce(),
        ingreso.getNovedad(),
        ingreso.getGarantiaFabricante(),
        ingreso.getUsuarioId(),
        ingreso.getObservaciones(),
        ingreso.getEstadoCliente(),
        ingreso.getLoteId(),
        ingreso.getCajaIngresoId(),
        ingreso.getModeloId());
  }

  public List<IngresoDTO> searchEntryReingreso(int palletId) {
    List<Object[]> results = ingresoRepository.searchEntryReingreso(palletId);

    return results.stream().map(obj -> {
      IngresoDTO ingreso = new IngresoDTO();
      ingreso.setSerial((String) obj[0]);
      ingreso.setMac((String) obj[1]);
      ingreso.setSerial3((String) obj[2]);
      ingreso.setSerial4((String) obj[3]);
      ingreso.setSerial5((String) obj[4]);
      ingreso.setCodigoSap((String) obj[5]);
      ingreso.setDescripcion((String) obj[6]);
      ingreso.setTipologia((String) obj[7]);
      ingreso.setTipoOrigen((String) obj[8]);
      ingreso.setFecha(((Timestamp) obj[9]).toString());
      ingreso.setEstadoCliente((String) obj[10]);
      ingreso.setLote((String) obj[11]);
      ingreso.setModelo((String) obj[12]);
      ingreso.setReingreso((Integer) obj[13]);
      return ingreso;
    }).collect(Collectors.toList());
  }

  public void cerrarIngreso(Integer palletId, Integer estadoId, Integer tipologiaId, Integer usuarioId,
      Integer opcion) {
    Integer filas = 0; // aquí el OUT lo usamos de forma simbólica
    ingresoRepository.sendIngreso(estadoId, tipologiaId, usuarioId, palletId, opcion, filas);
  }

  public List<IngresoTransitoDTO> getIngresoTransitByPalletId(Integer palletId) {
    List<Object[]> results = ingresoRepository.searchIngresoTransito(palletId);
    List<IngresoTransitoDTO> response = new ArrayList<>();

    for (Object[] row : results) {
      IngresoTransitoDTO dto = new IngresoTransitoDTO();
      dto.setSerial((String) row[0]);
      dto.setMac((String) row[1]);
      dto.setCodigoSap((String) row[2]);
      dto.setDescripcion((String) row[3]);
      dto.setTipologia((String) row[4]);
      dto.setLote((String) row[5]);
      dto.setGuia((String) row[6]);
      dto.setDocumento((String) row[7]);
      dto.setFecha(row[8] != null ? row[8].toString() : null);
      response.add(dto);
    }

    return response;
  }

  public void regularizarSap(String serial, int codigoSapId, int usuarioIdMovimiento) {
    ingresoRepository.updateSapCode(codigoSapId, usuarioIdMovimiento, serial, 1);
  }

  public void regularizarLoteSerial(String serial, int loteId, int usuarioIdMovimiento) {
    ingresoRepository.updateBatchSerial(loteId, usuarioIdMovimiento, serial, 1);
  }

  public String generarIngresoIlegible(IngresoIlegibleDTO ingresoDTO, String cliente, Integer usuarioId) {
    String consecutivo = consecutiveService.getConsecutive(cliente);

    String serialIlegible = (consecutivo == null) ? "1"
        : String.valueOf(Integer.parseInt(consecutivo.substring(6)) + 1);

    String prefijo;
    switch (cliente.toUpperCase()) {
      case "CLARO":
        prefijo = "ILE-C-";
        break;
      case "ETB":
        prefijo = "ILE-E-";
        break;
      case "TIGO BOGOTA":
      case "TIGO COSTA RICA":
      case "TIGO MEDELLIN":
      case "TIGO SALVADOR":
      case "TIGO PANAMA":
        prefijo = "ILE-T-";
        break;
      case "RED EXTERNA":
        prefijo = "ILE-R-";
        break;
      case "MOVIL":
        prefijo = "ILE-M-";
        break;
      case "HUGHESNET":
        prefijo = "ILE-H-";
        break;
      case "DIRECTV":
      case "TIGO CORPORATIVO PA":
      case "TIGO CORPORATIVO CR":
        prefijo = "ILE-D-";
        break;
      default:
        prefijo = "ILE-" + cliente.charAt(0) + "-";
        break;
    }

    String serial = prefijo + serialIlegible;
    String mac = "MAC-ILEGIBLE-" + serialIlegible;
    Timestamp fechaActual = new Timestamp(System.currentTimeMillis());

    // Insertar en la base de datos
    int status = ilegibleRepository.insertIlegible(serial, mac, 0, fechaActual);

    return (status == 1) ? serial : null;
  }
}
