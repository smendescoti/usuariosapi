package br.com.cotiinformatica.entities;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;

@Entity
@Table(name = "usuario")
@Data
public class Usuario {

	@Id
	@Column(name = "id")
	private UUID id;

	@Column(name = "nome", length = 100, nullable = false)
	private String nome;

	@Column(name = "email", length = 50, nullable = false, unique = true)
	private String email;

	@Column(name = "senha", length = 40, nullable = false)
	private String senha;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "datahoracriacao", nullable = false)
	private Instant dataHoraCriacao;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "datahoraalteracao", nullable = false)
	private Instant dataHoraAlteracao;
	
	@ManyToOne //muitos usuários para 1 perfil
	@JoinColumn(name = "perfil_id", nullable = false)
	private Perfil perfil;
}
