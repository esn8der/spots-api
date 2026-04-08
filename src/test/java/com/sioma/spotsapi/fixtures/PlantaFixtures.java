package com.sioma.spotsapi.fixtures;

import java.util.UUID;

public class PlantaFixtures {
    public static final String NOMBRE = "Palma";

    public static String uniqueName() {
        return NOMBRE + "-" + UUID.randomUUID().toString().substring(0, 6);
    }

}
