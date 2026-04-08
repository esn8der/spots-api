package com.sioma.spotsapi.fixtures;

import java.util.UUID;

public class FincaFixtures {
    public static final String NOMBRE = "Finca 1";
    public static final Long USUARIO_ID = 1L;

    public static String uniqueName() {
        return NOMBRE + "-" + UUID.randomUUID().toString().substring(0, 6);
    }
}
