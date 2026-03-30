package com.InfoRefineria.Controller;

import com.InfoRefineria.Entity.Planta;
import com.InfoRefineria.Entity.Sector;
import com.InfoRefineria.Entity.Usuario;
import com.InfoRefineria.Service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    // ─── PLANTAS ───
    @PostMapping("/plantas")
    public ResponseEntity<?> crearPlanta(@RequestBody Map<String, String> body) {
        try {
            Planta planta = adminService.crearPlanta(
                    body.get("nombre"),
                    body.get("nombreDisplay")
            );
            return ResponseEntity.ok(planta);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/plantas/{id}")
    public ResponseEntity<?> actualizarPlanta(@PathVariable Long id,
                                              @RequestBody Map<String, Object> body) {
        try {
            Planta planta = adminService.actualizarPlanta(id, body);
            return ResponseEntity.ok(planta);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ─── SECTORES ───
    @PostMapping("/sectores")
    public ResponseEntity<?> crearSector(@RequestBody Map<String, String> body) {
        try {
            Sector sector = adminService.crearSector(
                    body.get("nombre"),
                    body.get("nombreDisplay"),
                    Long.parseLong(body.get("plantaId"))
            );
            return ResponseEntity.ok(sector);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/sectores/{id}")
    public ResponseEntity<?> actualizarSector(@PathVariable Long id,
                                              @RequestBody Map<String, Object> body) {
        try {
            Sector sector = adminService.actualizarSector(id, body);
            return ResponseEntity.ok(sector);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ─── USUARIOS ───
    @GetMapping("/usuarios")
    public ResponseEntity<?> listarUsuarios() {
        return ResponseEntity.ok(adminService.listarUsuarios());
    }

    @PostMapping("/usuarios")
    public ResponseEntity<?> crearUsuario(@RequestBody Map<String, Object> body) {
        try {
            Usuario usuario = adminService.crearUsuario(body);
            return ResponseEntity.ok(usuario);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/usuarios/{id}")
    public ResponseEntity<?> actualizarUsuario(@PathVariable Long id,
                                               @RequestBody Map<String, Object> body) {
        try {
            Usuario usuario = adminService.actualizarUsuario(id, body);
            return ResponseEntity.ok(usuario);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
