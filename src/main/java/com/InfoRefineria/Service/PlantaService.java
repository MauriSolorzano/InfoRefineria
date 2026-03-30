package com.InfoRefineria.Service;

import com.InfoRefineria.Entity.Planta;
import com.InfoRefineria.Entity.Sector;
import com.InfoRefineria.Repository.PlantaRepository;
import com.InfoRefineria.Repository.SectorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PlantaService {
    private static final Logger log = LoggerFactory.getLogger(PlantaService.class);
    private final PlantaRepository plantaRepository;
    private final SectorRepository sectorRepository;

    public PlantaService(PlantaRepository plantaRepository, SectorRepository sectorRepository) {
        this.plantaRepository = plantaRepository;
        this.sectorRepository = sectorRepository;
    }

    // Metodo para crear una planta
    public Planta crearPlanta(String nombre, String nombreDisplay){
        if (plantaRepository.findByNombre(nombre.toUpperCase()).isPresent())
            throw new IllegalArgumentException("Ya existe planta con dicho nombre");

        Planta planta = new Planta();
        planta.setNombre(nombre.toUpperCase());
        planta.setNombreDisplay(nombreDisplay);
        planta.setActiva(true);
        planta.setCreadaEn(LocalDateTime.now());
        return plantaRepository.save(planta);
    }

    // Metodo para modificar una planta
    public Planta actualizarPlanta(Long id, String nombreDisplay, Boolean activa) {
        Planta planta = plantaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Planta no encontrada"));

        if (nombreDisplay != null) planta.setNombreDisplay(nombreDisplay);
        if (activa != null) planta.setActiva(activa);

        return plantaRepository.save(planta);
    }

    // Metodo para listar
    public List<Planta> listadoPlantas() {
        List<Planta> plantas = plantaRepository.findAll();
        if (plantas.isEmpty()) {
            log.warn("Consulta de plantas: La base de datos no retornó resultados.");
        }
        return plantas;
    }

    // Metodo para listar sectores de una planta
    public Optional<List<Sector>> sectoresPorNombrePlanta(String nombrePlanta){
        // Superadmin ve todos los sectores de todas las plantas
        if (nombrePlanta.equalsIgnoreCase("TODAS")) {
            return Optional.of(sectorRepository.findByActivoTrue());
        }
        return plantaRepository.findByNombre(nombrePlanta.toUpperCase())
                .map(planta -> sectorRepository.findByPlantaIdAndActivoTrue(planta.getId()));
    }
}
