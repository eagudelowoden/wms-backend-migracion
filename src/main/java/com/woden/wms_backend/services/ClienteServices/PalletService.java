package com.woden.wms_backend.services.ClienteServices;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.woden.wms_backend.dto.PalletDTO;
import com.woden.wms_backend.models.Entity.PalletModel;
import com.woden.wms_backend.repositories.ClienteRepositories.PalletRepository;
import com.woden.wms_backend.services.BaseService;

@Service
public class PalletService extends BaseService<PalletModel, Integer> {

  private static final Logger logger = LoggerFactory.getLogger(PalletService.class);
  @Autowired
  private PalletRepository palletRepository;

  // @Transactional
  @Transactional(propagation = Propagation.REQUIRED)
  public void createPallet(PalletModel p) {
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
  }

  // public List<PalletModel> searchEntry(String numero, String destino, int
  // usuarioId) {
  // List<Object[]> results = palletRepository.searchEntry(numero, destino,
  // usuarioId);

  // return results.stream().map(obj -> {
  // PalletModel pallet = new PalletModel();
  // pallet.setId((Integer) obj[0]);
  // pallet.setNumero((String) obj[1]);
  // pallet.setCodigoSapId((Integer) obj[2]);
  // // pallet.setCantidad((Integer) obj[3]);
  // pallet.setTipologiaId((Integer) obj[4]);
  // pallet.setLoteId((Integer) obj[5]);
  // return pallet;
  // }).collect(Collectors.toList());
  // }
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
}
