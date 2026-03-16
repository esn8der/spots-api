package com.sioma.spotsapi.infrastructure.persistence.repository;

import com.sioma.spotsapi.fixtures.FincaFixtures;
import com.sioma.spotsapi.fixtures.LoteFixtures;
import com.sioma.spotsapi.fixtures.PlantaFixtures;
import com.sioma.spotsapi.fixtures.UsuarioFixtures;
import com.sioma.spotsapi.infrastructure.config.PostgresContainerConfig;
import com.sioma.spotsapi.infrastructure.persistence.entity.FincaEntity;
import com.sioma.spotsapi.infrastructure.persistence.entity.LoteEntity;
import com.sioma.spotsapi.infrastructure.persistence.entity.PlantaEntity;
import com.sioma.spotsapi.infrastructure.persistence.entity.UsuarioEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class LoteJpaRepositoryTest extends PostgresContainerConfig {

    @Autowired
    private LoteJpaRepository repository;

    @Autowired
    private UsuarioJpaRepository usuarioRepository;

    @Autowired
    private FincaJpaRepository fincaRepository;

    @Autowired
    private PlantaJpaRepository plantaRepository;

    @Test
    @DisplayName("Should return true when lote already exists by fincaId and nombre ignore case")
    void shouldReturnTrueWhenLoteAlreadyExists(){
        // GIVEN
        usuarioRepository.save(
                new UsuarioEntity(
                        UsuarioFixtures.NOMBRE,
                        UsuarioFixtures.EMAIL,
                        UsuarioFixtures.PASSWORD
                )
        );

        fincaRepository.save(
                new FincaEntity(
                        FincaFixtures.NOMBRE,
                        FincaFixtures.USUARIO_ID
                )
        );

        plantaRepository.save(
                new PlantaEntity(
                        PlantaFixtures.NOMBRE
                )
        );

        repository.save(
                new LoteEntity(
                        LoteFixtures.NOMBRE,
                        LoteFixtures.FINCA_ID,
                        LoteFixtures.TIPO_CULTIVO_ID
                )
        );

        // WHEN
        boolean exists = repository.existsByNombreIgnoreCaseAndFincaId(
                LoteFixtures.NOMBRE.toUpperCase(),
                LoteFixtures.FINCA_ID
        );

        // THEN
        assertTrue(exists);
    }

    @Test
    @DisplayName("Should return false when lote does not exists by fincaId and nombre ignore case")
    void shouldReturnFalseWhenLoteDoesNotExists(){
        // WHEN
        boolean exists = repository.existsByNombreIgnoreCaseAndFincaId(
                "Otro lote",
                LoteFixtures.FINCA_ID + 1
        );

        // THEN
        assertFalse(exists);
    }

}
