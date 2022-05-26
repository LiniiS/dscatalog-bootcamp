package com.asantos.dscatalog.services;

import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.asantos.dscatalog.dto.ProductDTO;
import com.asantos.dscatalog.entities.Product;
import com.asantos.dscatalog.repositories.ProductRepository;
import com.asantos.dscatalog.services.exceptions.DataBaseException;
import com.asantos.dscatalog.services.exceptions.ResourceNotFoundException;

@Service
public class ProductService {

	@Autowired
	private ProductRepository productRepository;

	@Transactional(readOnly = true)
	public Page<ProductDTO> findAllPaged(PageRequest pageRequest) {
		Page<Product> productList = productRepository.findAll(pageRequest);
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
	//	newProduct.setName(productDto.getName());
		newProduct = productRepository.save(newProduct);

		return new ProductDTO(newProduct);
	}

	@Transactional
	public ProductDTO update(Long id, ProductDTO productDto) {
		try {
			@SuppressWarnings("deprecation")
			Product productUpdated = productRepository.getOne(id);
	//		productUpdated.setName(productDto.getName());
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
		}catch (DataIntegrityViolationException e) {
			throw new DataBaseException("Attention! Data Base Integrity Violation!");
		}
		
		
	}

}
