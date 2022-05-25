package com.asantos.dscatalog.resources;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.asantos.dscatalog.dto.CategoryDTO;
import com.asantos.dscatalog.services.CategoryService;

@RestController
@RequestMapping(value ="/categories")
public class CategoryResource {
	
	@Autowired
	private CategoryService categoryService;
	
	
	@GetMapping()
	public ResponseEntity<Page<CategoryDTO>> findAllCategories(
			@RequestParam(value = "page", defaultValue = "0") Integer page,
			@RequestParam(value = "linesPerPage", defaultValue = "12") Integer linesPerPage,
			@RequestParam(value = "direction", defaultValue = "ASC") String direction,				
			@RequestParam(value = "orderBy", defaultValue = "name") String orderBy
			){
		PageRequest pageRequest = PageRequest.of(page, linesPerPage, Direction.valueOf(direction), orderBy);
				
		Page<CategoryDTO> categoryDtoList = categoryService.findAllPaged(pageRequest);
		return ResponseEntity.ok().body(categoryDtoList);
	}
	
	@GetMapping(value = "/{id}")
	public ResponseEntity<CategoryDTO> findCategoryById(@PathVariable Long id){
		CategoryDTO categoryDto = categoryService.findById(id);
		return ResponseEntity.ok().body(categoryDto);
	}
	
	@PostMapping()
	public ResponseEntity<CategoryDTO> addNewCategory(@RequestBody CategoryDTO categoryDto){
		categoryDto = categoryService.insert(categoryDto);
		
		//best practice: return on response headers the path of the new resource created
		URI newResourceUri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
				.buildAndExpand(categoryDto.getId()).toUri();
		return ResponseEntity.created(newResourceUri).body(categoryDto);
	}
	
	
	@PutMapping(value="/{id}")
	public ResponseEntity<CategoryDTO> updateInfoCategory(@PathVariable Long id, @RequestBody CategoryDTO categoryDto){	
		categoryDto = categoryService.update(id, categoryDto);
		return ResponseEntity.ok().body(categoryDto);
	}
	
	
	@DeleteMapping(value="/{id}")
	public ResponseEntity<CategoryDTO> deleteCategory(@PathVariable Long id){	
		 categoryService.delete(id);
		return ResponseEntity.noContent().build();
	}
	
	
	
}
