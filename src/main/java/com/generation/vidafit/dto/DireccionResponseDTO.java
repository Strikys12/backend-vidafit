package com.generation.vidafit.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DireccionResponseDTO {

    private Long id;
    private Long userId;
    private String direccionExacta;
    private String barrio;
    private String comuna;
    private String ciudad;
    private String departamento;
}