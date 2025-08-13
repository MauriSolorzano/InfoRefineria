package com.InfoRefineria.Service;

import com.InfoRefineria.Entity.Imagen;
import com.InfoRefineria.Entity.Sector;
import com.InfoRefineria.Repository.ImagenRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
public class ImagenService {

    private final ImagenRepository imagenRepository;
    private final Path directorioImagenes = Paths.get("imagenes");
    private final Set<String> tiposPermitidos = Set.of("image/jpeg", "image/png", "image/gif", "image/webp");

    public ImagenService(ImagenRepository imagenRepository) throws Exception {
        this.imagenRepository = imagenRepository;
        Files.createDirectories(directorioImagenes);
    }

    public String guardarImagenes(MultipartFile archivo, String sectorStr) throws IOException {
        if (archivo.isEmpty()){
            throw new  IllegalArgumentException("El archivo no puede estar vacio");
        }
        if (!tiposPermitidos.contains(archivo.getContentType())){
            throw  new IllegalArgumentException("Tipo de archivo no permitido");
        }

        Sector sector = Sector.valueOf(sectorStr.toUpperCase());
        String nombreArchivo = UUID.randomUUID() + "_" + archivo.getOriginalFilename();
        Path rutaArchivo = directorioImagenes.resolve(nombreArchivo);
        archivo.transferTo(rutaArchivo);

        Imagen imagen = new Imagen();
        imagen.setNombreArchivo(nombreArchivo);
        imagen.setSector(sector);

        imagenRepository.save(imagen);
        return nombreArchivo;
    }

    public List<String> obtenerRutasPorSector(String sectorStr) {
        try {
            Sector sector = Sector.valueOf(sectorStr.toUpperCase());
            List<Imagen> imagenes = imagenRepository.findBySector(sector);
            return imagenes.stream()
                    .map(img -> "/imagenes/" + img.getNombreArchivo())
                    .toList();
        } catch (IllegalArgumentException e){
            throw new IllegalArgumentException("Sector no valido:" + sectorStr);
        }
    }

    public List<Map<String, Object>> obtenerImagenesCompletasPorSector(String sectorStr) {
        try {
            Sector sector = Sector.valueOf(sectorStr.toUpperCase());
            List<Imagen> imagenes = imagenRepository.findBySector(sector);
            return imagenes.stream()
                    .map(img -> {
                        Map<String, Object> imageData = new HashMap<>();
                        imageData.put("id", img.getId());
                        imageData.put("nombreArchivo", img.getNombreArchivo());
                        imageData.put("ruta", "/imagenes/" + img.getNombreArchivo());
                        imageData.put("sector", img.getSector());
                        return imageData;
                    })
                    .toList();
        } catch (IllegalArgumentException e){
            throw new IllegalArgumentException("Sector no valido:" + sectorStr);
        }
    }


    public void deleteImageById(Long id){
        Imagen imagen = imagenRepository.findById(id)
                .orElseThrow(()-> new ImagenNotFoundException("No se encontro la imagene con ID: " + id));

        try{
            if (imagen.getNombreArchivo()!= null){
                Path rutaArchivo = directorioImagenes.resolve(imagen.getNombreArchivo());
                Files.deleteIfExists(rutaArchivo);
            }
            imagenRepository.delete(imagen);
        }catch (IOException e){
            throw new RuntimeException("Error al eliminar el archivo fisico: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar la imagen: " + e.getMessage(), e);
        }
    }

    public void deleteImageBySector(Sector sector){
        try{
            List<Imagen> imagenes = imagenRepository.findBySector(sector);

            if (imagenes.isEmpty()){
                throw new RuntimeException("No se encontraron imagenes en el sector: " + sector);
            }

            for (Imagen imagen : imagenes) {
                try {
                    Path rutaArchivo = Paths.get(imagen.getNombreArchivo());
                    Files.deleteIfExists(rutaArchivo);
                } catch (IOException e) {
                    System.err.println("Error al eliminar archivo físico: " + imagen.getNombreArchivo());
                }
            }
            imagenRepository.deleteBySector(sector);
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar imagenes del sector: " + e.getMessage());
        }
    }

    // CLASE DE EXCEPCIÓN (agregar en un archivo separado o al final del mismo archivo)
    public class ImagenNotFoundException extends RuntimeException {
        public ImagenNotFoundException(String message) {
            super(message);
        }
    }
}