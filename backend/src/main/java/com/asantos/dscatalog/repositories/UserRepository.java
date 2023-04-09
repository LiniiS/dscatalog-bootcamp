package com.asantos.dscatalog.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.asantos.dscatalog.entities.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	
	//find user by Email -> using JPA template
	User findByEmail(String email);
	
	//Query methods -> olha para os atributos
	//buscas mais simples, filtros simples
	//documentação:https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories.query-methods
	List<User> findByFirstName(String firstName);

}
