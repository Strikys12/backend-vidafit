package com.generation.vidafit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.generation.vidafit.model.DetallePedido;

import java.util.Collection;
import java.util.List;

@Repository
public interface DetallePedidoRepository extends JpaRepository<DetallePedido, Long> {

    List<DetallePedido> findByPedidoId(Long pedidoId);
    boolean existsByPedidoId(Long id);

    boolean existsByProductoId(Long id);
}
