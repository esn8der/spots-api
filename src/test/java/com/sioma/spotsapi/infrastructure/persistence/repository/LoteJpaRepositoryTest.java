package com.sioma.spotsapi.infrastructure.persistence.repository;

import com.sioma.spotsapi.fixtures.FincaFixtures;
import com.sioma.spotsapi.fixtures.LoteFixtures;
import com.sioma.spotsapi.fixtures.PlantaFixtures;
import com.sioma.spotsapi.fixtures.UsuarioFixtures;
import com.sioma.spotsapi.infrastructure.config.PostgresContainerConfig;
import com.sioma.spotsapi.infrastructure.persistence.entities.FincaEntity;
import com.sioma.spotsapi.infrastructure.persistence.entities.LoteEntity;
import com.sioma.spotsapi.infrastructure.persistence.entities.PlantaEntity;
import com.sioma.spotsapi.infrastructure.persistence.entities.UsuarioEntity;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Polygon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

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
    void shouldReturnTrueWhenLoteAlreadyExists(){
        // GIVEN
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

        Polygon geocerca = LoteFixtures.anyGeocerca();

        LoteEntity lote = repository.save(
                new LoteEntity(
                        LoteFixtures.NOMBRE,
                        geocerca,
                        finca.getId(),
                        planta.getId()
                )
        );

        // WHEN
        boolean exists = repository.existsByNombreIgnoreCaseAndFincaId(
                lote.getNombre().toUpperCase(),
                lote.getFincaId()
        );

        // THEN
        assertTrue(exists);
    }

    @Test
    void shouldReturnFalseWhenLoteDoesNotExists(){
        // WHEN
        boolean exists = repository.existsByNombreIgnoreCaseAndFincaId(
                "Otro lote",
                LoteFixtures.FINCA_ID + 1
        );

        // THEN
        assertFalse(exists);
    }

    @Test
    void shouldReturnEmptyListWhenNoLotesFound(){
        // GIVEN
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

        // WHEN
        List<LoteEntity> lotes = repository.findAllByFincaId(finca.getId());

        // THEN
        assertTrue(lotes.isEmpty());
    }

    @Test
    void shouldReturnLotesByFincaId(){
        // GIVEN
        UsuarioEntity usuario1 = usuarioRepository.save(
                new UsuarioEntity(
                        UsuarioFixtures.NOMBRE,
                        UsuarioFixtures.EMAIL,
                        UsuarioFixtures.PASSWORD
                )
        );
        UsuarioEntity usuario2 = usuarioRepository.save(
                new UsuarioEntity(
                        UsuarioFixtures.NOMBRE,
                        "otro@mail.com",
                        UsuarioFixtures.PASSWORD
                )
        );

        FincaEntity finca1 = fincaRepository.save(
                new FincaEntity(
                        FincaFixtures.NOMBRE,
                        usuario1.getId()
                )
        );
        FincaEntity finca2 = fincaRepository.save(
                new FincaEntity(
                        "Finca 2",
                        usuario2.getId()
                )
        );

        PlantaEntity planta = plantaRepository.save(
                new PlantaEntity(
                        PlantaFixtures.NOMBRE
                )
        );

        Polygon geocerca = LoteFixtures.anyGeocerca();

        repository.save(new LoteEntity("Lote 1", geocerca, finca1.getId(), planta.getId()));
        repository.save(new LoteEntity("Lote 2", geocerca, finca1.getId(), planta.getId()));
        repository.save(new LoteEntity("Lote 3", geocerca, finca2.getId(), planta.getId()));

        // WHEN
        List<LoteEntity> lotes = repository.findAllByFincaId(finca1.getId());

        // THEN
        assertEquals(2, lotes.size());
        assertTrue(lotes.stream()
                .allMatch(l -> l.getFincaId()
                        .equals(finca1.getId()))
        );
        assertEquals(Set.of("Lote 1", "Lote 2"), lotes.stream()
                .map(LoteEntity::getNombre)
                .collect(Collectors.toSet())
        );
    }
}
