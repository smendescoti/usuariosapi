package br.com.cotiinformatica.messages;

import java.time.Instant;
import java.util.UUID;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.cotiinformatica.collections.MensagensUsuarios;
import br.com.cotiinformatica.components.MailSenderComponent;
import br.com.cotiinformatica.dtos.MailSenderDto;
import br.com.cotiinformatica.repositories.MensagensUsuariosRepository;

@Service
public class UsuarioMessageConsumer {

	//componente para envio dos emails
	@Autowired
	private MailSenderComponent mailSenderComponent;
	
	//repositório do MongoDB
	@Autowired
	private MensagensUsuariosRepository mensagensUsuariosRepository;
	
	//método para ler e processar cada item da fila (listener => ouvinte)
	@RabbitListener(queues = {
			"${queue.name}" /* nome da fila configurado no application.properties */
	})
	public void receive(@Payload String payload) throws Exception{
		
		//deserializar o conteudo gravado na fila
		ObjectMapper objectMapper = new ObjectMapper();
		MailSenderDto mailSenderDto = objectMapper.readValue(payload, MailSenderDto.class);
		
		//criando um objeto para gravação do log
		MensagensUsuarios log = new MensagensUsuarios();
		log.setId(UUID.randomUUID());
		log.setDataHora(Instant.now());
		log.setMensagem(payload);
		
		try {
			//enviando o email para o usuário
			mailSenderComponent.sendMessage(mailSenderDto);
			
			//escrevendo o log
			log.setTipo("SUCESSO");
			log.setDescricao("Email enviado com sucesso.");
		}
		catch(Exception e) {
			//escrevendo o log
			log.setTipo("ERRO");
			log.setDescricao(e.getMessage());
		}
		finally {
			//gravar o log
			mensagensUsuariosRepository.save(log);
		}
	}
}
