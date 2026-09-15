package com.generation.vidafit.service;

import com.generation.vidafit.dto.LoginDTO;
import com.generation.vidafit.dto.UsuarioRequestDTO;
import com.generation.vidafit.dto.UsuarioResponseDTO;
import com.generation.vidafit.model.Rol;
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

        // Todo usuario registrado desde el formulario siempre será CLIENTE
        usuario.setRol(Rol.CLIENTE);

        Usuario guardado = usuarioRepository.save(usuario);
        return mapearAUsuarioResponseDTO(guardado);
    }

    @Transactional(readOnly = true)
    public Optional<UsuarioResponseDTO> autenticar(LoginDTO datos) {
        if (datos == null || datos.getCorreo() == null || datos.getContrasena() == null) {
            return Optional.empty();
        }

        return usuarioRepository.findByEmail(datos.getCorreo())
                .filter(usuario -> java.util.Objects.equals(usuario.getPasswordHash(), datos.getContrasena()))
                .map(this::mapearAUsuarioResponseDTO);
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

        // Si usuario.getRol() es null, se asigna CLIENTE por defecto en el DTO
        List<String> roles = (usuario.getRol() != null)
                ? List.of(usuario.getRol().name())
                : List.of(Rol.CLIENTE.name());

        return new UsuarioResponseDTO(
                usuario.getId(),
                usuario.getNombre() != null ? usuario.getNombre() : "",
                usuario.getEmail() != null ? usuario.getEmail() : "",
                roles
        );
    }
}