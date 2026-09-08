package com.generation.vidafit.service;


import com.generation.vidafit.dto.ProductoRequestDTO;
import com.generation.vidafit.dto.ProductoResponseDTO;
import com.generation.vidafit.model.Producto;
import com.generation.vidafit.repository.ProductoRepository;
import com.generation.vidafit.repository.CategoriaRepository;
import com.generation.vidafit.repository.DetallePedidoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProductoService {

    private final  ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final DetallePedidoRepository detallePedidoRepository;

    public ProductoService(ProductoRepository productoRepository, CategoriaRepository categoriaRepository, DetallePedidoRepository detallePedidoRepository) {
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
        this.detallePedidoRepository = detallePedidoRepository;
    }

    @Transactional (readOnly = true)
    public List<ProductoResponseDTO> listarProductos() {
        return productoRepository.findAll()
                .stream()
                .map(this::mapearAProductoResponseDTO)
                .toList();
    }

    @Transactional (readOnly = true)
    public Optional<ProductoResponseDTO> obtenerProductoPorId(Long id) {
        return productoRepository.findById(id)
                .map(this::mapearAProductoResponseDTO);
    }

    @Transactional
    public ProductoResponseDTO crearProducto(ProductoRequestDTO datos) {

        if (datos.getPrecio() == null || datos.getPrecio().doubleValue() < 0) {
            throw new IllegalArgumentException("Precio inválido");
        }
        if (datos.getStock() == null || datos.getStock() < 0) {
            throw new IllegalArgumentException("Stock inválido");
        }
        if (!categoriaRepository.existsById(datos.getCategoriaId())) {
            throw new IllegalArgumentException("Categoría no existe");
        }

        Producto p = new Producto();
        p.setNombre(datos.getNombre());
        p.setPrecio(datos.getPrecio());
        p.setStock(datos.getStock());
        p.setCategoriaId(datos.getCategoriaId());


        Producto creado = productoRepository.save(p);
        return mapearAProductoResponseDTO(creado);
    }

    @Transactional
    public Optional<ProductoResponseDTO> actualizarProducto(Long id, ProductoRequestDTO datos) {
        return productoRepository.findById(id)
                .map(producto -> {
                    producto.setNombre(datos.getNombre());
                    if (datos.getPrecio() != null && datos.getPrecio().doubleValue() >= 0) {
                        producto.setPrecio(datos.getPrecio());
                    }
                    if (datos.getStock() != null && datos.getStock() >= 0) {
                        producto.setStock(datos.getStock());
                    }
                    if (datos.getCategoriaId() != null && categoriaRepository.existsById(datos.getCategoriaId())) {
                        producto.setCategoriaId(datos.getCategoriaId());
                    }
                    Producto actualizado = productoRepository.save(producto);
                    return mapearAProductoResponseDTO(actualizado);
                });
    }

    public boolean eliminarProducto(Long id) {
        if (!productoRepository.existsById(id)) {
            return false;
        }
        // Evitar borrar si hay detalles de pedidos que lo referencian
        if (detallePedidoRepository.existsByProductoId(id)) {
            return false;
        }
        productoRepository.deleteById(id);
        return true;
    }

    @Transactional
    public void disminuirStock(Long productoId, int cantidad) {
        Producto p = productoRepository.findById(productoId)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));
        if (cantidad <= 0) throw new IllegalArgumentException("Cantidad inválida");
        if (p.getStock() < cantidad) throw new IllegalStateException("Stock insuficiente");
        p.setStock(p.getStock() - cantidad);
        productoRepository.save(p);
    }

    @Transactional
    public void aumentarStock(Long productoId, int cantidad) {
        Producto p = productoRepository.findById(productoId)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));
        if (cantidad < 0) throw new IllegalArgumentException("Cantidad inválida");
        p.setStock(p.getStock() + cantidad);
        productoRepository.save(p);
    }


    private ProductoResponseDTO mapearAProductoResponseDTO(Producto p) {
        return new ProductoResponseDTO(
                p.getId(),
                p.getNombre(),
                p.getPrecio(),
                p.getStock(),
                p.getCategoriaId()
        );
    }
}
