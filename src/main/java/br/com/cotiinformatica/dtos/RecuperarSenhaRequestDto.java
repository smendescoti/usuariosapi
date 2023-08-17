package br.com.cotiinformatica.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RecuperarSenhaRequestDto {

	@Email(message = "Por favor, informe um endereço de email válido.")
	@NotBlank(message = "Por favor, informe o email do usuário.")
	private String email;
}
