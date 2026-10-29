package com.woden.wms_backend.services.ClienteServices;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.woden.wms_backend.dto.clientDTO.ReemplazoSerialRequest;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Query;

@Service
public class ReemplazoSerialService {

  private static final Logger logger = LoggerFactory.getLogger(ReemplazoSerialService.class);

  @PersistenceContext
  private EntityManager em;

  @Transactional(rollbackFor = Exception.class)
  public Map<String, Object> reemplazar(ReemplazoSerialRequest r) {
    String anterior = limpio(r.serialAnterior());
    String nuevo = limpio(r.serialNuevo());
    String modulo = limpio(r.modulo()).isEmpty() ? "EMPAQUE" : limpio(r.modulo()).toUpperCase();
    String modo = limpio(r.smartCardModo()).toUpperCase();
    String scNuevo = limpio(r.smartCardNuevo());
    int usuario = r.usuarioId() == null ? 0 : r.usuarioId();
    if (anterior.isEmpty() || nuevo.isEmpty()) {
      throw new IllegalArgumentException("Indique el serial anterior y el serial nuevo.");
    }
    if (anterior.equalsIgnoreCase(nuevo)) {
      throw new IllegalArgumentException("El serial nuevo debe ser distinto del anterior.");
    }
    if (r.destinoId() == null || r.destinoId() <= 0) {
      throw new IllegalArgumentException("Seleccione el destino del serial anterior.");
    }

    Object[] viejo = fila("SELECT Id, Serial, EstadoId, ISNULL(PalletId, 0), ISNULL(CajaEmpaqueId, 0), "
        + "ISNULL(TipologiaId, 0), ISNULL(SmartCard, '') FROM Ingreso WITH (UPDLOCK) WHERE Serial = ?1",
        "El serial anterior " + anterior + " no existe en Ingreso.", anterior);
    Object[] entra = fila("SELECT Id, Serial, ISNULL(Mac, ''), ISNULL(NivelId, 0), EstadoId, ISNULL(PalletId, 0) "
        + "FROM Ingreso WITH (UPDLOCK) WHERE Serial = ?1",
        "El serial nuevo " + nuevo + " no existe en Ingreso.", nuevo);
    int idViejo = num(viejo[0]);
    int estadoViejo = num(viejo[2]);
    int pallet = num(viejo[3]);
    int caja = num(viejo[4]);
    int tipologia = num(viejo[5]);
    String scViejo = limpio(String.valueOf(viejo[6]));
    int idNuevo = num(entra[0]);
    String macNuevo = String.valueOf(entra[2]);
    int nivelNuevo = num(entra[3]);
    int estadoNuevoAntes = num(entra[4]);

    if (pallet == 0) {
      throw new IllegalStateException("El serial anterior " + anterior + " no esta en un pallet.");
    }
    // por SerialId: el mismo serial puede tener empaques de vueltas anteriores (otro SerialId) que no toco
    Integer empaqueId = entero("SELECT MAX(Id) FROM Empaque WHERE SerialId = ?1", idViejo);
    if (empaqueId == null) {
      throw new IllegalStateException("El serial anterior " + anterior + " no tiene registro de empaque.");
    }
    if (entero("SELECT COUNT(*) FROM Empaque WHERE SerialId = ?1", idNuevo) > 0) {
      throw new IllegalStateException("El serial nuevo " + nuevo + " ya tiene un registro de empaque.");
    }
    String palletNumero = texto("SELECT Numero FROM Pallet WHERE Id = ?1", pallet);
    String cajaNumero = caja > 0 ? texto("SELECT CAST(Numero AS VARCHAR(20)) FROM CajaEmpaque WHERE Id = ?1", caja) : "";

    exigir(actualizar("UPDATE Ingreso SET EstadoId = ?1, PalletId = ?2, PalletIdEmpaque = ?2, CajaEmpaqueId = ?3, "
        + "UsuarioIdMovimiento = ?4, TipologiaId = ?5 WHERE Id = ?6",
        estadoViejo, pallet, caja, usuario, tipologia, idNuevo), "mover el serial nuevo al pallet");

    exigir(actualizar("UPDATE Empaque SET SerialId = ?1, Serial = ?2, Mac = ?3 WHERE Id = ?4",
        idNuevo, nuevo, macNuevo, empaqueId), "pasar el registro de empaque al serial nuevo");
    // NivelId aparte y solo si cambia: trg_AuditoriaNivelIdEmpaque escribe en AuditoriaNivelEmpaque, que no existe en
    // todas las bases; con el mismo nivel no se dispara
    Integer nivelEmpaque = entero("SELECT NivelId FROM Empaque WHERE Id = ?1", empaqueId);
    if (nivelNuevo > 0 && (nivelEmpaque == null || nivelEmpaque != nivelNuevo)) {
      try {
        exigir(actualizar("UPDATE Empaque SET NivelId = ?1 WHERE Id = ?2", nivelNuevo, empaqueId),
            "cambiar el nivel en empaque");
      } catch (PersistenceException e) {
        throw new IllegalStateException("No se pudo cambiar el nivel en empaque: " + causa(e) + ". No se cambio nada.");
      }
    }

    String scEntra = "";
    if ("INGRESO".equals(modo) && !scNuevo.isEmpty()) {
      Object[] sc = fila("SELECT Id, Serial FROM Ingreso WITH (UPDLOCK) WHERE Serial = ?1",
          "La smartcard " + scNuevo + " no existe en Ingreso.", scNuevo);
      int scId = num(sc[0]);
      String scSerial = String.valueOf(sc[1]);
      scEntra = scSerial;
      // la smartcard queda SMARTCARD-ASIGNADA en el pallet del equipo, como en el empaque normal; si la base no tiene
      // ese estado, el de la smartcard que sale (0 = la nueva conserva el suyo); sin nulls en los parametros
      Integer estadoSc = entero("SELECT TOP 1 Id FROM Estado WHERE Nombre = 'SMARTCARD-ASIGNADA'");
      if (estadoSc == null && !scViejo.isEmpty() && !"0".equals(scViejo)) {
        estadoSc = entero("SELECT TOP 1 EstadoId FROM Ingreso WHERE Serial = ?1", scViejo);
      }
      exigir(actualizar("UPDATE Ingreso SET EstadoId = CASE WHEN ?1 > 0 THEN ?1 ELSE EstadoId END, PalletId = ?2, PalletIdEmpaque = ?2, "
          + "CajaEmpaqueId = ?3, UsuarioIdMovimiento = ?4, TipologiaId = ?5 WHERE Id = ?6",
          estadoSc == null ? 0 : estadoSc, pallet, caja, usuario, tipologia, scId), "mover la smartcard nueva al pallet");
      exigir(actualizar("UPDATE Empaque SET SmartCardId = ?1, SmartCard = ?2 WHERE Id = ?3", scId, scSerial, empaqueId),
          "asociar la smartcard en empaque");
      exigir(actualizar("UPDATE Ingreso SET SmartCardId = ?1, SmartCard = ?2 WHERE Id = ?3", scId, scSerial, idNuevo),
          "asociar la smartcard al serial nuevo");
    } else if ("TEXTO".equals(modo) && !scNuevo.isEmpty()) {
      scEntra = scNuevo;
      exigir(actualizar("UPDATE Empaque SET SmartCardId = NULL, SmartCard = ?1 WHERE Id = ?2", scNuevo, empaqueId),
          "asociar la smartcard en empaque");
      exigir(actualizar("UPDATE Ingreso SET SmartCardId = NULL, SmartCard = ?1 WHERE Id = ?2", scNuevo, idNuevo),
          "asociar la smartcard al serial nuevo");
    }
    // la smartcard fisica que tenia el serial anterior sale del pallet y vuelve a EMPAQUE (como hacia el front),
    // solo si de verdad esta en ese pallet
    if (!scViejo.isEmpty() && !"0".equals(scViejo) && !scViejo.equalsIgnoreCase(scEntra)) {
      actualizar("UPDATE Ingreso SET EstadoId = ISNULL((SELECT TOP 1 Id FROM Estado WHERE Nombre = 'EMPAQUE'), EstadoId), "
          + "PalletId = 0, PalletIdEmpaque = 0, CajaEmpaqueId = 0, UsuarioIdMovimiento = ?1 WHERE Serial = ?2 AND PalletId = ?3",
          usuario, scViejo, pallet);
    }

    exigir(actualizar("UPDATE Ingreso SET EstadoId = ?1, PalletId = 0, PalletIdEmpaque = 0, CajaEmpaqueId = 0, "
        + "UsuarioIdMovimiento = ?2, TipologiaId = ?3, SmartCardId = NULL, SmartCard = NULL WHERE Id = ?4",
        r.destinoId(), usuario, tipologia, idViejo), "sacar el serial anterior del pallet");

    int accesorios = actualizar("UPDATE Accesorio SET SerialEmpaque = ?1 WHERE SerialEmpaque = ?2", nuevo, anterior);

    String lugar = palletNumero + (cajaNumero.isEmpty() ? "" : " caja " + cajaNumero);
    movimiento(idViejo, anterior, estadoViejo, r.destinoId(),
        "REEMPLAZO " + modulo + ": sale de pallet " + lugar + ", reemplazado por " + nuevo, usuario);
    movimiento(idNuevo, nuevo, estadoNuevoAntes, estadoViejo,
        "REEMPLAZO " + modulo + ": entra a pallet " + lugar + " en lugar de " + anterior, usuario);

    Object[] chk = fila("SELECT (SELECT COUNT(*) FROM Ingreso WHERE Id = ?1 AND PalletId = ?2 AND ISNULL(CajaEmpaqueId, 0) = ?3), "
        + "(SELECT COUNT(*) FROM Empaque WHERE Id = ?4 AND SerialId = ?1 AND Serial = ?5), "
        + "(SELECT COUNT(*) FROM Ingreso WHERE Id = ?6 AND ISNULL(PalletId, 0) = 0 AND EstadoId = ?7), "
        + "(SELECT COUNT(*) FROM Accesorio WHERE SerialEmpaque = ?8)",
        "No se pudo verificar el reemplazo.", idNuevo, pallet, caja, empaqueId, nuevo, idViejo, r.destinoId(), anterior);
    if (num(chk[0]) != 1 || num(chk[1]) != 1 || num(chk[2]) != 1 || num(chk[3]) != 0) {
      throw new IllegalStateException("El reemplazo no quedo como se esperaba (nuevo en pallet " + num(chk[0])
          + ", empaque " + num(chk[1]) + ", anterior fuera " + num(chk[2]) + ", accesorios sin pasar " + num(chk[3])
          + "). No se cambio nada.");
    }

    boolean simular = Boolean.TRUE.equals(r.simular());
    if (simular) {
      TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
    }
    logger.info("[Reemplazo] {} {} -> {} pallet {} accesorios {}{}", modulo, anterior, nuevo, palletNumero, accesorios,
        simular ? " (simulado, deshecho)" : "");

    Map<String, Object> out = new LinkedHashMap<>();
    out.put("success", true);
    out.put("simulado", simular);
    out.put("serialAnterior", anterior);
    out.put("serialNuevo", nuevo);
    out.put("pallet", palletNumero);
    out.put("caja", cajaNumero);
    out.put("accesorios", accesorios);
    out.put("message", simular ? "Reemplazo verificado (simulado, no se guardo)." : "Reemplazo realizado correctamente.");
    return out;
  }


