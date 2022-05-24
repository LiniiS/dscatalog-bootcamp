package com.asantos.dscatalog.resources;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.asantos.dscatalog.dto.CategoryDTO;
import com.asantos.dscatalog.services.CategoryService;

@RestController
@RequestMapping(value ="/categories")
public class CategoryResource {
	
	@Autowired
	private CategoryService categoryService;
	
	
	@GetMapping()
	public ResponseEntity<List<CategoryDTO>> findAll(){
		List<CategoryDTO> categoryDtoList = categoryService.findAll();
		return ResponseEntity.ok().body(categoryDtoList);
	}
	
	@GetMapping(value = "/{id}")
	public ResponseEntity<CategoryDTO> findById(@PathVariable Long id){
		CategoryDTO categoryDto = categoryService.findById(id);
		return ResponseEntity.ok().body(categoryDto);
	}
}
