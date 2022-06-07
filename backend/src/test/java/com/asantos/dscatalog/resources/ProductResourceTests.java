package com.asantos.dscatalog.resources;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.asantos.dscatalog.dto.ProductDTO;
import com.asantos.dscatalog.services.ProductService;
import com.asantos.dscatalog.services.exceptions.DataBaseException;
import com.asantos.dscatalog.services.exceptions.ResourceNotFoundException;
import com.asantos.dscatalog.tests.Factory;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(ProductResource.class)
public class ProductResourceTests {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private ProductService productService;

	@Autowired
	private ObjectMapper objectMapper;

	private long existingId;
	private long nonExistingId;
	private long dependentId;
	private ProductDTO productDTO;
	private PageImpl<ProductDTO> page;

	@BeforeEach
	void setUp() throws Exception {
		existingId = 15L;
		nonExistingId = 2L;
		dependentId = 3L;
		productDTO = Factory.createProductDTO();
		page = new PageImpl<>(List.of(productDTO));
		// -> poderia ser um import Static, observar o pcte do import do any()
		when(productService.findAllPaged(any())).thenReturn(page);

		when(productService.findById(existingId)).thenReturn(productDTO);
		when(productService.findById(nonExistingId)).thenThrow(ResourceNotFoundException.class);

		when(productService.update(eq(existingId), any())).thenReturn(productDTO);
		when(productService.update(eq(nonExistingId), any())).thenThrow(ResourceNotFoundException.class);

		// insert do service
		when(productService.insert(any())).thenReturn(productDTO);

		// cenario do comportamento no delete
		doNothing().when(productService).delete(existingId);
		doThrow(ResourceNotFoundException.class).when(productService).delete(nonExistingId);
		doThrow(DataBaseException.class).when(productService).delete(dependentId);
	}

	@Test
	public void deleteShouldReturnNoContentWhenIdExists() throws Exception {
		ResultActions result = mockMvc.perform(delete("/products/{id}", existingId));

		result.andExpect(status().isNoContent());
	}

	@Test
	public void deleteShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {
		ResultActions result = mockMvc.perform(delete("/products/{id}", nonExistingId));

		result.andExpect(status().isNotFound());
	}

	// não tem cenário (when)
	@Test
	public void insertShouldReturnProductDTOCreated() throws Exception {

		String jsonBody = objectMapper.writeValueAsString(productDTO);

		ResultActions result = mockMvc.perform(post("/products").content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON));

		result.andExpect(status().isCreated());
		result.andExpect(jsonPath("$.id").exists());
		result.andExpect(jsonPath("$.name").exists());
		result.andExpect(jsonPath("$.description").exists());
	}

	@Test
	public void updateShouldReturnProductDTOWhenIdExists() throws Exception {

		// passando o objeto para string -> o put envia um corpo junto à requisição
		String jsonBody = objectMapper.writeValueAsString(productDTO);

		ResultActions result = mockMvc.perform(put("/products/{id}", existingId).content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON));

		result.andExpect(status().isOk());
		result.andExpect(jsonPath("$.id").exists());
		result.andExpect(jsonPath("$.name").exists());
		result.andExpect(jsonPath("$.description").exists());

	}

	@Test
	public void updateShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {

		String jsonBody = objectMapper.writeValueAsString(productDTO);

		ResultActions result = mockMvc.perform(put("/products/{id}", nonExistingId).content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON));

		result.andExpect(status().isNotFound());
	}

	@Test
	public void findAllShouldReturnPage() throws Exception {

		ResultActions result = mockMvc.perform(get("/products").accept(MediaType.APPLICATION_JSON));

		result.andExpect(status().isOk());
	}

	@Test
	public void findByIdShouldReturnProductWhenIdExists() throws Exception {
		ResultActions result = mockMvc.perform(get("/products/{id}", existingId).accept(MediaType.APPLICATION_JSON));

		result.andExpect(status().isOk());

		// verificar se retornou um produto?
		// no corpo da resposta existe um campo id?
		result.andExpect(jsonPath("$.id").exists());
		result.andExpect(jsonPath("$.name").exists());
		result.andExpect(jsonPath("$.description").exists());

	}

	@Test
	public void findByIdShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {

		ResultActions result = mockMvc.perform(get("/products/{id}", nonExistingId).accept(MediaType.APPLICATION_JSON));

		result.andExpect(status().isNotFound());

	}

}
