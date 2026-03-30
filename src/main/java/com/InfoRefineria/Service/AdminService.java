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

    @SuppressWarnings("unchecked")
    public Usuario crearUsuario(Map<String, Object> body) {
        String username = (String) body.get("username");
        if (usuarioRepository.findByUsernameAndActivoTrue(username).isPresent())
            throw new IllegalArgumentException("El usuario ya existe");

        Usuario usuario = new Usuario();
        usuario.setUsername(username);
        usuario.setPasswordHash(passwordEncoder.encode((String) body.get("password")));
        usuario.setRol((String) body.get("rol"));
        usuario.setActivo(true);

        // Asignar planta si no es SUPERADMIN
        if (body.containsKey("plantaId") && body.get("plantaId") != null) {
            Long plantaId = Long.parseLong(body.get("plantaId").toString());
            plantaRepository.findById(plantaId).ifPresent(usuario::setPlanta);
        }

        // Asignar sectores si es ADMIN_PLANTA
        if (body.containsKey("sectorIds") && body.get("sectorIds") != null) {
            List<Integer> sectorIds = (List<Integer>) body.get("sectorIds");
            List<Sector> sectores = sectorIds.stream()
                    .map(sId -> sectorRepository.findById(Long.valueOf(sId)).orElseThrow())
                    .toList();
            usuario.setSectores(sectores);
        }

        return usuarioRepository.save(usuario);
    }

    @SuppressWarnings("unchecked")
    public Usuario actualizarUsuario(Long id, Map<String, Object> body) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        if (body.containsKey("password") && body.get("password") != null)
            usuario.setPasswordHash(passwordEncoder.encode((String) body.get("password")));
        if (body.containsKey("activo"))
            usuario.setActivo((Boolean) body.get("activo"));
        if (body.containsKey("rol"))
            usuario.setRol((String) body.get("rol"));

        if (body.containsKey("plantaId") && body.get("plantaId") != null) {
            Long plantaId = Long.parseLong(body.get("plantaId").toString());
            plantaRepository.findById(plantaId).ifPresent(usuario::setPlanta);
        }

        if (body.containsKey("sectorIds") && body.get("sectorIds") != null) {
            List<Integer> sectorIds = (List<Integer>) body.get("sectorIds");
            List<Sector> sectores = sectorIds.stream()
                    .map(sId -> sectorRepository.findById(Long.valueOf(sId)).orElseThrow())
                    .toList();
            usuario.setSectores(sectores);
        }

        return usuarioRepository.save(usuario);
    }


}
