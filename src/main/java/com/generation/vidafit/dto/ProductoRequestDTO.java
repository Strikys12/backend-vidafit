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
public class ProductoRequestDTO {

    private String imagen;
    private String nombre;
    private BigDecimal precio;
    private Integer stock;
    private Long categoriaId;
    private String descripcion;
    private Marca marca;
}