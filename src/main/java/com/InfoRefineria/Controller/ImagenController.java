package com.InfoRefineria.Controller;

import com.InfoRefineria.Entity.Imagen;
import com.InfoRefineria.Entity.Sector;
import com.InfoRefineria.Service.ImagenService;
import com.InfoRefineria.Service.PdfImageExtractorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

//@CrossOrigin(origins = "http://localhost:5500", allowCredentials = "true")
@RestController
@RequestMapping("/api/imagenes")
public class ImagenController {

    private final ImagenService imagenService;

    public ImagenController(ImagenService imagenService) {
        this.imagenService = imagenService;
    }

    @PostMapping
    public ResponseEntity<?> subirImagen(
            @RequestParam("imagen") MultipartFile archivo,
            @RequestParam("sector") String sector,
            @RequestParam(value = "planta", defaultValue = "TODAS") String planta) {
        try {
            Imagen imagen = imagenService.guardarImagen(archivo, sector, planta);
            return ResponseEntity.ok(Map.of(
                    "message", "Imagen subida correctamente",
                    "urlPublica", imagen.getUrlPublica(),
                    "id", imagen.getId()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{sector}")
    public ResponseEntity<?> obtenerImagenes(
            @PathVariable String sector,
            @RequestParam(value = "planta", defaultValue = "TODAS") String planta) {
        try {
            List<Imagen> imagenes = imagenService.obtenerImagenesPorSector(sector, planta);
            return ResponseEntity.ok(imagenes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarImagen(@PathVariable Long id) {
        try {
            imagenService.eliminarImagenPorId(id);
            return ResponseEntity.ok(Map.of("message", "Imagen eliminada", "id", id));
        } catch (ImagenService.ImagenNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
