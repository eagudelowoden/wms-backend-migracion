package com.woden.wms_backend.dto.clientDTO;

/**
 * Reemplazo de un serial empacado (Empaque > Proceso y Calidad > Proceso).
 *
 * @param serialAnterior serial que sale del pallet
 * @param serialNuevo    serial que entra en su lugar (hereda pallet, caja, fecha de empaque y accesorios)
 * @param destinoId      estado al que va el serial anterior
 * @param usuarioId      usuario que hace el reemplazo
 * @param modulo         EMPAQUE o CALIDAD (va en la descripcion del movimiento)
 * @param smartCardModo  NINGUNO (sin smartcard), INGRESO (smartcard registrada en Ingreso, smartCardInfoOn = 0)
 *                       o TEXTO (smartcard como texto, smartCardInfoOn = 1)
 * @param smartCardNuevo smartcard del serial nuevo (serial de la smartcard o el texto)
 * @param simular        true: hace todo, verifica y lo deshace (para probar sin cambiar datos)
 */
public record ReemplazoSerialRequest(
    String serialAnterior,
    String serialNuevo,
    Integer destinoId,
    Integer usuarioId,
    String modulo,
    String smartCardModo,
    String smartCardNuevo,
    Boolean simular) {
}
