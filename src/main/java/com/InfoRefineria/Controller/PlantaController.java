package com.InfoRefineria.Controller;

import com.InfoRefineria.Entity.Planta;
import com.InfoRefineria.Entity.Sector;
import com.InfoRefineria.Service.PlantaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/plantas")
public class PlantaController {
    private final PlantaService plantaService;

    public PlantaController(PlantaService plantaService) {
        this.plantaService = plantaService;
    }

    @PostMapping
    public ResponseEntity<?> crearPlanta(@RequestBody Map<String, String> body){
        try{
            Planta planta = plantaService.crearPlanta(
                    body.get("nombre"),
                    body.get("nombreDisplay")
            );
            return ResponseEntity.ok(planta);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarPlanta(@PathVariable Long id,
                                              @RequestBody Map<String, Object> body) {
        try {
            String nombreDisplay = (String) body.get("nombreDisplay");
            Boolean activa = body.get("activa") != null ? (Boolean) body.get("activa") : null;

            Planta planta = plantaService.actualizarPlanta(id, nombreDisplay, activa);
            return ResponseEntity.ok(planta);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<List<Planta>> listadoPlantas(){
        return ResponseEntity.ok(plantaService.listadoPlantas());
    }

    @GetMapping("/{nombrePlanta}/sectores")
    public ResponseEntity<List<Sector>> getSectoresPorPlanta(@PathVariable String nombrePlanta) {
        return plantaService.sectoresPorNombrePlanta(nombrePlanta)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
