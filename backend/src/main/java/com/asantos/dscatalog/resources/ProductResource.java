package com.asantos.dscatalog.resources;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.asantos.dscatalog.dto.ProductDTO;
import com.asantos.dscatalog.services.ProductService;

@RestController
@RequestMapping(value = "/products")
public class ProductResource {

	@Autowired
	private ProductService productService;

	@GetMapping()
	public ResponseEntity<Page<ProductDTO>> findAllProducts(Pageable pageable) {

		Page<ProductDTO> categoryDtoList = productService.findAllPaged(pageable);
		return ResponseEntity.ok().body(categoryDtoList);
	}

	@GetMapping(value = "/{id}")
	public ResponseEntity<ProductDTO> findProductById(@PathVariable Long id) {
		ProductDTO productDto = productService.findById(id);
		return ResponseEntity.ok().body(productDto);
	}

	@PostMapping()
	public ResponseEntity<ProductDTO> addNewCategory(@RequestBody ProductDTO productDto) {
		productDto = productService.insert(productDto);

		// best practice: return on response headers the path of the new resource
		// created
		URI newResourceUri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
				.buildAndExpand(productDto.getId()).toUri();
		return ResponseEntity.created(newResourceUri).body(productDto);
	}

	@PutMapping(value = "/{id}")
	public ResponseEntity<ProductDTO> updateInfoProduct(@PathVariable Long id, @RequestBody ProductDTO productDto) {
		productDto = productService.update(id, productDto);
		return ResponseEntity.ok().body(productDto);
	}

	@DeleteMapping(value = "/{id}")
	public ResponseEntity<ProductDTO> deleteCategory(@PathVariable Long id) {
		productService.delete(id);
		return ResponseEntity.noContent().build();
	}

}
