package com.asantos.dscatalog.services.validations;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerMapping;

import com.asantos.dscatalog.dto.UserUpdateDTO;
import com.asantos.dscatalog.entities.User;
import com.asantos.dscatalog.repositories.UserRepository;
import com.asantos.dscatalog.resources.exceptions.FieldMessage;


public class UserUpdateValidator implements ConstraintValidator<UserUpdateValid, UserUpdateDTO> {
	
	@Autowired
	private HttpServletRequest request;
	
	
	@Autowired
	private UserRepository repository;
	
	
	
	@Override
	public void initialize(UserUpdateValid ann) {
	}

	@Override
	public boolean isValid(UserUpdateDTO userUpdateDto, ConstraintValidatorContext context) {
		
		
		@SuppressWarnings("unchecked")
		var uriVars = (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
		long userIdFromRequest = Long.valueOf(uriVars.get("id"));
		
		
		//lista vazia
		List<FieldMessage> fieldErrorMessageList = new ArrayList<>();
		
		// testes seguindo as regras de negócio:
		//#1 verifica se o email do novo usuário já existe ou se é nulo
		User user = repository.findByEmail(userUpdateDto.getEmail());
		
		if(null != user && userIdFromRequest != user.getId()) {
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