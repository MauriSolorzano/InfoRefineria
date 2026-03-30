package com.InfoRefineria.Service;

import com.InfoRefineria.Entity.Imagen;
import com.InfoRefineria.Entity.Sector;
import com.InfoRefineria.Repository.ImagenRepository;
import com.InfoRefineria.Repository.SectorRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class ImagenService {
    private final ImagenRepository imagenRepository;
    private final SectorRepository sectorRepository;
    private final MinioStorageService storageService;
    private final SectorNotificationService notificationService;

    public ImagenService(ImagenRepository imagenRepository, SectorRepository sectorRepository, MinioStorageService storageService, SectorNotificationService notificationService) {
        this.imagenRepository = imagenRepository;
        this.sectorRepository = sectorRepository;
        this.storageService = storageService;
        this.notificationService = notificationService;
    }

    private static final Set<String> TIPOS_PERMITIDOS = Set.of(
            "image/jpeg", "image/png", "image/gif", "image/webp", "application/pdf"
    );

    public Imagen guardarImagen(MultipartFile archivo, String nombreSector, String nombrePlanta) throws Exception {
        if (archivo.isEmpty())
            throw new IllegalArgumentException("El archivo no puede estar vacío");
        if (!TIPOS_PERMITIDOS.contains(archivo.getContentType()))
            throw new IllegalArgumentException("Tipo de archivo no permitido");

        // Buscar el sector en la BD
        Sector sector = sectorRepository
                .findByNombreAndPlantaNombre(nombreSector.toUpperCase(), nombrePlanta.toUpperCase())
                .orElseThrow(() -> new IllegalArgumentException("Sector no encontrado: " + nombreSector));

        // Subir a Supabase Storage
        String urlPublica = storageService.subirArchivo(archivo, nombrePlanta, nombreSector);

        // Extraer el storagePath de la URL pública
        String storagePath = nombrePlanta.toUpperCase() + "/" +
                nombreSector.toUpperCase() + "/" +
                urlPublica.substring(urlPublica.lastIndexOf("/") + 1);

        // Guardar metadatos en la BD
        Imagen imagen = new Imagen();
        imagen.setSector(sector);
        imagen.setNombreArchivo(archivo.getOriginalFilename());
        imagen.setStoragePath(storagePath);
        imagen.setUrlPublica(urlPublica);
        imagen.setSubidaEn(LocalDateTime.now());

        Imagen guardada = imagenRepository.save(imagen);

        notificationService.notificarCambio(nombrePlanta, nombreSector);
        return guardada;
    }

    public List<Imagen> obtenerImagenesPorSector(String nombreSector, String nombrePlanta) {
        Sector sector = sectorRepository
                .findByNombreAndPlantaNombre(nombreSector.toUpperCase(), nombrePlanta.toUpperCase())
                .orElseThrow(() -> new IllegalArgumentException("Sector no encontrado"));

        return imagenRepository.findBySectorIdOrderByOrdenAsc(sector.getId());
    }

    public void eliminarImagenPorId(Long id) {
        Imagen imagen = imagenRepository.findById(id)
                .orElseThrow(() -> new ImagenNotFoundException("Imagen no encontrada: " + id));
        String sector = imagen.getSector().getNombre();
        String planta = imagen.getSector().getPlanta().getNombre();

        storageService.eliminarArchivo(imagen.getStoragePath());
        imagenRepository.delete(imagen);
        notificationService.notificarCambio(planta, sector);
    }

    public class ImagenNotFoundException extends RuntimeException {
        public ImagenNotFoundException(String message) { super(message); }
    }

}

