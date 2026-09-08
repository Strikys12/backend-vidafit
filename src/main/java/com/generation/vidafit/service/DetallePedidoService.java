package com.generation.vidafit.service;

import com.generation.vidafit.dto.DetallePedidoRequestDTO;
import com.generation.vidafit.dto.DetallePedidoResponseDTO;
import com.generation.vidafit.model.DetallePedido;
import com.generation.vidafit.model.Pedido;
import com.generation.vidafit.model.Producto;
import com.generation.vidafit.repository.DetallePedidoRepository;
import com.generation.vidafit.repository.PedidoRepository;
import com.generation.vidafit.repository.ProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class DetallePedidoService {

    private final DetallePedidoRepository detallePedidoRepository;
    private final PedidoRepository pedidoRepository;
    private final ProductoRepository productoRepository;

    public DetallePedidoService(DetallePedidoRepository detallePedidoRepository,
                                PedidoRepository pedidoRepository,
                                ProductoRepository productoRepository) {
        this.detallePedidoRepository = detallePedidoRepository;
        this.pedidoRepository = pedidoRepository;
        this.productoRepository = productoRepository;
    }

    @Transactional(readOnly = true)
    public List<DetallePedidoResponseDTO> listarDetalles() {
        return detallePedidoRepository.findAll()
                .stream()
                .map(this::mapearADetallePedidoResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public Optional<DetallePedidoResponseDTO> buscarPorId(Long id) {
        return detallePedidoRepository.findById(id)
                .map(this::mapearADetallePedidoResponseDTO);
    }

    @Transactional(readOnly = true)
    public List<DetallePedidoResponseDTO> listarPorPedido(Long pedidoId) {

        if (!pedidoRepository.existsById(pedidoId)) {
            throw new IllegalArgumentException("Pedido no existe");
        }

        return detallePedidoRepository.findByPedidoId(pedidoId)
                .stream()
                .map(this::mapearADetallePedidoResponseDTO)
                .toList();
    }

    @Transactional
    public DetallePedidoResponseDTO crearDetalle(DetallePedidoRequestDTO datos) {

        if (datos.getCantidad() <= 0) {
            throw new IllegalArgumentException(
                    "La cantidad debe ser mayor que cero"
            );
        }

        if (!pedidoRepository.existsById(datos.getPedidoId())) {
            throw new IllegalArgumentException("Pedido no existe");
        }

        Producto producto = productoRepository.findById(datos.getProductoId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Producto no existe"));

        if (producto.getStock() < datos.getCantidad()) {
            throw new IllegalStateException("Stock insuficiente");
        }

        DetallePedido detalle = new DetallePedido();

        detalle.setPedidoId(datos.getPedidoId());
        detalle.setProductoId(datos.getProductoId());
        detalle.setCantidad(datos.getCantidad());
        detalle.setPrecioUnitario(producto.getPrecio());

        DetallePedido creado = detallePedidoRepository.save(detalle);

        producto.setStock(producto.getStock() - datos.getCantidad());
        productoRepository.save(producto);

        return mapearADetallePedidoResponseDTO(creado);
    }

    @Transactional
    public Optional<DetallePedidoResponseDTO> actualizarDetalle(
            Long id,
            DetallePedidoRequestDTO datos) {

        return detallePedidoRepository.findById(id)
                .map(detalle -> {

                    if (datos.getCantidad() <= 0) {
                        throw new IllegalArgumentException(
                                "La cantidad debe ser mayor que cero"
                        );
                    }

                    Producto producto = productoRepository.findById(
                                    detalle.getProductoId()
                            )
                            .orElseThrow(() ->
                                    new IllegalArgumentException(
                                            "Producto no existe"
                                    ));

                    int diferencia = datos.getCantidad()
                            - detalle.getCantidad();

                    if (diferencia > 0 &&
                            producto.getStock() < diferencia) {
                        throw new IllegalStateException(
                                "Stock insuficiente"
                        );
                    }

                    if (diferencia > 0) {
                        producto.setStock(
                                producto.getStock() - diferencia
                        );
                    } else if (diferencia < 0) {
                        producto.setStock(
                                producto.getStock() + Math.abs(diferencia)
                        );
                    }

                    detalle.setCantidad(datos.getCantidad());

                    detallePedidoRepository.save(detalle);
                    productoRepository.save(producto);

                    return mapearADetallePedidoResponseDTO(detalle);
                });
    }

    @Transactional
    public boolean eliminarDetalle(Long id) {

        Optional<DetallePedido> detalleOptional =
                detallePedidoRepository.findById(id);

        if (detalleOptional.isEmpty()) {
            return false;
        }

        DetallePedido detalle = detalleOptional.get();

        Producto producto = productoRepository.findById(
                detalle.getProductoId()
        ).orElse(null);

        if (producto != null) {
            producto.setStock(
                    producto.getStock() + detalle.getCantidad()
            );

            productoRepository.save(producto);
        }

        detallePedidoRepository.deleteById(id);
        return true;
    }

    private DetallePedidoResponseDTO mapearADetallePedidoResponseDTO(
            DetallePedido detalle) {

        return new DetallePedidoResponseDTO(
                detalle.getId(),
                detalle.getPedidoId(),
                detalle.getProductoId(),
                detalle.getCantidad(),
                detalle.getPrecioUnitario()
        );
    }
}