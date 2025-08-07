package com.InfoRefineria.Controller;

import com.InfoRefineria.Entity.Imagen;
import com.InfoRefineria.Service.ImagenService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:5500", allowCredentials = "true")
@Controller
@RequestMapping("/api/imagenes")
public class ImagenController {
    private final ImagenService imagenService;

    public ImagenController(ImagenService imagenService){
        this.imagenService = imagenService;
    }

    @PostMapping
    public ResponseEntity<String> subirImagen(@RequestParam("imagen")MultipartFile archivo, @RequestParam("sector")String sector){
        try{
            imagenService.guardarImagenes(archivo,sector);
            return ResponseEntity.ok("Se subio correctamente la imagen");
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al subir la imagen: " + e.getMessage());
        }
    }

    @GetMapping("/{sector}")
    public ResponseEntity<List<Map<String, Object>>> obtenerImagenesPorSector(@PathVariable String sector){
        try {
            List<Map<String, Object>> imagenes = imagenService.obtenerImagenesCompletasPorSector(sector);
            return ResponseEntity.ok(imagenes);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteImage(@PathVariable Long id) {
        try {
            imagenService.deleteImageById(id);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Imagen eliminada exitosamente");
            response.put("id", id.toString());

            return ResponseEntity.ok(response);

        } catch (ImagenService.ImagenNotFoundException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al eliminar la imagen");
            return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }


}
