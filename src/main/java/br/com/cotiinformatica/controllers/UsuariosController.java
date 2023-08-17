package br.com.cotiinformatica.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.cotiinformatica.dtos.AtualizarDadosRequestDto;
import br.com.cotiinformatica.dtos.AtualizarDadosResponseDto;
import br.com.cotiinformatica.dtos.AutenticarRequestDto;
import br.com.cotiinformatica.dtos.AutenticarResponseDto;
import br.com.cotiinformatica.dtos.CriarContaRequestDto;
import br.com.cotiinformatica.dtos.CriarContaResponseDto;
import br.com.cotiinformatica.dtos.RecuperarSenhaRequestDto;
import br.com.cotiinformatica.dtos.RecuperarSenhaResponseDto;
import br.com.cotiinformatica.services.UsuarioService;
import jakarta.validation.Valid;

@RestController
@RequestMapping(value = "/api/usuarios")
public class UsuariosController {
	
	@Autowired
	private UsuarioService usuarioService;

	@PostMapping("autenticar")
	public ResponseEntity<AutenticarResponseDto> autenticar(@RequestBody @Valid AutenticarRequestDto dto) throws Exception {
		AutenticarResponseDto response = usuarioService.autenticar(dto);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	
	@PostMapping("criar-conta")
	public ResponseEntity<CriarContaResponseDto> criarConta(@RequestBody @Valid CriarContaRequestDto dto) throws Exception {
		CriarContaResponseDto response = usuarioService.criarConta(dto);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}
	
	@PostMapping("recuperar-senha")
	public ResponseEntity<RecuperarSenhaResponseDto> recuperarSenha(@RequestBody @Valid RecuperarSenhaRequestDto dto) throws Exception {
		RecuperarSenhaResponseDto response = usuarioService.recuperarSenha(dto);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	
	@PutMapping("atualizar-dados")
	public ResponseEntity<AtualizarDadosResponseDto> atualizarDados(
			@RequestBody @Valid AtualizarDadosRequestDto dto, 
			@RequestHeader("Authorization") String authorizationHeader) throws Exception {
		
		String accessToken = authorizationHeader.replace("Bearer ", "");
		
		AtualizarDadosResponseDto response = usuarioService.atualizarDados(dto, accessToken);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
}
