package br.com.cotiinformatica.services;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;

import br.com.cotiinformatica.components.MD5Component;
import br.com.cotiinformatica.components.TokenCreatorComponent;
import br.com.cotiinformatica.dtos.AtualizarDadosRequestDto;
import br.com.cotiinformatica.dtos.AtualizarDadosResponseDto;
import br.com.cotiinformatica.dtos.AutenticarRequestDto;
import br.com.cotiinformatica.dtos.AutenticarResponseDto;
import br.com.cotiinformatica.dtos.CriarContaRequestDto;
import br.com.cotiinformatica.dtos.CriarContaResponseDto;
import br.com.cotiinformatica.dtos.MailSenderDto;
import br.com.cotiinformatica.dtos.RecuperarSenhaRequestDto;
import br.com.cotiinformatica.dtos.RecuperarSenhaResponseDto;
import br.com.cotiinformatica.entities.Perfil;
import br.com.cotiinformatica.entities.Usuario;
import br.com.cotiinformatica.messages.UsuarioMessageProducer;
import br.com.cotiinformatica.repositories.PerfilRepository;
import br.com.cotiinformatica.repositories.UsuarioRepository;

@Service
public class UsuarioService {

	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Autowired
	private PerfilRepository perfilRepository;
	
	@Autowired
	private MD5Component md5Component;
	
	@Autowired
	private TokenCreatorComponent tokenCreatorComponent;
	
	@Autowired
	private UsuarioMessageProducer usuarioMessageProducer;
	
	@Autowired
	private ObjectMapper objectMapper;

	public AutenticarResponseDto autenticar(AutenticarRequestDto dto) throws Exception {

		//verificar se o usuário existe no banco de dados
		Usuario usuario = usuarioRepository.findByEmailAndSenha(dto.getEmail(), md5Component.encrypt(dto.getSenha()));
		//verificar se o usuário não foi encontrado
		if(usuario == null) {
			throw new IllegalArgumentException("Acesso negado. Usuário não encontrado.");
		}
		
		//gerar os dados de autenticação do usuário.
		AutenticarResponseDto response = new AutenticarResponseDto();
		response.setId(usuario.getId());
		response.setNome(usuario.getNome());
		response.setEmail(usuario.getEmail());
		response.setAccessToken(tokenCreatorComponent.generateToken(usuario.getEmail()));
		response.setDataHoraAcesso(Instant.now());
		response.setDataHoraExpiracao(Instant.now()); //TODO implementar a expiração do token
		
		return response;
	}

	public CriarContaResponseDto criarConta(CriarContaRequestDto dto) throws Exception {

		//verificar se o email informado já está cadastrado
		if(usuarioRepository.findByEmail(dto.getEmail()) != null)
			throw new IllegalArgumentException("O email informado já está cadastrado. Tente outro.");

		Usuario usuario = new Usuario();
		
		usuario.setId(UUID.randomUUID());
		usuario.setNome(dto.getNome());
		usuario.setEmail(dto.getEmail());
		usuario.setSenha(md5Component.encrypt(dto.getSenha()));
		usuario.setDataHoraCriacao(Instant.now());
		usuario.setDataHoraAlteracao(Instant.now());
		
		//associando o usuário ao perfil "USER"
		Perfil perfilUser = perfilRepository.findByNome("USER");
		usuario.setPerfil(perfilUser);
		
		//criando o conteúdo da mensagem
		MailSenderDto mailSenderDto = new MailSenderDto();
		mailSenderDto.setMailTo(usuario.getEmail());
		mailSenderDto.setSubject("Criação de conta de usuário realizada com sucesso - COTI Informática");
		mailSenderDto.setBody("<div>"
				+ "<h2>Parabéns " + usuario.getNome() + "!</h2>"
				+ "<div>Sua conta de usuário foi criada com sucesso!</div>"
				+ "<div>Att,</div>"
				+ "<div>Equipe COTI Informática</div>"
				+ "</div>");
		
		//gravando os dados na fila do RabbitMQ
		usuarioMessageProducer.sendMessage(objectMapper.writeValueAsString(mailSenderDto));
		
		//realizando o cadastro do usuário
		usuarioRepository.save(usuario);
		
		CriarContaResponseDto response = new CriarContaResponseDto();
		response.setId(usuario.getId());
		response.setNome(usuario.getNome());
		response.setEmail(usuario.getEmail());
		response.setDataHoraCriacao(usuario.getDataHoraCriacao());
		
		return response;
	}

