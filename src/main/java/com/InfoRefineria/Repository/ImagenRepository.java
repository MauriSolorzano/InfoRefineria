package com.InfoRefineria.Repository;

import com.InfoRefineria.Entity.Imagen;
import com.InfoRefineria.Entity.Sector;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImagenRepository extends JpaRepository<Imagen, Long> {
    List<Imagen> findBySector (Sector sector);

    @Modifying
    @Transactional
    void deleteBySector(Sector sector);
}
