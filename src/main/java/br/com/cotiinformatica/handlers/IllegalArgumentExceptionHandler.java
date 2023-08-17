package br.com.cotiinformatica.handlers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import br.com.cotiinformatica.dtos.ErrorResponseDto;

@ControllerAdvice
public class IllegalArgumentExceptionHandler {

	@ExceptionHandler(IllegalArgumentException.class)
	@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
	@ResponseBody
	public ErrorResponseDto handleIllegalArgumentException(IllegalArgumentException ex) {
		
		//capturando a mensagem de erro
		List<String> erros = new ArrayList<String>();
		erros.add(ex.getMessage());
		
		//retornando o DTO contendo o erro
		ErrorResponseDto response = new ErrorResponseDto();
		response.setStatus(HttpStatus.UNPROCESSABLE_ENTITY);
		response.setErrors(erros);
		
		return response;
	}
}