  private void movimiento(int serialId, String serial, int origen, int destino, String descripcion, int usuario) {
    actualizar("INSERT INTO Movimiento (SerialId, Serial, OrigenId, DestinoId, Descripcion, UsuarioId, Fecha) "
        + "VALUES (?1, ?2, ?3, ?4, ?5, ?6, GETDATE())",
        serialId, serial, origen, destino, descripcion.length() > 100 ? descripcion.substring(0, 100) : descripcion, usuario);
  }

  private Query consulta(String sql, Object... params) {
    Query q = em.createNativeQuery(sql);
    for (int i = 0; i < params.length; i++) {
      q.setParameter(i + 1, params[i]);
    }
    return q;
  }

  private int actualizar(String sql, Object... params) {
    return consulta(sql, params).executeUpdate();
  }

  private void exigir(int filas, String paso) {
    if (filas != 1) {
      throw new IllegalStateException("No se pudo " + paso + " (" + filas + " filas). No se cambio nada.");
    }
  }

  private Object[] fila(String sql, String siNoExiste, Object... params) {
    List<?> filas = consulta(sql, params).getResultList();
    if (filas.isEmpty()) {
      throw new IllegalStateException(siNoExiste);
    }
    Object f = filas.get(0);
    return f instanceof Object[] arr ? arr : new Object[] { f };
  }

  private Integer entero(String sql, Object... params) {
    List<?> filas = consulta(sql, params).getResultList();
    return filas.isEmpty() || filas.get(0) == null ? null : num(filas.get(0));
  }

  private String texto(String sql, Object... params) {
    List<?> filas = consulta(sql, params).getResultList();
    return filas.isEmpty() || filas.get(0) == null ? "" : String.valueOf(filas.get(0)).trim();
  }

  public static String causa(Throwable e) {
    Throwable t = e;
    while (t.getCause() != null && t.getCause() != t) {
      t = t.getCause();
    }
    return t.getMessage() == null ? t.getClass().getSimpleName() : t.getMessage();
  }

  private static int num(Object o) {
    return o == null ? 0 : ((Number) o).intValue();
  }

  private static String limpio(String s) {
    return s == null ? "" : s.trim();
  }
}
