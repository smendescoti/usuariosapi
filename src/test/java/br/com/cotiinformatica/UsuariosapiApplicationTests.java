package br.com.cotiinformatica;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Locale;
import java.util.UUID;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;

import br.com.cotiinformatica.dtos.AtualizarDadosRequestDto;
import br.com.cotiinformatica.dtos.AutenticarRequestDto;
import br.com.cotiinformatica.dtos.AutenticarResponseDto;
import br.com.cotiinformatica.dtos.CriarContaRequestDto;
import br.com.cotiinformatica.dtos.RecuperarSenhaRequestDto;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UsuariosapiApplicationTests {
	
	//componente capaz de executar qualquer controlador
	//do projeto Spring Boot, por exemplo @RestController
	@Autowired
	private MockMvc mockMvc;
	
	//componente utilizado para serializar e deserializar
	//os dados que serão trafegados com a API em formato JSON
	@Autowired
	private ObjectMapper objectMapper;
	
	//atributos para armazenar email e senha do usuário cadastrado
	private static UUID idUsuario;
	private static String emailUsuario;
	private static String senhaUsuario;
	private static String accessToken;

	@Test
	@Order(1)
	void criarContaTest() throws Exception {
		
		//instanciando a biblioteca Java Faker
		Faker faker = new Faker(new Locale("pt-BR"));
		
		//criando um DTO para enviar a requisição POST
		//de criação de conta de usuário da API
		CriarContaRequestDto request = new CriarContaRequestDto();
		request.setNome(faker.name().fullName());
		request.setEmail(faker.internet().emailAddress());
		request.setSenha("@Teste1234");
		
		//executando uma chamada POST para o serviço: /api/usuarios/criar-conta
		//enviando os dados do DTO em formato JSON (serializando)
		//verificando se a reposta da API é 201 (CREATED)
		mockMvc.perform(post("/api/usuarios/criar-conta")
				.contentType("application/json")
				.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isCreated());
		
		//guardar o email e a senha do usuário cadastrado
		emailUsuario = request.getEmail();
		senhaUsuario = request.getSenha();
	}
	
	@Test
	@Order(2)
	void usuarioJaCadastradoTest() throws Exception {
		
		Faker faker = new Faker(new Locale("pt-BR"));
				
		CriarContaRequestDto request = new CriarContaRequestDto();
		request.setNome(faker.name().fullName());
		request.setEmail(emailUsuario);
		request.setSenha("@Teste1234");
		
		mockMvc.perform(post("/api/usuarios/criar-conta")
				.contentType("application/json")
				.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isUnprocessableEntity());
	}
	
	@Test
	@Order(3)
	void criarContaCamposObrigatoriosTest() throws Exception {
		
		CriarContaRequestDto request = new CriarContaRequestDto();
		
		mockMvc.perform(post("/api/usuarios/criar-conta")
				.contentType("application/json")
				.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isBadRequest());		
	}
	
	@Test
	@Order(4)
	void autenticarUsuarioTest() throws Exception {
		
		//criando os dados da requisição
		AutenticarRequestDto request = new AutenticarRequestDto();
		request.setEmail(emailUsuario);
		request.setSenha(senhaUsuario);
		
		//executando uma chamada POST para o serviço: /api/usuarios/autenticar
		//enviando os dados do DTO em formato JSON (serializando)
		//verificando se a reposta da API é 200 (CREATED)
		MvcResult result = mockMvc.perform(post("/api/usuarios/autenticar")
				.contentType("application/json")
				.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isOk())
				.andReturn();
		
		String responseBody = result.getResponse().getContentAsString();
		AutenticarResponseDto response = objectMapper.readValue(responseBody, AutenticarResponseDto.class);
		
		idUsuario = response.getId();
		accessToken = response.getAccessToken();
	}
	
	@Test
	@Order(5)
	void acessoNegadoTest() throws Exception {
		
		//criando os dados da requisição
		AutenticarRequestDto request = new AutenticarRequestDto();
		request.setEmail(emailUsuario);
		request.setSenha("@Teste54321");
		
		//executando uma chamada POST para o serviço: /api/usuarios/autenticar
		//enviando os dados do DTO em formato JSON (serializando)
		//verificando se a reposta da API é 422 (UNPROCESSABLE ENTITY)
		mockMvc.perform(post("/api/usuarios/autenticar")
				.contentType("application/json")
				.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isUnprocessableEntity());
	}
	
	@Test
	@Order(6)
	void autenticarCamposObrigatoriosTest() throws Exception {
		
		AutenticarRequestDto request = new AutenticarRequestDto();
		
		//executando uma chamada POST para o serviço: /api/usuarios/autenticar
		//enviando os dados do DTO em formato JSON (serializando)
		//verificando se a reposta da API é 400 (BAD REQUEST)
		mockMvc.perform(post("/api/usuarios/autenticar")
				.contentType("application/json")
				.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isBadRequest());
	}
	
	@Test
	@Order(7)
	void atualizarDadosTest() throws Exception {
		
		Faker faker = new Faker();
		
		AtualizarDadosRequestDto request = new AtualizarDadosRequestDto();
		request.setId(idUsuario);
		request.setNome(faker.name().fullName());
		request.setSenhaAtual(senhaUsuario);
		request.setNovaSenha("@Teste1234");
		
		mockMvc.perform(put("/api/usuarios/atualizar-dados")
				.header("Authorization", "Bearer " + accessToken)
				.contentType("application/json")
				.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isOk());		
		
		senhaUsuario = request.getNovaSenha();
	}
	
	@Test
	@Order(8)
	void atualizarDadosUsuarioInvalidoTest() throws Exception {
		
		Faker faker = new Faker();
		
		AtualizarDadosRequestDto request = new AtualizarDadosRequestDto();
		request.setId(UUID.randomUUID());
		request.setNome(faker.name().fullName());
		request.setSenhaAtual(senhaUsuario);
		request.setNovaSenha("@Teste1234");
		
		mockMvc.perform(put("/api/usuarios/atualizar-dados")
				.header("Authorization", "Bearer " + accessToken)
				.contentType("application/json")
				.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isUnprocessableEntity());	
	}
	
	@Test
	@Order(9)
	void atualizarDadosCamposVaziosTest() throws Exception {
		
		AtualizarDadosRequestDto request = new AtualizarDadosRequestDto();
		request.setId(idUsuario);
		
		mockMvc.perform(put("/api/usuarios/atualizar-dados")
				.header("Authorization", "Bearer " + accessToken)
				.contentType("application/json")
				.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isUnprocessableEntity());	
	}
	
	@Test
	@Order(10)
	void recuperarSenhaTest() throws Exception {
		
		RecuperarSenhaRequestDto request = new RecuperarSenhaRequestDto();
		request.setEmail(emailUsuario);
		
		//executando uma chamada POST para o serviço: /api/usuarios/recuperar-senha
		//enviando os dados do DTO em formato JSON (serializando)
		//verificando se a resposta da API é 200 (OK)
		mockMvc.perform(post("/api/usuarios/recuperar-senha")
				.contentType("application/json")
				.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isOk());
	}
	
	@Test
	@Order(11)
	void recuperarSenhaUsuarioNaoEncontradoTest() throws Exception {
		
		Faker faker = new Faker();
		
		RecuperarSenhaRequestDto request = new RecuperarSenhaRequestDto();
		request.setEmail(faker.internet().emailAddress());
		
		mockMvc.perform(post("/api/usuarios/recuperar-senha")
				.contentType("application/json")
				.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isUnprocessableEntity());
	}
	
	@Test
	@Order(12)
	void recuperarSenhaCamposObrigatorios() throws Exception {
		
		RecuperarSenhaRequestDto request = new RecuperarSenhaRequestDto();
		
		mockMvc.perform(post("/api/usuarios/recuperar-senha")
				.contentType("application/json")
				.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isBadRequest());
	}
}


