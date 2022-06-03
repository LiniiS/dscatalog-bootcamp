package com.asantos.dscatalog.services;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.asantos.dscatalog.dto.ProductDTO;
import com.asantos.dscatalog.entities.Category;
import com.asantos.dscatalog.entities.Product;
import com.asantos.dscatalog.repositories.CategoryRepository;
import com.asantos.dscatalog.repositories.ProductRepository;
import com.asantos.dscatalog.services.exceptions.DataBaseException;
import com.asantos.dscatalog.services.exceptions.ResourceNotFoundException;
import com.asantos.dscatalog.tests.Factory;

@ExtendWith(SpringExtension.class)
public class ProductServiceTests {

	@InjectMocks
	private ProductService productService;

	@Mock
	private ProductRepository productRepository;

	@Mock
	private CategoryRepository categoryRepository;

	private long existingId;
	private long nonExistingId;
	private long dependentId;
	private PageImpl<Product> page; // tipo concreto que cria uma pagina
	private Product product;
	private Category category;
	ProductDTO productDto;

	@BeforeEach
	void setup() throws Exception {
		existingId = 1L;
		nonExistingId = 1000L;
		dependentId = 4L;
		product = Factory.createProduct();
		category = Factory.createCategory();
		productDto = Factory.createProductDTO();
		page = new PageImpl<>(List.of(product));

		// qnd chamar o findAll, retorna um Pageable que retorna uma página de algo
		Mockito.when(productRepository.findAll((Pageable) ArgumentMatchers.any())).thenReturn(page);

		// quando chamar o save, retornar o produto salvo no BD
		Mockito.when(productRepository.save(ArgumentMatchers.any())).thenReturn(product);

		// quando chamar o findById passando um Id existente, retorna um optional não
		// vazio, algo está presente
		Mockito.when(productRepository.findById(existingId)).thenReturn(Optional.of(product));

		// quando chamar o findById passando um Id não existente, retorna um optional
		// vazio, nada presente
		Mockito.when(productRepository.findById(nonExistingId)).thenReturn(Optional.empty());

		// quando chamar o getOne do product com um id existente
		Mockito.when(productRepository.getOne(existingId)).thenReturn(product);

		// quando chamar o getOne do product com um id existente
		Mockito.when(productRepository.getOne(nonExistingId)).thenThrow(EntityNotFoundException.class);

		// quando chamar o getOne da category com id existente (desencadeado pelo getOne
		// do product id existente)
		Mockito.when(categoryRepository.getOne(existingId)).thenReturn(category);

		// quando chamar o getOne da category com id existente (desencadeado pelo getOne
		// do product id inexistente)
		Mockito.when(categoryRepository.getOne(nonExistingId)).thenThrow(EntityNotFoundException.class);

		// simulação do delete é void, não tem retorno
		// nesse caso coloca a ação e depois o when
		Mockito.doNothing().when(productRepository).deleteById(existingId);
		Mockito.doThrow(EmptyResultDataAccessException.class).when(productRepository).deleteById(nonExistingId);
		Mockito.doThrow(DataIntegrityViolationException.class).when(productRepository).deleteById(dependentId);
	}

	@Test
	public void updateShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {

		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			productService.update(nonExistingId, productDto);
		});

	}

	@Test
	public void updateShouldReturnProducDTOtWhenIdExists() {

		ProductDTO productDtoUpdated = productService.update(existingId, productDto);

		// quando o id existe, o service aciona o getOne do produto e aciona o getOne da
		// category
		Assertions.assertNotNull(productDtoUpdated);

	}

	@Test
	public void findByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {

		// o service aciona o repository que retorna um optional vazio e cai no
		// orElseThrow que lança a ResourceNotFoundException~
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			productService.findById(nonExistingId);
		});

	}

	@Test
	public void findByIdShouldReturnProductDTOWhenIdExists() {
		ProductDTO productDto = productService.findById(existingId);

		// o service aciona o repository q devolve um Optional mockado
		// basta verificar se o retorno não é nulo
		Assertions.assertNotNull(productDto);

	}

	@Test
	public void findAllPagedShouldReturnPage() {
		Pageable pageable = PageRequest.of(0, 10);

		Page<ProductDTO> result = productService.findAllPaged(pageable);

		Assertions.assertNotNull(result);

		// verificar se o service tá chamando o repository
		Mockito.verify(productRepository, Mockito.times(1)).findAll(pageable);

	}

	@Test
	public void deleteShouldThrowDataBaseExceptionWhenWhenDependentId() {

		// o DataIntegrityViolationException é capturado pelo catch e é lançado uma
		// DataBaseExeption
		Assertions.assertThrows(DataBaseException.class, () -> {
			productService.delete(dependentId);
		});

		// mockito verifica se o mock fez a chamada do deleteById, pelo menos uma vez:
		Mockito.verify(productRepository, Mockito.times(1)).deleteById(dependentId);
	}

	@Test
	public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExists() {

		// o EmptyResultDataAccessExpection é capturado pelo catch e é lançado uma
		// ResourceNotFoundException
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			productService.delete(nonExistingId);
		});

		// mockito verifica se o mock fez a chamada do deleteById, pelo menos uma vez:
		Mockito.verify(productRepository, Mockito.times(1)).deleteById(nonExistingId);
	}

	@Test
	public void deleteShouldDoNothingWhenIdExists() {

		Assertions.assertDoesNotThrow(() -> {
			productService.delete(existingId);
		});

		// mockito verifica se o mock fez a chamada do deleteById, pelo menos uma vez:
		Mockito.verify(productRepository, Mockito.times(1)).deleteById(existingId);
	}

}
