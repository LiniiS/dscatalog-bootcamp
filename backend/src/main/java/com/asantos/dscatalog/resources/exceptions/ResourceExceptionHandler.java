package com.asantos.dscatalog.resources.exceptions;

import java.time.Instant;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.asantos.dscatalog.services.exceptions.ResourceNotFoundException;

@ControllerAdvice
public class ResourceExceptionHandler {
	
	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<StandardError> entityNotFound(ResourceNotFoundException exception, HttpServletRequest request){
		
		StandardError standardError = new StandardError();
		standardError.setTimestamp(Instant.now());
		standardError.setStatus(HttpStatus.NOT_FOUND.value());
		standardError.setError("Resource not found");
		standardError.setMessage(exception.getMessage());
		standardError.setPath(request.getRequestURI());
		
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(standardError);
		
	}

}
