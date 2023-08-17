package br.com.cotiinformatica.dtos;

import java.time.Instant;
import java.util.UUID;

import lombok.Data;

@Data
public class AutenticarResponseDto {

	private UUID id;
	private String nome;
	private String email;
	private String accessToken;
	private Instant dataHoraAcesso;
	private Instant dataHoraExpiracao;
}
