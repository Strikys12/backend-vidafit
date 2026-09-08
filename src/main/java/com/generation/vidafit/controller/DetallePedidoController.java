package com.generation.vidafit.controller;

import com.generation.vidafit.dto.DetallePedidoRequestDTO;
import com.generation.vidafit.dto.DetallePedidoResponseDTO;
import com.generation.vidafit.service.DetallePedidoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/detalles-pedido")
@CrossOrigin(origins = "*")
public class DetallePedidoController {

    private final DetallePedidoService detallePedidoService;

    public DetallePedidoController(DetallePedidoService detallePedidoService) {
        this.detallePedidoService = detallePedidoService;
    }

    @GetMapping
    public ResponseEntity<List<DetallePedidoResponseDTO>> listarDetalles() {
        return ResponseEntity.ok(detallePedidoService.listarDetalles());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DetallePedidoResponseDTO> obtenerDetallePorId(@PathVariable Long id) {
        return detallePedidoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/pedido/{pedidoId}")
    public ResponseEntity<List<DetallePedidoResponseDTO>> listarPorPedido(@PathVariable Long pedidoId) {
        return ResponseEntity.ok(detallePedidoService.listarPorPedido(pedidoId));
    }

    @PostMapping
    public ResponseEntity<DetallePedidoResponseDTO> crearDetalle(@RequestBody DetallePedidoRequestDTO datos) {
        DetallePedidoResponseDTO creado = detallePedidoService.crearDetalle(datos);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DetallePedidoResponseDTO> actualizarDetalle(
            @PathVariable Long id,
            @RequestBody DetallePedidoRequestDTO datos) {
        return detallePedidoService.actualizarDetalle(id, datos)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarDetalle(@PathVariable Long id) {
        if (detallePedidoService.eliminarDetalle(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}