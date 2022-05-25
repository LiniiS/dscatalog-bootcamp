package com.asantos.dscatalog.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.asantos.dscatalog.dto.CategoryDTO;
import com.asantos.dscatalog.entities.Category;
import com.asantos.dscatalog.repositories.CategoryRepository;
import com.asantos.dscatalog.services.exceptions.DataBaseException;
import com.asantos.dscatalog.services.exceptions.ResourceNotFoundException;

@Service
public class CategoryService {

	@Autowired
	CategoryRepository categoryRepository;

	@Transactional(readOnly = true)
	public List<CategoryDTO> findAll() {
		List<Category> categoryList = categoryRepository.findAll();
		return categoryList.stream().map(item -> new CategoryDTO(item)).collect(Collectors.toList());

	}

	@Transactional(readOnly = true)
	public CategoryDTO findById(Long id) {
		Optional<Category> optionalCategory = categoryRepository.findById(id);
		// Category category = optionalCategory.get();
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

	@Transactional
	public CategoryDTO update(Long id, CategoryDTO categoryDto) {
		try {
			@SuppressWarnings("deprecation")
			Category categoryUpdated = categoryRepository.getOne(id);
			categoryUpdated.setName(categoryDto.getName());
			categoryUpdated = categoryRepository.save(categoryUpdated);
			return new CategoryDTO(categoryUpdated);
		} catch (EntityNotFoundException e) {
			throw new ResourceNotFoundException("Id not found: " + id);
		}

	}

	public void delete(Long id) {
		
		try {
			categoryRepository.deleteById(id);			
		} catch (EmptyResultDataAccessException e) {
			throw new ResourceNotFoundException("Id not found: " + id);
		}catch (DataIntegrityViolationException e) {
			throw new DataBaseException("Attention! Data Base Integrity Violation!");
		}
		
		
	}

}
