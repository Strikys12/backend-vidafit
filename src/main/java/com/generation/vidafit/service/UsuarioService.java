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
    public List<UsuarioResponseDTO> listarUsuarios(){
        return usuarioRepository.findAll()
                .stream()
                .map(this::mapearAUsuarioResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public Optional<UsuarioResponseDTO> obtenerUsuarioPorId(Long id){
        return usuarioRepository.findById(id)
                .map(this::mapearAUsuarioResponseDTO);
    }

    @Transactional
    public UsuarioResponseDTO crearUsuario(UsuarioRequestDTO datos){
        Usuario usuario = new Usuario();
        usuario.setNombre(datos.getNombre());
        usuario.setCorreo(datos.getCorreo());
        usuario.setContrasena(datos.getContrasena());
        return mapearAUsuarioResponseDTO(usuarioRepository.save(usuario));
    }

    @Transactional
    public Optional<UsuarioResponseDTO> actualizarUsuario(Long id, UsuarioRequestDTO datos){
        return usuarioRepository.findById(id)
                .map(usuario ->{
                    usuario.setNombre(datos.getNombre());
                    usuario.setCorreo(datos.getCorreo());
                    usuario.setContrasena(datos.getContrasena());
                    Usuario actualizado = usuarioRepository.save(usuario);
                    return mapearAUsuarioResponseDTO(actualizado);

                });
    }

    public boolean eliminarUsuario(Long id){
        if(!usuarioRepository.existsById(id)){
            return false;
        }
        usuarioRepository.deleteById(id);
        return true;
    }

    private UsuarioResponseDTO mapearAUsuarioResponseDTO(Usuario usuario){
        List<String> roles = Optional.ofNullable(usuario.getRoles())
                .orElse(List.of())
                .stream()
                .map(rol -> {
                    if (rol == null || rol.getNombre() == null) return "";
                    Object nombre = rol.getNombre();
                    return (nombre instanceof Enum) ? ((Enum<?>) nombre).name() : nombre.toString();
                })
                .filter(s -> !s.isBlank())
                .toList();
        return new UsuarioResponseDTO(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getCorreo(),
                roles
        );
    }
}
