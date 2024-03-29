package com.asantos.dscatalog.services;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.asantos.dscatalog.dto.CategoryDTO;
import com.asantos.dscatalog.dto.ProductDTO;
import com.asantos.dscatalog.entities.Category;
import com.asantos.dscatalog.entities.Product;
import com.asantos.dscatalog.repositories.CategoryRepository;
import com.asantos.dscatalog.repositories.ProductRepository;
import com.asantos.dscatalog.services.exceptions.DataBaseException;
import com.asantos.dscatalog.services.exceptions.ResourceNotFoundException;

@Service
public class ProductService {

	@Autowired
	private ProductRepository productRepository;
	
	@Autowired
	private CategoryRepository categoryRepository;


	@Transactional(readOnly = true)
	public Page<ProductDTO> findAllPaged(Pageable pageable) {
		Page<Product> productList = productRepository.findAll(pageable);
		return productList.map(item -> new ProductDTO(item));

	}
	
	
	@Transactional(readOnly = true)
	public Page<ProductDTO> findAllOrFiltered(Long categoryId, String name, PageRequest pageRequest) {
		System.out.println("calling findAllOrFiltered()");
		
		//boa prática instanciar o objeto, verificar se ele é nulo pois o campo é opcional na busca
		//Category category = (categoryId == 0) ? null : categoryRepository.getReferenceById(categoryId);
		@SuppressWarnings("deprecation")
		List<Category> categories = (categoryId == 0) ? null : Arrays.asList(categoryRepository.getOne(categoryId));
		
		Page<Product> productList = productRepository.findAllOrFiltered(categories, name, pageRequest);
				
		return productList.map(item -> new ProductDTO(item));

	}
		

	@Transactional(readOnly = true)
	public ProductDTO findById(Long id) {
		Optional<Product> optionalProduct = productRepository.findById(id);
		Product product = optionalProduct.orElseThrow(() -> new ResourceNotFoundException("Resource not found!!!"));
		return new ProductDTO(product, product.getCategories());
	}

	@Transactional
	public ProductDTO insert(ProductDTO productDto) {

		Product newProduct = new Product();
		copyDtoToEntity(productDto, newProduct);
		newProduct = productRepository.save(newProduct);

		return new ProductDTO(newProduct);
	}

	@Transactional
	public ProductDTO update(Long id, ProductDTO productDto) {
		try {
			@SuppressWarnings("deprecation")
			Product productUpdated = productRepository.getOne(id);
			copyDtoToEntity(productDto, productUpdated);
			productUpdated = productRepository.save(productUpdated);
			return new ProductDTO(productUpdated);
		} catch (EntityNotFoundException e) {
			throw new ResourceNotFoundException("Id not found: " + id);
		}

	}

	public void delete(Long id) {

		try {
			productRepository.deleteById(id);
		} catch (EmptyResultDataAccessException e) {
			throw new ResourceNotFoundException("Id not found: " + id);
		} catch (DataIntegrityViolationException e) {
			throw new DataBaseException("Attention! Data Base Integrity Violation!");
		}

	}
	
	private void copyDtoToEntity(ProductDTO productDto, Product product) {
		product.setName(productDto.getName());
		product.setDescription(productDto.getDescription());
		product.setPrice(productDto.getPrice());
		product.setImgUrl(productDto.getImgUrl());
		product.setDate(productDto.getDate());
		
		product.getCategories().clear();
		for(CategoryDTO categoryDto : productDto.getCategories()) {
			@SuppressWarnings("deprecation")
			Category category = categoryRepository.getOne(categoryDto.getId());
			product.getCategories().add(category);
		}
	}

}
