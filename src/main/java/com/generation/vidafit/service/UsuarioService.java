package com.generation.vidafit.service;

import com.generation.vidafit.dto.UsuarioRequestDTO;
import com.generation.vidafit.dto.UsuarioResponseDTO;
import com.generation.vidafit.model.Usuario;
import com.generation.vidafit.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listarUsuarios() {
        return usuarioRepository.findAll()
                .stream()
                .map(this::mapearAUsuarioResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public Optional<UsuarioResponseDTO> obtenerUsuarioPorId(Long id) {
        return usuarioRepository.findById(id)
                .map(this::mapearAUsuarioResponseDTO);
    }

    @Transactional
    public UsuarioResponseDTO crearUsuario(UsuarioRequestDTO datos) {
        if (datos.getCorreo() == null || datos.getCorreo().isBlank()) {
            throw new IllegalArgumentException("El correo es obligatorio");
        }

        if (usuarioRepository.existsByEmail(datos.getCorreo())) {
            throw new IllegalArgumentException("El correo ya se encuentra registrado");
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(datos.getNombre());
        usuario.setEmail(datos.getCorreo());
        usuario.setPasswordHash(datos.getContrasena());

        Usuario guardado = usuarioRepository.save(usuario);
        return mapearAUsuarioResponseDTO(guardado);
    }

    @Transactional
    public Optional<UsuarioResponseDTO> actualizarUsuario(Long id, UsuarioRequestDTO datos) {
        return usuarioRepository.findById(id)
                .map(usuario -> {

                    if (datos.getNombre() != null && !datos.getNombre().isBlank()) {
                        usuario.setNombre(datos.getNombre());
                    }

                    if (datos.getCorreo() != null && !datos.getCorreo().isBlank()) {
                        if (!datos.getCorreo().equals(usuario.getEmail()) && usuarioRepository.existsByEmail(datos.getCorreo())) {
                            throw new IllegalArgumentException("El correo ya está registrado por otro usuario");
                        }
                        usuario.setEmail(datos.getCorreo());
                    }

                    if (datos.getContrasena() != null && !datos.getContrasena().isBlank()) {
                        usuario.setPasswordHash(datos.getContrasena());
                    }

                    Usuario actualizado = usuarioRepository.save(usuario);
                    return mapearAUsuarioResponseDTO(actualizado);
                });
    }

    @Transactional
    public boolean eliminarUsuario(Long id) {
        if (!usuarioRepository.existsById(id)) {
            return false;
        }
        usuarioRepository.deleteById(id);
        return true;
    }

    private UsuarioResponseDTO mapearAUsuarioResponseDTO(Usuario usuario) {
        if (usuario == null) {
            return null;
        }

        // Se obtiene el nombre del Enum Rol si no es nulo
        List<String> roles = (usuario.getRol() != null)
                ? List.of(usuario.getRol().name())
                : List.of();

        return new UsuarioResponseDTO(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getEmail(),
                roles
        );
    }
}