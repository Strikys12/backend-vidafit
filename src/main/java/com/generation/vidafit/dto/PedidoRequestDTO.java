package com.generation.vidafit.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PedidoRequestDTO {

    private Long usuarioId;
    private Long direccionId;
    private List<DetallePedidoRequestDTO> detalles;
    private String estado;
}