package com.asantos.dscatalog.resources;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.asantos.dscatalog.dto.ProductDTO;
import com.asantos.dscatalog.tests.Factory;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ProductResourceIT {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	private long existingId;
	private long nonExistingId;
	private long countTotalProducts;

	@BeforeEach
	void setUp() throws Exception {
		existingId = 1L;
		nonExistingId = 250L;
		countTotalProducts = 25L;
	}

	@Test
	public void findAllShouldReturnSortedPageWhenSortByName() throws Exception {

		ResultActions result = mockMvc
				.perform(get("/products?page&size=10&sort=name,asc").accept(MediaType.APPLICATION_JSON));

		result.andExpect(status().isOk());
		// acessar o conteúdo da resposta JSONPATH
		// verificando se o valor do campo retornado é igual ao que temos no banco
		result.andExpect(jsonPath("$.totalElements").value(countTotalProducts));

		// verificar se existe o campo content => é quem possui a lista de items
		result.andExpect(jsonPath("$.content").exists());

		// verificar os 3 primeiros elementos da lista
		result.andExpect(jsonPath("$.content[0].name").value("Macbook Pro"));
		result.andExpect(jsonPath("$.content[1].name").value("PC Gamer"));
		result.andExpect(jsonPath("$.content[2].name").value("PC Gamer Alfa"));
	}

	@Test
	public void updateShouldReturnProductDTOWhenIdExists() throws Exception {
		// instanciar aqui msm o ProdutoDTO
		ProductDTO productDTO = Factory.createProductDTO();

		// passando o objeto para string -> o put envia um corpo junto à requisição
		String jsonBody = objectMapper.writeValueAsString(productDTO);

		String expectedName = productDTO.getName();
		String expectedDescription = productDTO.getDescription();

		ResultActions result = mockMvc.perform(put("/products/{id}", existingId).content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON));

		result.andExpect(status().isOk());
		// por ser de integração e sabemos que será retornado um produto, dá pra testar
		// outro cenário
		result.andExpect(jsonPath("$.id").value(existingId));
		result.andExpect(jsonPath("$.name").value(expectedName));
		result.andExpect(jsonPath("$.description").value(expectedDescription));

	}

	@Test
	public void updateShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {
		ProductDTO productDTO = Factory.createProductDTO();

		String jsonBody = objectMapper.writeValueAsString(productDTO);

		ResultActions result = mockMvc.perform(put("/products/{id}", nonExistingId).content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON));

		result.andExpect(status().isNotFound());

	}
}
