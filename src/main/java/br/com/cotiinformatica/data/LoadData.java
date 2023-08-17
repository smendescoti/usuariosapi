package br.com.cotiinformatica.data;

import java.time.Instant;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import br.com.cotiinformatica.components.MD5Component;
import br.com.cotiinformatica.entities.Perfil;
import br.com.cotiinformatica.entities.Usuario;
import br.com.cotiinformatica.repositories.PerfilRepository;
import br.com.cotiinformatica.repositories.UsuarioRepository;

@Component
public class LoadData implements ApplicationRunner {

	@Autowired
	private PerfilRepository perfilRepository;
	
	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private MD5Component md5Component;
	
	@Override
	public void run(ApplicationArguments args) throws Exception {

		// verificando se a tabela de perfil já possui um Perfil ADMIN...
		Perfil perfilAdmin = perfilRepository.findByNome("ADMIN");
		if (perfilAdmin == null) {

			perfilAdmin = new Perfil();
			perfilAdmin.setId(UUID.randomUUID());
			perfilAdmin.setNome("ADMIN");

			perfilRepository.save(perfilAdmin);
		}

		// verificando se a tabela de perfil já possui um Perfil ADMIN...
		Perfil perfilUser = perfilRepository.findByNome("USER");
		if (perfilUser == null) {

			perfilUser = new Perfil();
			perfilUser.setId(UUID.randomUUID());
			perfilUser.setNome("USER");

			perfilRepository.save(perfilUser);
		}
		
		// verificando se ja existe um usuário ADMIN padrão
		Usuario usuarioAdmin = usuarioRepository.findByEmail("admin@cotiinformatica.com.br");
		if(usuarioAdmin == null) {
			
			usuarioAdmin = new Usuario();
			usuarioAdmin.setId(UUID.randomUUID());
			usuarioAdmin.setNome("Usuário Administrador");
			usuarioAdmin.setEmail("admin@cotiinformatica.com.br");
			usuarioAdmin.setSenha(md5Component.encrypt("@Admin1234"));
			usuarioAdmin.setDataHoraCriacao(Instant.now());
			usuarioAdmin.setDataHoraAlteracao(Instant.now());
			usuarioAdmin.setPerfil(perfilAdmin);
			
			usuarioRepository.save(usuarioAdmin);
		}
	}
}
