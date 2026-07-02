package com.InfoRefineria.Repository;

import com.InfoRefineria.Entity.Imagen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImagenRepository extends JpaRepository<Imagen, Long> {
    List<Imagen> findBySectorIdOrderByOrdenAscSubidaEnAscIdAsc(Long sectorId);

}
