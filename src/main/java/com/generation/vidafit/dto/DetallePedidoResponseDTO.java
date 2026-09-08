package com.generation.vidafit.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DetallePedidoResponseDTO {

    private Long id;
    private Long pedidoId;
    private Long productoId;
    private Integer cantidad;
    private BigDecimal precioUnitario;
}