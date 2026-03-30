package com.InfoRefineria.Repository;

import com.InfoRefineria.Entity.Planta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PlantaRepository extends JpaRepository<Planta, Long> {
    List<Planta> findByActivaTrue();
    Optional<Planta> findByNombre(String nombre);
}
