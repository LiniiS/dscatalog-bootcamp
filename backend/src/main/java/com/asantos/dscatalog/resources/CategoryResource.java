package com.asantos.dscatalog.resources;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
	public ResponseEntity<List<CategoryDTO>> findAllCategories(){
		List<CategoryDTO> categoryDtoList = categoryService.findAll();
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
}
