package com.generation.vidafit.service;

import com.generation.vidafit.dto.DetallePedidoRequestDTO;
import com.generation.vidafit.dto.DetallePedidoResponseDTO;
import com.generation.vidafit.dto.PedidoRequestDTO;
import com.generation.vidafit.dto.PedidoResponseDTO;
import com.generation.vidafit.model.*;
import com.generation.vidafit.model.EstadoPedido;
import com.generation.vidafit.repository.DetallePedidoRepository;
import com.generation.vidafit.repository.DireccionRepository;
import com.generation.vidafit.repository.PedidoRepository;
import com.generation.vidafit.repository.ProductoRepository;
import com.generation.vidafit.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final UsuarioRepository usuarioRepository;
    private final DireccionRepository direccionRepository;
    private final ProductoRepository productoRepository;
    private final DetallePedidoRepository detallePedidoRepository;

    public PedidoService(PedidoRepository pedidoRepository,
                         UsuarioRepository usuarioRepository,
                         DireccionRepository direccionRepository,
                         ProductoRepository productoRepository,
                         DetallePedidoRepository detallePedidoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.usuarioRepository = usuarioRepository;
        this.direccionRepository = direccionRepository;
        this.productoRepository = productoRepository;
        this.detallePedidoRepository = detallePedidoRepository;
    }

    @Transactional(readOnly = true)
    public List<PedidoResponseDTO> listarPedidos() {
        return pedidoRepository.findAll()
                .stream()
                .map(this::mapearAPedidoResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public Optional<PedidoResponseDTO> buscarPorId(Long id) {
        return pedidoRepository.findById(id)
                .map(this::mapearAPedidoResponseDTO);
    }

    @Transactional(readOnly = true)
    public List<PedidoResponseDTO> listarPedidosPorUsuario(Long usuarioId) {
        if (!usuarioRepository.existsById(usuarioId)) {
            throw new IllegalArgumentException("Usuario no existe");
        }
        return pedidoRepository.findByUsuarioId(usuarioId)
                .stream()
                .map(this::mapearAPedidoResponseDTO)
                .toList();
    }

    @Transactional
    public PedidoResponseDTO crearPedido(PedidoRequestDTO datos) {

        if (datos.getUsuarioId() == null) {
            throw new IllegalArgumentException("Usuario no existe");
        }

        Usuario usuario = usuarioRepository.findById(datos.getUsuarioId())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no existe"));

        Direccion direccion = direccionRepository.findById(datos.getDireccionId())
                .orElseThrow(() -> new IllegalArgumentException("Dirección no existe"));

        if (direccion.getUsuario() == null || !direccion.getUsuario().getId().equals(datos.getUsuarioId())) {
            throw new IllegalArgumentException("La dirección no pertenece al usuario");
        }

        if (datos.getDetalles() == null || datos.getDetalles().isEmpty()) {
            throw new IllegalArgumentException("El pedido debe tener al menos un producto");
        }

        Pedido pedido = new Pedido();
        pedido.setUsuario(usuario);
        pedido.setDireccion(direccion);
        pedido.setFechaPedido(LocalDateTime.now());
        pedido.setEstado(EstadoPedido.valueOf("CREADO"));
        pedido.setTotal(BigDecimal.ZERO);

        Pedido pedidoGuardado = pedidoRepository.save(pedido);

        BigDecimal total = BigDecimal.ZERO;

        for (DetallePedidoRequestDTO detReq : datos.getDetalles()) {

            if (detReq.getCantidad() <= 0) {
                throw new IllegalArgumentException("La cantidad debe ser mayor que cero");
            }

            Producto producto = productoRepository.findById(detReq.getProductoId())
                    .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado: " + detReq.getProductoId()));

            if (producto.getStock() < detReq.getCantidad()) {
                throw new IllegalStateException("Stock insuficiente para producto: " + detReq.getProductoId());
            }

            DetallePedido detalle = new DetallePedido();
            detalle.setPedido(pedidoGuardado);
            detalle.setProducto(producto);
            detalle.setCantidad(detReq.getCantidad());
            detalle.setPrecioUnitario(producto.getPrecio());

            detallePedidoRepository.save(detalle);

            producto.setStock(producto.getStock() - detReq.getCantidad());
            productoRepository.save(producto);

            BigDecimal subtotal = producto.getPrecio().multiply(BigDecimal.valueOf(detReq.getCantidad()));
            total = total.add(subtotal);
        }

        pedidoGuardado.setTotal(total);
        pedidoRepository.save(pedidoGuardado);

        return mapearAPedidoResponseDTO(pedidoGuardado);
    }

    @Transactional
    public Optional<PedidoResponseDTO> actualizarPedido(Long id, PedidoRequestDTO datos) {

        return pedidoRepository.findById(id)
                .map(pedido -> {

                    if (datos.getDireccionId() != null) {

                        Direccion direccion = direccionRepository.findById(datos.getDireccionId())
                                .orElseThrow(() -> new IllegalArgumentException("Dirección no existe"));

                        if (pedido.getUsuario() == null || direccion.getUsuario() == null ||
                                !direccion.getUsuario().getId().equals(pedido.getUsuario().getId())) {

                            throw new IllegalArgumentException("La nueva dirección no pertenece al usuario");
                        }

                        pedido.setDireccion(direccion);
                    }

                    if (datos.getEstado() != null && !datos.getEstado().isBlank()) {

                        String estado = datos.getEstado().toUpperCase();

                        if (!estado.equals("CREADO") &&
                                !estado.equals("CONFIRMADO") &&
                                !estado.equals("ENVIADO") &&
                                !estado.equals("ENTREGADO") &&
                                !estado.equals("CANCELADO")) {

                            throw new IllegalArgumentException("Estado de pedido inválido");
                        }

                        pedido.setEstado(EstadoPedido.valueOf(estado));
                    }

                    Pedido actualizado = pedidoRepository.save(pedido);

                    return mapearAPedidoResponseDTO(actualizado);
                });
    }

    @Transactional
    public boolean eliminarPedido(Long id) {

        if (!pedidoRepository.existsById(id)) {
            return false;
        }

        if (detallePedidoRepository.existsByPedidoId(id)) {
            throw new IllegalStateException("No se puede eliminar el pedido porque tiene detalles asociados");
        }

        pedidoRepository.deleteById(id);
        return true;
    }

    private PedidoResponseDTO mapearAPedidoResponseDTO(Pedido p) {

        List<DetallePedidoResponseDTO> detalles = detallePedidoRepository.findByPedidoId(p.getId())
                .stream()
                .map(d -> {
                    Long pedidoId = (d.getPedido() != null) ? d.getPedido().getId() : null;
                    Long productoId = (d.getProducto() != null) ? d.getProducto().getId() : null;

                    return new DetallePedidoResponseDTO(
                            d.getId(),
                            pedidoId,
                            productoId,
                            d.getCantidad(),
                            d.getPrecioUnitario()
                    );
                })
                .toList();

        Long usuarioId = (p.getUsuario() != null) ? p.getUsuario().getId() : null;
        Long direccionId = (p.getDireccion() != null) ? p.getDireccion().getId() : null;
        String estadoStr = (p.getEstado() != null) ? p.getEstado().name() : null;

        return new PedidoResponseDTO(
                p.getId(),
                usuarioId,
                direccionId,
                p.getFechaPedido(),
                estadoStr,
                p.getTotal(),
                detalles
        );
    }
}