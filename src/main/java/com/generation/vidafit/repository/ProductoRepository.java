package com.generation.vidafit.repository;


import com.generation.vidafit.model.Categoria;
import org.springframework.stereotype.Repository;
import com.generation.vidafit.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    boolean existsByCategoriaId(Long id);

    List<Producto> findByCategoriaId(Long categoriaId);
}
