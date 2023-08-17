package br.com.cotiinformatica.dtos;

import lombok.Data;

@Data
public class MailSenderDto {

	private String mailTo;
	private String subject;
	private String body;
}