	public RecuperarSenhaResponseDto recuperarSenha(RecuperarSenhaRequestDto dto) throws Exception {

		//buscar o usuário no banco de dados através do email
		Usuario usuario = usuarioRepository.findByEmail(dto.getEmail());
		
		//verificando se o usuário não foi encontrado
		if(usuario == null)
			throw new IllegalArgumentException("Usuário não encontrado. Verifique o email informado.");
		
		//gerando uma nova senha para o usuário
		Faker faker = new Faker();
		String novaSenha = faker.internet().password(8, 10, true, true, true);
		
		//criando o conteúdo da mensagem
		MailSenderDto mailSenderDto = new MailSenderDto();
		mailSenderDto.setMailTo(usuario.getEmail());
		mailSenderDto.setSubject("Recuperação de senha de usuário realizada com sucesso - COTI Informática");
		mailSenderDto.setBody("<div>"
				+ "<h2>Parabéns " + usuario.getNome() + "!</h2>"
				+ "<div>Uma nova senha foi gerada com sucesso!</div>"
				+ "<div>Acesse o sistema com a senha: <strong>" + novaSenha + "</strong></div>"
				+ "<div>Att,</div>"
				+ "<div>Equipe COTI Informática</div>"
				+ "</div>");

		//gravando os dados na fila do RabbitMQ
		usuarioMessageProducer.sendMessage(objectMapper.writeValueAsString(mailSenderDto));
		
		//atualizando os dados do usuário no banco de dados
		usuario.setSenha(md5Component.encrypt(novaSenha));
		usuarioRepository.save(usuario);
		
		//retornando a resposta da requisição
		RecuperarSenhaResponseDto response = new RecuperarSenhaResponseDto();
		response.setId(usuario.getId());
		response.setNome(usuario.getNome());
		response.setEmail(usuario.getEmail());
		response.setDataHoraRecuperacaoDeSenha(Instant.now());
		
		return response;
	}

	public AtualizarDadosResponseDto atualizarDados(AtualizarDadosRequestDto dto, String accessToken) throws Exception {

		//extrair o email do usuário gravado no token
		String email = tokenCreatorComponent.getEmailFromToken(accessToken);
		
		//buscando o usuário no banco de dados através do ID
		Optional<Usuario> optional = usuarioRepository.findById(dto.getId());
		//verificando se nenhum usuário foi encontrado
		if(optional.isEmpty() || !optional.get().getEmail().equals(email)) 
			throw new IllegalArgumentException("Usuário não encontrado. Verifique o id informado.");
		
		//capturar o usuário obtido do banco de dados
		Usuario usuario = optional.get();
		
		boolean isUpdated = false;
		
		//verificando se o nome está preenchido
		if(dto.getNome() != null && dto.getNome() != "") {
			usuario.setNome(dto.getNome());
			isUpdated = true;
		}
		
		//verificando se as senhas estão preenchidas
		if(dto.getSenhaAtual() != null && dto.getNovaSenha() != null) {
			//verificando se a senha atual está incorreta
			if(!usuario.getSenha().equals(md5Component.encrypt(dto.getSenhaAtual())))
				throw new IllegalArgumentException("Senha atual inválida.");
			else {
				usuario.setSenha(md5Component.encrypt(dto.getNovaSenha()));
				isUpdated = true;
			}				
		}
		
		//verificando se algum campo do usuário foi modificado
		if(isUpdated) {
			//atualizando no banco de dados
			usuario.setDataHoraAlteracao(Instant.now());
			usuarioRepository.save(usuario);
			
			AtualizarDadosResponseDto response = new AtualizarDadosResponseDto();
			response.setId(usuario.getId());
			response.setNome(usuario.getNome());
			response.setEmail(usuario.getEmail());
			response.setDataHoraAlteracao(usuario.getDataHoraAlteracao());
			
			return response;
		}
		else {
			throw new IllegalArgumentException("Nenhum dado do usuário foi alterado.");
		}		
	}
}
