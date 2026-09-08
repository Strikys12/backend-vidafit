package com.generation.vidafit.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DetallePedidoRequestDTO {

    private Long pedidoId;
    private Long productoId;
    private Integer cantidad;
}