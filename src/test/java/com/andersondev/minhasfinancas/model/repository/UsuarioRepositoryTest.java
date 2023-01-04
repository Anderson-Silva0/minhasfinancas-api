package com.andersondev.minhasfinancas.model.repository;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import com.andersondev.minhasfinancas.model.entity.Usuario;

@SpringBootTest
@ActiveProfiles("test")
public class UsuarioRepositoryTest {
	
	@Autowired
	private UsuarioRepository repository;
	
	@Test
	public void deveVerificarAExistenciaDeUmEmail() {
		//cenário
		Usuario usuario = criarUsuario();
		repository.save(usuario);
		
		//execução
		boolean result = repository.existsByEmail("anderson@gmail.com");
		
		//verificação
		assertTrue(result);
		repository.deleteAll();
	}
	
	@Test
	public void deveRetornarFalsoQuandoNaoHouverUsuarioCadastradoComOEmail() {
		
		repository.deleteAll();
		
		boolean result = repository.existsByEmail("anderson@gmail.com");
		
		Assertions.assertFalse(result);
	}
	
	@Test
	public void devePersistirUmUsuarioNaBaseDeDados() {
		
		Usuario usuario = criarUsuario();
		repository.save(usuario);
		
		assertNotNull(usuario.getId());
		repository.deleteAll();
	}
	@Test
	public void deveBuscarUmUsuarioPorEmail() {
		Usuario usuario = criarUsuario();
		repository.save(usuario);
		Optional<Usuario> result = repository.findByEmail("anderson@gmail.com");
		
		assertNotNull(result.get());
		assertTrue(result.isPresent());
		assertEquals(usuario.getNome(), result.get().getNome());
		assertEquals(usuario.getEmail(), result.get().getEmail());
		assertEquals(usuario.getSenha(), result.get().getSenha());
		
		assertEquals(Optional.class, result.getClass());
		repository.deleteAll();
	}
	
	@Test
	public void deveRetornarVazioAoBuscarUsuarioPorEmailQuandoNaoExisteNaBase() {
		Optional<Usuario> result = repository.findByEmail("anderson@gmai.com");
		
		assertFalse(result.isPresent());
	}
	
	public static Usuario criarUsuario() {
		return Usuario
		.builder()
		.nome("Anderson")
		.email("anderson@gmail.com")
		.senha("123")
		.build();
	}
}
