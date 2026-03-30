package com.InfoRefineria.Repository;

import com.InfoRefineria.Entity.Sector;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SectorRepository extends JpaRepository<Sector, Long> {
    List<Sector> findByPlantaIdAndActivoTrue(Long plantaId);
    Optional<Sector> findByNombreAndPlantaId(String nombre, Long plantaId);
    List<Sector> findByActivoTrue();
    @Query("SELECT s FROM Sector s WHERE s.nombre = :nombre AND s.planta.nombre = :nombrePlanta")
    Optional<Sector> findByNombreAndPlantaNombre(@Param("nombre") String nombre,
                                                 @Param("nombrePlanta") String nombrePlanta);
}
