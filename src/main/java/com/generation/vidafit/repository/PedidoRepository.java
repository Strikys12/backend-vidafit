package com.generation.vidafit.repository;


import org.springframework.stereotype.Repository;
import com.generation.vidafit.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;


@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    boolean existsByDireccionId(Long id);
}
