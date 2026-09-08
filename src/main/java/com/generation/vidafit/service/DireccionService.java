package com.generation.vidafit.service;

import com.generation.vidafit.dto.DireccionRequestDTO;
import com.generation.vidafit.dto.DireccionResponseDTO;
import com.generation.vidafit.model.Direccion;
import com.generation.vidafit.repository.DireccionRepository;
import com.generation.vidafit.repository.UsuarioRepository;
import com.generation.vidafit.repository.PedidoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class DireccionService {

    private final DireccionRepository direccionRepository;
    private final UsuarioRepository usuarioRepository;
    private final PedidoRepository pedidoRepository;

    public DireccionService(DireccionRepository direccionRepository,
                            UsuarioRepository usuarioRepository,
                            PedidoRepository pedidoRepository) {
        this.direccionRepository = direccionRepository;
        this.usuarioRepository = usuarioRepository;
        this.pedidoRepository = pedidoRepository;
    }

    @Transactional(readOnly = true)
    public List<DireccionResponseDTO> listarDirecciones() {
        return direccionRepository.findAll()
                .stream()
                .map(this::mapearADireccionResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public Optional<DireccionResponseDTO> buscarPorId(Long id) {
        return direccionRepository.findById(id)
                .map(this::mapearADireccionResponseDTO);
    }

    @Transactional
    public DireccionResponseDTO crearDireccion(DireccionRequestDTO datos) {
        if (!usuarioRepository.existsById(datos.getUserId())) {
            throw new IllegalArgumentException("Usuario no existe");
        }

        Direccion d = new Direccion();
        d.setUserId(datos.getUserId());
        d.setDireccionExacta(datos.getDireccionExacta());
        d.setBarrio(datos.getBarrio());
        d.setComuna(datos.getComuna());
        d.setCiudad(datos.getCiudad());
        d.setDepartamento(datos.getDepartamento());

        Direccion creado = direccionRepository.save(d);
        return mapearADireccionResponseDTO(creado);
    }

    @Transactional
    public Optional<DireccionResponseDTO> actualizarDireccion(Long id, DireccionRequestDTO datos) {
        return direccionRepository.findById(id)
                .map(d -> {
                    d.setDireccionExacta(datos.getDireccionExacta());
                    d.setBarrio(datos.getBarrio());
                    d.setComuna(datos.getComuna());
                    d.setCiudad(datos.getCiudad());
                    d.setDepartamento(datos.getDepartamento());
                    Direccion actualizado = direccionRepository.save(d);
                    return mapearADireccionResponseDTO(actualizado);
                });
    }

    public boolean eliminarDireccion(Long id) {
        if (!direccionRepository.existsById(id)) {
            return false;
        }
        if (pedidoRepository.existsByDireccionId(id)) {
            return false;
        }
        direccionRepository.deleteById(id);
        return true;
    }

    private DireccionResponseDTO mapearADireccionResponseDTO(Direccion d) {
        return new DireccionResponseDTO(
                d.getId(),
                d.getUsuario().getId(),
                d.getDireccionExacta(),
                d.getBarrio(),
                d.getComuna(),
                d.getCiudad(),
                d.getDepartamento()
        );
    }
}