package br.com.cotiinformatica.components;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class TokenCreatorComponent {

	/*
	 * @Value
	 * Capturar o valor de propriedades/chave
	 * mapeadas no application.properties
	 */
	@Value("${jwt.secret}")
	private String jwtSecret;
	
	/*
	 * Método para fazermos a geração dos TOKENS JWT
	 */
	public String generateToken(String emailUsuario) throws Exception {
		return Jwts.builder()
				.setSubject(emailUsuario) //email do usuário autenticado
				.setIssuedAt(new Date()) //data de geração do token
				.signWith(SignatureAlgorithm.HS256, jwtSecret) //assinatura antifalsificação
				.compact(); //finalizando e retornando o TOKEN gerado
	}
	
	/*
	 * Método para ler o email do usuário gravado no token
	 */
	public String getEmailFromToken(String accessToken) throws Exception {
		return Jwts.parser()
				.setSigningKey(jwtSecret)
				.parseClaimsJws(accessToken)
				.getBody()
				.getSubject();
	}
}






