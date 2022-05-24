package com.asantos.dscatalog.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.asantos.dscatalog.entities.Category;
import com.asantos.dscatalog.repositories.CategoryRepository;

@Service
public class CategoryService {
	
	
	@Autowired
	CategoryRepository categoryRepository;
	
	public List<Category> findAll(){
		return categoryRepository.findAll();
	}

}
