package com.generation.vidafit.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "direcciones")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Direccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "direccion_exacta")
    private String direccionExacta;

    private String barrio;
    private String comuna;
    private String ciudad;
    private String departamento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;


}