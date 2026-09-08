package com.generation.vidafit.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PedidoResponseDTO {

    private Long id;
    private Long usuarioId;
    private Long direccionId;
    private LocalDateTime fechaPedido;
    private String estado;
    private BigDecimal total;
    private List<DetallePedidoResponseDTO> detalles;
}