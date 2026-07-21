package com.woden.wms_backend.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MenuModuloMapping {

    private static final Map<Integer, String> MODULO_TO_MENU = new HashMap<>();

    private static final Map<Integer, String> SECCION_TO_GROUP = new HashMap<>();

    private static final Set<String> ALL_MENU_IDS = new HashSet<>();

    static {
        SECCION_TO_GROUP.put(1, "INV");
        SECCION_TO_GROUP.put(2, "LOG");
        SECCION_TO_GROUP.put(3, "PROD");
        SECCION_TO_GROUP.put(5, "ADM");

        MODULO_TO_MENU.put(1, "IGEN");
        MODULO_TO_MENU.put(3, "LING");
        MODULO_TO_MENU.put(4, "LSEP");
        MODULO_TO_MENU.put(5, "LALM");
        MODULO_TO_MENU.put(6, "LDES");
        MODULO_TO_MENU.put(7, "LNOV");
        MODULO_TO_MENU.put(33, "LSCR");
        MODULO_TO_MENU.put(8, "PCLA");
        MODULO_TO_MENU.put(9, "PENS");
        MODULO_TO_MENU.put(10, "PCOS");
        MODULO_TO_MENU.put(11, "PETIQ");
        MODULO_TO_MENU.put(12, "PDIA");
        MODULO_TO_MENU.put(13, "PREP");
        MODULO_TO_MENU.put(14, "PEMP");
        MODULO_TO_MENU.put(15, "PCAL");
        MODULO_TO_MENU.put(34, "PINN");
        MODULO_TO_MENU.put(35, "LLIM");
        MODULO_TO_MENU.put(36, "PSMART");
        MODULO_TO_MENU.put(18, "ADTM");
        MODULO_TO_MENU.put(19, "ADMA");
        MODULO_TO_MENU.put(22, "ADCS");
        MODULO_TO_MENU.put(24, "ADET");
        MODULO_TO_MENU.put(25, "ADPER");
        MODULO_TO_MENU.put(26, "ADUS");
        MODULO_TO_MENU.put(43, "ADDEV");

        ALL_MENU_IDS.addAll(MODULO_TO_MENU.values());
        ALL_MENU_IDS.add("ADMXP");
    }

    public static Set<String> getMenuIds(List<Integer> moduloIds) {
        Set<String> ids = new HashSet<>();
        for (int mid : moduloIds) {
            String menuId = MODULO_TO_MENU.get(mid);
            if (menuId != null) ids.add(menuId);
        }
        return ids;
    }

    public static Set<String> getGroupIds(List<Integer> seccionIds) {
        Set<String> ids = new HashSet<>();
        for (int sid : seccionIds) {
            String groupId = SECCION_TO_GROUP.get(sid);
            if (groupId != null) ids.add(groupId);
        }
        return ids;
    }

    public static boolean tieneMapping(String menuId) {
        return ALL_MENU_IDS.contains(menuId);
    }
}
