package com.generation.vidafit.controller;

import com.generation.vidafit.dto.DireccionRequestDTO;
import com.generation.vidafit.dto.DireccionResponseDTO;
import com.generation.vidafit.service.DireccionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/direcciones")
@CrossOrigin(origins = "*")
public class DireccionController {

    private final DireccionService direccionService;

    public DireccionController(DireccionService direccionService) {
        this.direccionService = direccionService;
    }

    // Listar todas las direcciones
    @GetMapping
    public ResponseEntity<List<DireccionResponseDTO>> listarDirecciones() {
        return ResponseEntity.ok(direccionService.listarDirecciones());
    }

    // Buscar dirección por su ID (Ajustado a buscarPorId de tu Service)
    @GetMapping("/{id}")
    public ResponseEntity<DireccionResponseDTO> obtenerDireccionPorId(@PathVariable Long id) {
        return direccionService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<DireccionResponseDTO> crearDireccion(@RequestBody DireccionRequestDTO datos) {
        DireccionResponseDTO creada = direccionService.crearDireccion(datos);
        return ResponseEntity.status(HttpStatus.CREATED).body(creada);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DireccionResponseDTO> actualizarDireccion(
            @PathVariable Long id,
            @RequestBody DireccionRequestDTO datos) {
        return direccionService.actualizarDireccion(id, datos)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarDireccion(@PathVariable Long id) {
        if (direccionService.eliminarDireccion(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}