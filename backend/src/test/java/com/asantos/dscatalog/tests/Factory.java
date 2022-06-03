package com.asantos.dscatalog.tests;

import java.time.Instant;

import com.asantos.dscatalog.dto.ProductDTO;
import com.asantos.dscatalog.entities.Category;
import com.asantos.dscatalog.entities.Product;

public class Factory {

	public static Product createProduct() {
		Product product = new Product("Phone", "Best Smartphone", 1500.0, "https://img.com/img.png", Instant.parse("2020-10-20T03:00:00Z"));
		product.getCategories().add(createCategory());
		return product;		
	}
	
	public static ProductDTO createProductDTO() {
		Product product = createProduct();
		return new ProductDTO(product, product.getCategories());
	}
	
	
	public static Category createCategory() {
		return new Category(1L, "Electronics");
	}
}