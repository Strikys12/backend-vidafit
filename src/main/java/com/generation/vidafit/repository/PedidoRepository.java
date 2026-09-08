package com.generation.vidafit.repository;


import com.generation.vidafit.model.Usuario;
import org.springframework.stereotype.Repository;
import com.generation.vidafit.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;


@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    boolean existsByDireccionId(Long id);
    List<Pedido> findByUsuarioId(Long usuarioId);


}
