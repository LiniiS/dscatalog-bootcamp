package com.asantos.dscatalog.services;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import com.asantos.dscatalog.dto.ProductDTO;
import com.asantos.dscatalog.repositories.ProductRepository;
import com.asantos.dscatalog.services.exceptions.ResourceNotFoundException;

@SpringBootTest
@Transactional
public class ProductServiceIT {

	@Autowired
	private ProductService productService;

	@Autowired
	private ProductRepository productRepository;

	private Long existingId;
	private Long nonExistingId;
	private Long countTotalProducts;

	@BeforeEach
	void setup() throws Exception {
		existingId = 1L;
		nonExistingId = 250L;
		countTotalProducts = 25L;
	}

	@Test
	public void deleteShouldDeleteResourceWhenIdExists() {
		productService.delete(existingId);

		// verificar se o total de produtos é o total atual - 1
		Assertions.assertEquals(countTotalProducts - 1, productRepository.count());
	}

	@Test
	public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {

		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			productService.delete(nonExistingId);
		});
	}

	@Test
	public void findAllPagedShouldReturnPageWhenPageZeroSizeTen() {
		// construir uma pagina simples de número 0 e tamanho 10
		PageRequest pageRequest = PageRequest.of(0, 10);

		// action
		Page<ProductDTO> pageResult = productService.findAllPaged(pageRequest);

		// assert
		// a page não está vazia, sabemos que possui 25 items, mas o tamanho da page é
		// 10
		Assertions.assertFalse(pageResult.isEmpty());
		// a página deve ser a primeira (0)
		Assertions.assertEquals(0, pageResult.getNumber());
		// o tamanho de items na pagina é igual a 10
		Assertions.assertEquals(10, pageResult.getSize());
		// verificar se todos os elementos do pageResult é igual ao total dos elementos
		// no bd
		Assertions.assertEquals(countTotalProducts, pageResult.getTotalElements());

	}

	@Test
	public void findAllPagedShouldReturnEmptyPageWhenPageDoesNotExist() {
		// construir uma pagina simples de número 0 e tamanho 10
		PageRequest pageRequest = PageRequest.of(50, 10);

		// action
		Page<ProductDTO> pageResult = productService.findAllPaged(pageRequest);

		Assertions.assertTrue(pageResult.isEmpty());

	}

	
	@Test
	public void findAllPagedShouldReturnSortedPageWhenSortedByName() {
		// construir uma pagina simples de número 0 e tamanho 10
		PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("name"));

		// action
		Page<ProductDTO> pageResult = productService.findAllPaged(pageRequest);

		Assertions.assertFalse(pageResult.isEmpty());
		
		//testar se está ordenado? observar os nomes do bd
		Assertions.assertEquals("Macbook Pro", pageResult.getContent().get(0).getName());
		Assertions.assertEquals("PC Gamer", pageResult.getContent().get(1).getName());
		Assertions.assertEquals("PC Gamer Alfa", pageResult.getContent().get(2).getName());

	}
}
