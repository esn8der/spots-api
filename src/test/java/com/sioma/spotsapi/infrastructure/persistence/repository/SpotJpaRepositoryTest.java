package com.sioma.spotsapi.infrastructure.persistence.repository;

import com.sioma.spotsapi.domain.model.Lote;
import com.sioma.spotsapi.fixtures.*;
import com.sioma.spotsapi.infrastructure.config.PostgresContainerConfig;
import com.sioma.spotsapi.infrastructure.persistence.entities.*;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase.Replace.NONE;

@DataJpaTest
@Import(PostgresContainerConfig.class)
@AutoConfigureTestDatabase(replace = NONE)
class SpotJpaRepositoryTest {

    @Autowired
    private SpotJpaRepository repository;

    @Autowired
    private UsuarioJpaRepository usuarioRepository;

    @Autowired
    private FincaJpaRepository fincaRepository;

    @Autowired
    private PlantaJpaRepository plantaRepository;

    @Autowired
    private LoteJpaRepository loteJpaRepository;

    @Test
    void shouldReturnTrueWhenSpotExistsByLoteIdAndLineaAndPosicion() {
        // GIVEN
        LoteEntity lote = createLote();

        Point point = SpotFixtures.validPoint();

        repository.save(new SpotEntity(
                        point,
                        lote.getId(),
                        SpotFixtures.LINEA,
                        SpotFixtures.POSICION
                )
        );

        // WHEN
        boolean exists = repository.existsByLoteIdAndLineaAndPosicion(
                lote.getId(),
                SpotFixtures.LINEA,
                SpotFixtures.POSICION
        );

        // THEN
        assertTrue(exists);
    }

    @Test
    void shouldReturnFalseWhenSpotDoesNotExistByLoteIdAndLineaAndPosicion() {
        // GIVEN
        LoteEntity lote = createLote();

        // WHEN
        boolean exists = repository.existsByLoteIdAndLineaAndPosicion(
                lote.getId(),
                SpotFixtures.LINEA,
                SpotFixtures.POSICION + 1
        );

        // THEN
        assertFalse(exists);
    }

    private LoteEntity createLote() {
        UsuarioEntity usuario = usuarioRepository.save(
                new UsuarioEntity(
                        UsuarioFixtures.NOMBRE,
                        UsuarioFixtures.EMAIL,
                        UsuarioFixtures.PASSWORD
                )
        );

        FincaEntity finca = fincaRepository.save(
                new FincaEntity(
                        FincaFixtures.NOMBRE,
                        usuario.getId()
                )
        );

        PlantaEntity planta = plantaRepository.save(
                new PlantaEntity(
                        PlantaFixtures.NOMBRE
                )
        );

        Lote lotePolygon = LoteFixtures.loteContainingPoint(SpotFixtures.validPoint());

        return loteJpaRepository.save(
                new LoteEntity(
                        lotePolygon.getNombre(),
                        lotePolygon.getGeocerca(),
                        finca.getId(),
                        planta.getId()
                )
        );

    }
}
