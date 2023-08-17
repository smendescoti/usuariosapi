package br.com.cotiinformatica.repositories;

import java.util.UUID;

import org.springframework.data.mongodb.repository.MongoRepository;

import br.com.cotiinformatica.collections.MensagensUsuarios;

public interface MensagensUsuariosRepository extends MongoRepository<MensagensUsuarios, UUID> {

}
