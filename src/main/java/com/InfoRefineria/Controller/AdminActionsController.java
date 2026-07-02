package com.InfoRefineria.Controller;

import com.InfoRefineria.Entity.Planta;
import com.InfoRefineria.Entity.Sector;
import com.InfoRefineria.Entity.Usuario;
import com.InfoRefineria.Service.AdminService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

@RestController
@RequestMapping("/api/admin-actions")
public class AdminActionsController {
    private final AdminService adminService;
    private final ObjectMapper objectMapper;

    public AdminActionsController(AdminService adminService, ObjectMapper objectMapper) {
        this.adminService = adminService;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/plantas/{id}/payload/{payload}")
    public ResponseEntity<?> actualizarPlanta(@PathVariable Long id, @PathVariable String payload) {
        try {
            Planta planta = adminService.actualizarPlanta(id, decodePayload(payload));
            return ResponseEntity.ok(planta);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/sectores/{id}/payload/{payload}")
    public ResponseEntity<?> actualizarSector(@PathVariable Long id, @PathVariable String payload) {
        try {
            Sector sector = adminService.actualizarSector(id, decodePayload(payload));
            return ResponseEntity.ok(sector);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/usuarios/{id}/payload/{payload}")
    public ResponseEntity<?> actualizarUsuario(@PathVariable Long id, @PathVariable String payload) {
        try {
            Usuario usuario = adminService.actualizarUsuario(id, decodePayload(payload));
            return ResponseEntity.ok(usuario);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    private Map<String, Object> decodePayload(String payload) throws Exception {
        int padding = (4 - payload.length() % 4) % 4;
        String normalizedPayload = payload + "=".repeat(padding);
        byte[] decoded = Base64.getUrlDecoder().decode(normalizedPayload);
        String json = new String(decoded, StandardCharsets.UTF_8);
        return objectMapper.readValue(json, new TypeReference<>() {});
    }
}
