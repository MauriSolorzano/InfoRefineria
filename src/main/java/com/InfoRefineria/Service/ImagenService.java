package com.InfoRefineria.Service;

import com.InfoRefineria.Entity.Imagen;
import com.InfoRefineria.Entity.Sector;
import com.InfoRefineria.Repository.ImagenRepository;
import com.InfoRefineria.Repository.SectorRepository;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
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

    public List<Imagen> guardarImagen(MultipartFile archivo, String nombreSector, String nombrePlanta) throws Exception {
        if (archivo.isEmpty())
            throw new IllegalArgumentException("El archivo no puede estar vacío");
        if (!TIPOS_PERMITIDOS.contains(archivo.getContentType()))
            throw new IllegalArgumentException("Tipo de archivo no permitido");

        Sector sector = sectorRepository
                .findByNombreAndPlantaNombre(nombreSector.toUpperCase(), nombrePlanta.toUpperCase())
                .orElseThrow(() -> new IllegalArgumentException("Sector no encontrado: " + nombreSector));

        // ── Si es PDF, extraer páginas ──
        if ("application/pdf".equals(archivo.getContentType())) {
            return guardarPaginasPDF(archivo, sector, nombreSector, nombrePlanta);
        }

        // ── Si es imagen normal, flujo original ──
        return List.of(guardarArchivoSimple(archivo, sector, nombreSector, nombrePlanta));
    }

    private List<Imagen> guardarPaginasPDF(MultipartFile archivo, Sector sector,
                                           String nombreSector, String nombrePlanta) throws Exception {
        List<Imagen> guardadas = new ArrayList<>();

        try (PDDocument pdf = PDDocument.load(archivo.getInputStream())) {
            PDFRenderer renderer = new PDFRenderer(pdf);
            int totalPaginas = pdf.getNumberOfPages();

            for (int i = 0; i < totalPaginas; i++) {
                // Renderizar página a imagen (150 DPI es suficiente para pantallas)
                BufferedImage imagen = renderer.renderImageWithDPI(i, 150, ImageType.RGB);

                // Convertir BufferedImage a bytes PNG
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(imagen, "png", baos);
                byte[] bytes = baos.toByteArray();

                // Crear un MultipartFile "falso" para reutilizar subirArchivo()
                String nombrePagina = archivo.getOriginalFilename()
                        .replace(".pdf", "") + "_pagina_" + (i + 1) + ".png";

                MultipartFile paginaFile = new MockMultipartFile(
                        nombrePagina, nombrePagina, "image/png", bytes
                );

                Imagen guardada = guardarArchivoSimple(paginaFile, sector, nombreSector, nombrePlanta);
                guardadas.add(guardada);
            }
        }

        notificationService.notificarCambio(nombrePlanta, nombreSector);
        return guardadas;
    }

    private Imagen guardarArchivoSimple(MultipartFile archivo, Sector sector,
                                        String nombreSector, String nombrePlanta) throws Exception {
        String urlPublica = storageService.subirArchivo(archivo, nombrePlanta, nombreSector);

        String plantaLimpia = nombrePlanta.toUpperCase().trim().replace(" ", "_");
        String sectorLimpio = nombreSector.toUpperCase().trim().replace(" ", "_");
        String nombreArchivoConUUID = urlPublica.substring(urlPublica.lastIndexOf("/") + 1);
        String storagePath = plantaLimpia + "/" + sectorLimpio + "/" + nombreArchivoConUUID;

        Imagen imagen = new Imagen();
        imagen.setSector(sector);
        imagen.setNombreArchivo(archivo.getOriginalFilename());
        imagen.setStoragePath(storagePath);
        imagen.setUrlPublica(urlPublica);
        imagen.setSubidaEn(LocalDateTime.now());

        return imagenRepository.save(imagen);
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

