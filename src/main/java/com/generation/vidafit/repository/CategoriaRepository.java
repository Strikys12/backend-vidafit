package com.generation.vidafit.repository;


import com.generation.vidafit.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    Categoria existsByCategoriaId(String nombre);
}
