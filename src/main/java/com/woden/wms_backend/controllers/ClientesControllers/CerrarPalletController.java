// package com.woden.wms_backend.controllers.ClientesControllers;

// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;

// import com.woden.wms_backend.dto.CerrarCompletoDTO;
// import com.woden.wms_backend.services.ClienteServices.IngresoService;
// import com.woden.wms_backend.services.ClienteServices.PalletService;

// @RestController
// @RequestMapping("/api/pallets")
// public class CerrarPalletController {

//   private final IngresoService ingresoService;
//   private final PalletService palletService;

//   public CerrarPalletController(IngresoService ingresoService, PalletService palletService) {
//     this.ingresoService = ingresoService;
//     this.palletService = palletService;
//   }

//   @PostMapping("/cerrar")
//   public ResponseEntity<String> cerrarPallet(@RequestBody CerrarCompletoDTO dto) {
//     try {
//       ingresoService.cerrarIngreso(dto.getPalletId(), dto.getEstadoId(), dto.getTipologiaId(), dto.getUsuarioId(),
//           dto.getOpcion());
//       palletService.cerrarPallet(dto.getPalletId(), dto.getDestinoId(), dto.getTipologiaId(), dto.getPosicionId(),
//           dto.getEstadoId());
//       return ResponseEntity.ok("✅ Pallet cerrado correctamente");
//     } catch (Exception e) {
//       return ResponseEntity.status(500).body("❌ Error al cerrar el pallet: " + e.getMessage());
//     }
//   }
// }