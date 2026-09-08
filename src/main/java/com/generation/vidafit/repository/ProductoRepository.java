package com.generation.vidafit.repository;


import org.springframework.stereotype.Repository;
import com.generation.vidafit.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;


@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    boolean existsByCategoriaId(Long id);
}
