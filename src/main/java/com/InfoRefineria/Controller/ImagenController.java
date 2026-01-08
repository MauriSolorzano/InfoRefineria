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
@Controller
@RequestMapping("/api/imagenes")
public class ImagenController {

    private final ImagenService imagenService;
    @Autowired
    private PdfImageExtractorService pdfImageExtractorService;
    public ImagenController(ImagenService imagenService){
        this.imagenService = imagenService;
    }

    private static final Logger logger = LoggerFactory.getLogger(ImagenController.class);

    // Post para cargar imagenes PNG, JPEG
    @PostMapping
    public ResponseEntity<String> subirImagen(@RequestParam("imagen")MultipartFile archivo,
                                              @RequestParam("sector")String sector,
                                              @RequestParam(value = "planta", required = false, defaultValue = "CORDOBA") String planta){
        try{
            imagenService.guardarImagenes(archivo,sector, planta);
            return ResponseEntity.ok("Se subio correctamente la imagen");
        }catch (Exception e){
            logger.error("Error al subir imagen al sector {}: {}", sector, e.getMessage(), e);
            e.printStackTrace();

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al subir la imagen: " + e.getMessage());
        }
    }

    //Post para cargar archivos PDF
    @PostMapping("/pdf")
    public ResponseEntity<Map<String, Object>> subirPDF(
            @RequestParam("pdf") MultipartFile archivo,
            @RequestParam("sector") String sector,
            @RequestParam(value = "planta", required = false, defaultValue = "CORDOBA") String planta){
        try{
            // Try para validar que el archivo sea PDF
            if (!archivo.getContentType().equals("application/pdf")){
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "El archivo debe ser PDF"));
            }
            List<String> imagenesExtraidas = pdfImageExtractorService.extraerImagenesDePdf(archivo, sector, planta);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "PDF procesado correctamente");
            response.put("totalImagenes", imagenesExtraidas.size());
            response.put("nombreArchivo", archivo.getOriginalFilename());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al procesar el PDF: " + e.getMessage()));
        }
    }


    @GetMapping("/{sector}")
    public ResponseEntity<List<Map<String, Object>>> obtenerImagenesPorSector(@PathVariable String sector,
                                                                              @RequestParam(value = "planta", required = false, defaultValue = "CORDOBA") String planta){
        try {
            List<Map<String, Object>> imagenes = imagenService.obtenerImagenesCompletasPorSector(sector, planta);
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

    @DeleteMapping("/sector/{sector}")
    public ResponseEntity<Map<String, Object>> deleteImagesBySector(@PathVariable Sector sector){
        try{
            imagenService.deleteImageBySector(sector);
            Map<String, Object> response = new HashMap<>();
            response.put("mesasge", " Todas las imagenes del sector: " + sector + "fueron eliminadas");
            response.put("sector", sector);

            return ResponseEntity.ok(response);
        }catch (RuntimeException e){
            Map<String, Object> error = new HashMap<>();
            error.put("error ", e.getMessage());
            error.put("sector: ", sector);

            if (e.getMessage().contains("No se encontraron")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
            }
        }
    }
}
