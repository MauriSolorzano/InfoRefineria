package com.InfoRefineria.Controller;

import com.InfoRefineria.DTO.AuthRequest;
import com.InfoRefineria.DTO.AuthResponse;
import com.InfoRefineria.Entity.Usuario;
import com.InfoRefineria.Repository.UsuarioRepository;
import com.InfoRefineria.Service.UserDetailsServiceImp;
import com.InfoRefineria.Utils.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtil;
    private final UsuarioRepository usuarioRepository;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtUtils jwtUtil,
                          UsuarioRepository usuarioRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.usuarioRepository = usuarioRepository;
    }

    @PostMapping("/auth")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        try {
            // 1 - Autenticar usuario y contraseña
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(), request.getPassword())
            );

            // 2 - Buscar el usuario en la BD para obtener planta y rol
            Usuario usuario = usuarioRepository
                    .findByUsernameAndActivoTrue(auth.getName())
                    .orElseThrow();

            // 3 - Generar token JWT
            String token = jwtUtil.createToken(auth);

            // 4 - Armar respuesta con token + datos de planta
            String nombrePlanta = usuario.getPlanta() != null
                    ? usuario.getPlanta().getNombreDisplay()
                    : "Todas las plantas";

            String planta = usuario.getPlanta() != null
                    ? usuario.getPlanta().getNombre()
                    : "TODAS";

            return ResponseEntity.ok(new AuthResponse(
                    token,
                    usuario.getUsername(),
                    usuario.getRol(),
                    planta,
                    nombrePlanta
            ));

        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Usuario o contraseña incorrectos");
        }
    }
}
