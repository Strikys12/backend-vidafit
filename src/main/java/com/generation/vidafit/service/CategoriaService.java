package com.generation.vidafit.service;


import com.generation.vidafit.dto.CategoriaRequestDTO;
import com.generation.vidafit.dto.CategoriaResponseDTO;
import com.generation.vidafit.model.Categoria;
import com.generation.vidafit.repository.CategoriaRepository;
import com.generation.vidafit.repository.ProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;



@Service
public class CategoriaService {
    private final CategoriaRepository categoriaRepository;
    private final ProductoRepository productoRepository;

    public CategoriaService(CategoriaRepository categoriaRepository, ProductoRepository productoRepository) {
        this.categoriaRepository = categoriaRepository;
        this.productoRepository = productoRepository;
    }

    @Transactional(readOnly = true)
    public List<CategoriaResponseDTO> listarCategorias(){
        return categoriaRepository.findAll()
                .stream()
                .map(this::mapearACategoriaResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public Optional<CategoriaResponseDTO> obtenerCategoriaPorId(Long id){
        return categoriaRepository.findById(id)
                .map(this::mapearACategoriaResponseDTO);
    }

    @Transactional
    public CategoriaResponseDTO crearCategoria(CategoriaRequestDTO datos) {
        Categoria categoria = new Categoria();
        categoria.setNombre(datos.getNombre());
        categoria.setDescripcion(datos.getDescripcion());
        Categoria categoriaGuardada = categoriaRepository.save(categoria);
        return mapearACategoriaResponseDTO(categoriaGuardada);
    }

    @Transactional
    public Optional<CategoriaResponseDTO> actualizarCategoria(Long id, CategoriaRequestDTO datos) {
        return categoriaRepository.findById(id)
                .map(categoria -> {
                    categoria.setNombre(datos.getNombre());
                    categoria.setDescripcion(datos.getDescripcion());
                    Categoria categoriaActualizada = categoriaRepository.save(categoria);
                    return mapearACategoriaResponseDTO(categoriaActualizada);
                });
    }

    public boolean eliminarCategoria(Long id){
        if(!categoriaRepository.existsById(id)){
            return false;
        }

        if(productoRepository.existsByCategoria_CategoriaId(id)){
            throw new IllegalStateException("No se puede eliminar la categoría porque hay productos asociados a ella.");
        }

        categoriaRepository.deleteById(id);
        return true;
    }



    private CategoriaResponseDTO mapearACategoriaResponseDTO(Categoria c) {
        return new CategoriaResponseDTO(
                c.getCategoriaId(),
                c.getNombre(),
                c.getDescripcion()
        );
    }
}