package com.asantos.dscatalog.services.validations;

import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import com.asantos.dscatalog.dto.UserInsertDTO;
import com.asantos.dscatalog.entities.User;
import com.asantos.dscatalog.repositories.UserRepository;
import com.asantos.dscatalog.resources.exceptions.FieldMessage;


public class UserInsertValidator implements ConstraintValidator<UserInsertValid, UserInsertDTO> {
	
	@Autowired
	private UserRepository repository;
	
	@Override
	public void initialize(UserInsertValid ann) {
	}

	@Override
	public boolean isValid(UserInsertDTO userInsertDto, ConstraintValidatorContext context) {
		
		//lista vazia
		List<FieldMessage> fieldErrorMessageList = new ArrayList<>();
		
		// testes seguindo as regras de negócio:
		//#1 verifica se o email do novo usuário já existe no banco de dados (já foi cadastrado)
		User user = repository.findByEmail(userInsertDto.getEmail());
		if(null != user) {
			fieldErrorMessageList.add(new FieldMessage("email", "Email já cadastrado"));
		}
		
		//percorre a lista criada pra procurar por erros e inserir à lista do Beans Validation
		//será recuperada no tratamento de errors
		for (FieldMessage errorFieldMessage : fieldErrorMessageList) {
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate(errorFieldMessage.getMessage()).addPropertyNode(errorFieldMessage.getFieldName())
					.addConstraintViolation();
		}
		
		//testa se a lista está vazia, não houve nenhum erro
		return fieldErrorMessageList.isEmpty();
	}
}