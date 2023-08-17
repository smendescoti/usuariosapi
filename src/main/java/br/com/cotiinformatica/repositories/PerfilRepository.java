package br.com.cotiinformatica.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.cotiinformatica.entities.Perfil;

public interface PerfilRepository extends JpaRepository<Perfil, UUID> {

	@Query( "select p from Perfil p " + 
	        "where p.nome = :nome")
	public Perfil findByNome(
			@Param("nome") String nome);
}
