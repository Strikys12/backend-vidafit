package com.generation.vidafit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.generation.vidafit.model.DetallePedido;

import java.util.Collection;

@Repository
public interface DetallePedidoRepository extends JpaRepository<DetallePedido, Long> {


    boolean existsByPedidoId(Long id);
}
