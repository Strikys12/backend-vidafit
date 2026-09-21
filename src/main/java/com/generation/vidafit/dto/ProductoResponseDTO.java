package com.generation.vidafit.dto;

import com.generation.vidafit.model.Marca;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductoResponseDTO {

    private Long id;
    private String imagen;
    private String nombre;
    private BigDecimal precio;
    private String descripcion;
    private Integer stock;
    private Long categoriaId;
    private Marca marca;

}