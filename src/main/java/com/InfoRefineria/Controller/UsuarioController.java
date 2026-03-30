package com.InfoRefineria.Controller;

import com.InfoRefineria.Entity.Sector;
import com.InfoRefineria.Entity.Usuario;
import com.InfoRefineria.Service.AdminService;
import com.InfoRefineria.Service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/visor")
public class UsuarioController {
    private final UsuarioService usuarioService;
    private final AdminService adminService;

    public UsuarioController(UsuarioService usuarioService, AdminService adminService) {
        this.usuarioService = usuarioService;
        this.adminService = adminService;
    }

    @GetMapping("/mis-sectores")
    public ResponseEntity<?> misSectores(Authentication authentication) {
        try {
            List<Sector> sectores = usuarioService.misSectores(authentication.getName());
            return ResponseEntity.ok(sectores);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
