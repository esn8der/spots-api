package com.sioma.spotsapi.infrastructure.config;

import org.slf4j.LoggerFactory;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
public abstract class PostgresContainerConfig {

    private static final DockerImageName POSTGIS_IMAGE =
            DockerImageName.parse("postgis/postgis:16-3.4-alpine")
                    .asCompatibleSubstituteFor("postgres");

    @Container
    static PostgreSQLContainer postgres =
            new PostgreSQLContainer(POSTGIS_IMAGE)
                    .withDatabaseName("test-db")
                    .withUsername("test")
                    .withPassword("test")
                    .withLogConsumer(new Slf4jLogConsumer(LoggerFactory.getLogger("TC")))
                    .withReuse(true);

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        // Habilita soporte espacial
        registry.add("spring.jpa.properties.hibernate.integration.spatial.enabled",
                () -> "true");
    }
}