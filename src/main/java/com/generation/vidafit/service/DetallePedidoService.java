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
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero");
        }

        // 1. PRIMERO buscas y declaras la variable 'pedido'
        Pedido pedido = pedidoRepository.findById(datos.getPedidoId())
                .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado"));

        // 2. PRIMERO buscas y declaras la variable 'producto'
        Producto producto = productoRepository.findById(datos.getProductoId())
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));

        if (producto.getStock() < datos.getCantidad()) {
            throw new IllegalStateException("Stock insuficiente");
        }

        // 3. AHORA SÍ instancias y usas las variables declaradas arriba
        DetallePedido detalle = new DetallePedido();
        detalle.setPedido(pedido);
        detalle.setProducto(producto);
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

                    Producto producto = productoRepository.findById(datos.getProductoId())
                            .orElseThrow(() ->
                                    new IllegalArgumentException("Producto no encontrado"));

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

        Optional<DetallePedido> detalleOptional = detallePedidoRepository.findById(id);

        if (detalleOptional.isEmpty()) {
            return false;
        }

        DetallePedido detalle = detalleOptional.get();


        Producto producto = detalle.getProducto();

        if (producto != null) {

            producto.setStock(producto.getStock() + detalle.getCantidad());


            productoRepository.save(producto);
        }

        detallePedidoRepository.deleteById(id);
        return true;
    }

    private DetallePedidoResponseDTO mapearADetallePedidoResponseDTO(DetallePedido detalle) {

        Long pedidoId = (detalle.getPedido() != null) ? detalle.getPedido().getId() : null;
        Long productoId = (detalle.getProducto() != null) ? detalle.getProducto().getId() : null;
        String nombreProducto = (detalle.getProducto() != null) ? detalle.getProducto().getNombre() : "Producto sin nombre";

        return new DetallePedidoResponseDTO(
                detalle.getId(),
                pedidoId,
                productoId,
                nombreProducto,
                detalle.getCantidad(),
                detalle.getPrecioUnitario()
        );
    }
}