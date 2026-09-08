package com.generation.vidafit.service;

import com.generation.vidafit.dto.ProductoRequestDTO;
import com.generation.vidafit.dto.ProductoResponseDTO;
import com.generation.vidafit.model.Categoria;
import com.generation.vidafit.model.Producto;
import com.generation.vidafit.repository.CategoriaRepository;
import com.generation.vidafit.repository.DetallePedidoRepository;
import com.generation.vidafit.repository.ProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final DetallePedidoRepository detallePedidoRepository;

    public ProductoService(ProductoRepository productoRepository,
                           CategoriaRepository categoriaRepository,
                           DetallePedidoRepository detallePedidoRepository) {
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
        this.detallePedidoRepository = detallePedidoRepository;
    }

    @Transactional(readOnly = true)
    public List<ProductoResponseDTO> listarProductos() {
        return productoRepository.findAll()
                .stream()
                .map(this::mapearAProductoResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public Optional<ProductoResponseDTO> obtenerProductoPorId(Long id) {
        return productoRepository.findById(id)
                .map(this::mapearAProductoResponseDTO);
    }

    @Transactional(readOnly = true)
    public List<ProductoResponseDTO> listarProductosPorCategoria(Long categoriaId) {
        if (!categoriaRepository.existsById(categoriaId)) {
            throw new IllegalArgumentException("Categoría no existe");
        }
        return productoRepository.findByCategoriaId(categoriaId)
                .stream()
                .map(this::mapearAProductoResponseDTO)
                .toList();
    }

    @Transactional
    public ProductoResponseDTO crearProducto(ProductoRequestDTO datos) {

        if (datos.getNombre() == null || datos.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre del producto es obligatorio");
        }

        if (datos.getPrecio() == null || datos.getPrecio().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Precio inválido");
        }

        if (datos.getStock() == null || datos.getStock() < 0) {
            throw new IllegalArgumentException("Stock inválido");
        }

        if (datos.getCategoriaId() == null) {
            throw new IllegalArgumentException("La categoría es obligatoria");
        }

        Categoria categoria = categoriaRepository.findById(datos.getCategoriaId())
                .orElseThrow(() -> new IllegalArgumentException("Categoría no existe"));

        Producto p = new Producto();
        p.setNombre(datos.getNombre());
        p.setPrecio(datos.getPrecio());
        p.setStock(datos.getStock());
        p.setCategoria(categoria);

        Producto creado = productoRepository.save(p);
        return mapearAProductoResponseDTO(creado);
    }

    @Transactional
    public Optional<ProductoResponseDTO> actualizarProducto(Long id, ProductoRequestDTO datos) {

        return productoRepository.findById(id)
                .map(producto -> {

                    if (datos.getNombre() != null && !datos.getNombre().isBlank()) {
                        producto.setNombre(datos.getNombre());
                    }

                    if (datos.getPrecio() != null) {
                        if (datos.getPrecio().compareTo(BigDecimal.ZERO) < 0) {
                            throw new IllegalArgumentException("Precio inválido");
                        }
                        producto.setPrecio(datos.getPrecio());
                    }

                    if (datos.getStock() != null) {
                        if (datos.getStock() < 0) {
                            throw new IllegalArgumentException("Stock inválido");
                        }
                        producto.setStock(datos.getStock());
                    }

                    if (datos.getCategoriaId() != null) {
                        Categoria categoria = categoriaRepository.findById(datos.getCategoriaId())
                                .orElseThrow(() -> new IllegalArgumentException("Categoría no existe"));
                        producto.setCategoria(categoria);
                    }

                    Producto actualizado = productoRepository.save(producto);
                    return mapearAProductoResponseDTO(actualizado);
                });
    }

    @Transactional
    public boolean eliminarProducto(Long id) {

        if (!productoRepository.existsById(id)) {
            return false;
        }

        // Evitar borrar si hay detalles de pedidos que lo referencian
        if (detallePedidoRepository.existsByProductoId(id)) {
            throw new IllegalStateException("No se puede eliminar el producto porque está asociado a pedidos existentes");
        }

        productoRepository.deleteById(id);
        return true;
    }

    @Transactional
    public void disminuirStock(Long productoId, int cantidad) {

        if (cantidad <= 0) {
            throw new IllegalArgumentException("Cantidad inválida");
        }

        Producto p = productoRepository.findById(productoId)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));

        if (p.getStock() < cantidad) {
            throw new IllegalStateException("Stock insuficiente");
        }

        p.setStock(p.getStock() - cantidad);
        productoRepository.save(p);
    }

    @Transactional
    public void aumentarStock(Long productoId, int cantidad) {

        if (cantidad <= 0) {
            throw new IllegalArgumentException("Cantidad inválida");
        }

        Producto p = productoRepository.findById(productoId)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));

        p.setStock(p.getStock() + cantidad);
        productoRepository.save(p);
    }

    private ProductoResponseDTO mapearAProductoResponseDTO(Producto p) {

        Long categoriaId = (p.getCategoria() != null) ? p.getCategoria().getId() : null;

        return new ProductoResponseDTO(
                p.getId(),
                p.getNombre(),
                p.getPrecio(),
                p.getStock(),
                categoriaId
        );
    }
}