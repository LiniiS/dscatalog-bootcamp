package com.asantos.dscatalog.repositories;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.EmptyResultDataAccessException;

import com.asantos.dscatalog.entities.Product;
import com.asantos.dscatalog.tests.Factory;

@DataJpaTest
public class ProductRepositoryTests {

	@Autowired
	private ProductRepository productRepository;

	private long existingId;
	private long nonExistingId;
	private long countTotalProducts;

	@BeforeEach
	void setup() throws Exception {
		existingId = 1L;
		nonExistingId = 150L;
		countTotalProducts = 25L;
	}

	@Test
	public void saveShouldPersistWithAutoIncrementWhenIdIsNull() {
		// arrange
		Product product = Factory.createProduct();
		product.setId(null);
		// act
		product = productRepository.save(product);
		// assert
		Assertions.assertNotNull(product.getId());
		Assertions.assertEquals(countTotalProducts + 1, product.getId());
	}

	@Test
	public void finByIdShouldReturnNonEmptyOptionalWhenProductIdExists() {
		Optional<Product> result = productRepository.findById(existingId);

		Assertions.assertTrue(result.isPresent());
	}
	
	@Test
	public void findByIdShouldReturnEmptyOptionalWhenProductIDoesNotExist() {
		Optional<Product> result = productRepository.findById(nonExistingId);

		Assertions.assertTrue(result.isEmpty());
	}

	@Test
	public void deleteShoulDeleteObjectWhenIdExists() {

		productRepository.deleteById(existingId);
		Optional<Product> result = productRepository.findById(existingId);

		Assertions.assertFalse(result.isPresent());

	}

	@Test
	public void deleteShouldThrowEmptyResultDataAccessExceptionWhenIdDoesNotExist() {

		Assertions.assertThrows(EmptyResultDataAccessException.class, () -> {
			productRepository.deleteById(nonExistingId);
		});
	}

}
