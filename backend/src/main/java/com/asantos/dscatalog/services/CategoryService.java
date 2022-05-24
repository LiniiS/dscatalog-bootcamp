package com.asantos.dscatalog.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.asantos.dscatalog.dto.CategoryDTO;
import com.asantos.dscatalog.entities.Category;
import com.asantos.dscatalog.repositories.CategoryRepository;
import com.asantos.dscatalog.services.exceptions.ResourceNotFoundException;

@Service
public class CategoryService {
	
	
	@Autowired
	CategoryRepository categoryRepository;
	
	@Transactional(readOnly=true)
	public List<CategoryDTO> findAll(){
		List<Category> categoryList =  categoryRepository.findAll();
		return categoryList.stream().map(item -> new CategoryDTO(item)).collect(Collectors.toList());
		
		
	}

	@Transactional(readOnly=true)
	public CategoryDTO findById(Long id) {
		Optional<Category> optionalCategory = categoryRepository.findById(id);
	//	Category category = optionalCategory.get();
		Category category = optionalCategory.orElseThrow(() -> new ResourceNotFoundException("Resource not found!!!"));
		return new CategoryDTO(category);
	}

	@Transactional
	public CategoryDTO insert(CategoryDTO categoryDto) {
		
		Category newCategory = new Category();
		newCategory.setName(categoryDto.getName());
		newCategory = categoryRepository.save(newCategory);
		
		return new CategoryDTO(newCategory);
	}

}
