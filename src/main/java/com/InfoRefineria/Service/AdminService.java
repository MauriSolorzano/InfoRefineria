package com.InfoRefineria.Service;

import com.InfoRefineria.Entity.Planta;
import com.InfoRefineria.Entity.Sector;
import com.InfoRefineria.Entity.Usuario;
import com.InfoRefineria.Repository.PlantaRepository;
import com.InfoRefineria.Repository.SectorRepository;
import com.InfoRefineria.Repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class AdminService {
    private final PlantaRepository plantaRepository;
    private final SectorRepository sectorRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminService(PlantaRepository plantaRepository,
                        SectorRepository sectorRepository,
                        UsuarioRepository usuarioRepository,
                        PasswordEncoder passwordEncoder) {
        this.plantaRepository = plantaRepository;
        this.sectorRepository = sectorRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // ─── PLANTAS ───
    public Planta crearPlanta(String nombre, String nombreDisplay) {
        if (plantaRepository.findByNombre(nombre.toUpperCase()).isPresent())
            throw new IllegalArgumentException("Ya existe una planta con ese nombre");

        Planta planta = new Planta();
        planta.setNombre(nombre.toUpperCase());
        planta.setNombreDisplay(nombreDisplay);
        planta.setActiva(true);
        planta.setCreadaEn(LocalDateTime.now());
        return plantaRepository.save(planta);
    }

    public Planta actualizarPlanta(Long id, Map<String, Object> body) {
        Planta planta = plantaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Planta no encontrada"));

        if (body.containsKey("nombreDisplay"))
            planta.setNombreDisplay((String) body.get("nombreDisplay"));
        if (body.containsKey("activa"))
            planta.setActiva((Boolean) body.get("activa"));

        return plantaRepository.save(planta);
    }

    // ─── SECTORES ───
    public Sector crearSector(String nombre, String nombreDisplay, Long plantaId) {
        Planta planta = plantaRepository.findById(plantaId)
                .orElseThrow(() -> new IllegalArgumentException("Planta no encontrada"));

        String nombreLimpio = nombre.toUpperCase().replace(" ", "_");

        Sector sector = new Sector();
        sector.setNombre(nombre.toUpperCase());
        sector.setNombreDisplay(nombreDisplay);
        sector.setPlanta(planta);
        sector.setActivo(true);
        return sectorRepository.save(sector);
    }

    public Sector actualizarSector(Long id, Map<String, Object> body) {
        Sector sector = sectorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Sector no encontrado"));

        if (body.containsKey("nombreDisplay"))
            sector.setNombreDisplay((String) body.get("nombreDisplay"));
        if (body.containsKey("activo"))
            sector.setActivo((Boolean) body.get("activo"));

        return sectorRepository.save(sector);
    }

    // ─── USUARIOS ───
    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    public Usuario crearUsuario(Map<String, Object> body) {
        String username = (String) body.get("username");
        String password = (String) body.get("password");
        String rol = (String) body.get("rol");

        if (username == null || username.isBlank())
            throw new IllegalArgumentException("El username es obligatorio");
        if (password == null || password.isBlank())
            throw new IllegalArgumentException("La contraseña es obligatoria");
        if (rol == null || rol.isBlank())
            throw new IllegalArgumentException("El rol es obligatorio");

        if (usuarioRepository.findByUsernameAndActivoTrue(username).isPresent())
            throw new IllegalArgumentException("El usuario ya existe");

        Usuario usuario = new Usuario();
        usuario.setUsername(username);
        usuario.setPasswordHash(passwordEncoder.encode(password));
        usuario.setRol(rol);
        usuario.setActivo(true);

        if (usaPlanta(rol)) {
            Long plantaId = obtenerPlantaIdObligatoria(body.get("plantaId"));
            Planta planta = plantaRepository.findById(plantaId)
                    .orElseThrow(() -> new IllegalArgumentException("Planta no encontrada"));
            usuario.setPlanta(planta);
        } else {
            usuario.setPlanta(null);
        }

        if (usaSectores(rol)) {
            usuario.setSectores(obtenerSectores(body.get("sectorIds")));
        } else {
            usuario.setSectores(new ArrayList<>());
        }

        return usuarioRepository.save(usuario);
    }

    public Usuario actualizarUsuario(Long id, Map<String, Object> body) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        if (body.containsKey("password") && body.get("password") != null && !body.get("password").toString().isBlank())
            usuario.setPasswordHash(passwordEncoder.encode(body.get("password").toString()));
        if (body.containsKey("activo"))
            usuario.setActivo((Boolean) body.get("activo"));

        String rol = body.containsKey("rol") ? (String) body.get("rol") : usuario.getRol();
        if (rol == null || rol.isBlank())
            throw new IllegalArgumentException("El rol es obligatorio");
        usuario.setRol(rol);

        if (!usaPlanta(rol)) {
            usuario.setPlanta(null);
        } else if (body.containsKey("plantaId")) {
            Long plantaId = obtenerPlantaIdObligatoria(body.get("plantaId"));
            Planta planta = plantaRepository.findById(plantaId)
                    .orElseThrow(() -> new IllegalArgumentException("Planta no encontrada"));
            usuario.setPlanta(planta);
        }

        if (usaSectores(rol)) {
            if (body.containsKey("sectorIds")) {
                usuario.setSectores(obtenerSectores(body.get("sectorIds")));
            }
        } else if (body.containsKey("rol") || body.containsKey("sectorIds")) {
            usuario.setSectores(new ArrayList<>());
        }

        return usuarioRepository.save(usuario);
    }

    private boolean usaPlanta(String rol) {
        return !"SUPERADMIN".equals(rol);
    }

    private boolean usaSectores(String rol) {
        return "VISOR".equals(rol);
    }

    private Long obtenerPlantaIdObligatoria(Object plantaId) {
        if (plantaId == null || plantaId.toString().isBlank())
            throw new IllegalArgumentException("La planta es obligatoria");
        return Long.parseLong(plantaId.toString());
    }

    private List<Sector> obtenerSectores(Object rawSectorIds) {
        if (!(rawSectorIds instanceof List<?> sectorIds) || sectorIds.isEmpty())
            throw new IllegalArgumentException("Seleccioná al menos un sector");

        return sectorIds.stream()
                .map(sectorId -> Long.parseLong(sectorId.toString()))
                .map(sectorId -> sectorRepository.findById(sectorId)
                        .orElseThrow(() -> new IllegalArgumentException("Sector no encontrado: " + sectorId)))
                .toList();
    }


}
