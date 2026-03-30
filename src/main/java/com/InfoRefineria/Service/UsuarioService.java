package com.InfoRefineria.Service;

import com.InfoRefineria.Entity.Sector;
import com.InfoRefineria.Entity.Usuario;
import com.InfoRefineria.Repository.UsuarioRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public List<Sector> misSectores(String username){
        Usuario usuario = usuarioRepository.findByUsernameAndActivoTrue(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        return usuario.getSectores();
    }
}
