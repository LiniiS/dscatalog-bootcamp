package com.asantos.dscatalog.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.asantos.dscatalog.entities.Category;
import com.asantos.dscatalog.entities.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

	@Query("SELECT obj FROM Product obj JOIN FETCH obj.categories WHERE obj in :products") // usando Pageable, não é
																							// possível usar o Join
																							// Fetch
	List<Product> findProductsCategories(List<Product> products);

	@Query("SELECT DISTINCT obj FROM Product obj INNER JOIN obj.categories cats "
			+ "WHERE (COALESCE( :categories ) IS NULL OR cats IN :categories ) "
			+ "AND ( :name = '' OR LOWER(obj.name) LIKE LOWER(CONCAT('%', :name, '%') ) )") 	// consulta usando forma normal
																// conjuntiva =
																// conjunto de condições sunidas por AND, cada
																// condição pode ter OR, a operação AND para dar
																// falso, só hasta 1, se a primeira será falsa,
																// o resto nem é testado, ontimizando a consulta
	Page<Product> findAllOrFiltered(List<Category> categories, String name, Pageable pageable);

}
