package com.andersondev.minhasfinancas.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;

import com.andersondev.minhasfinancas.exception.ErroAutenticacao;
import com.andersondev.minhasfinancas.exception.RegraNegocioException;
import com.andersondev.minhasfinancas.model.entity.Usuario;
import com.andersondev.minhasfinancas.model.repository.UsuarioRepository;
import com.andersondev.minhasfinancas.service.impl.UsuarioServiceImpl;

@SpringBootTest
@ActiveProfiles("test")
public class UsuarioServiceTest {

	private static final String messageException = "Já existe um usuário cadastrado com este email.";
	
	@SpyBean
	private UsuarioServiceImpl service;
	
	@MockBean
	private UsuarioRepository repository;
	
	@Test
	public void deveSalvarUmUsuario() {
		doNothing().when(service).validarEmail(anyString());
		
		Usuario usuario = Usuario.builder()
				.id(1l)
				.nome("nome")
				.email("email@email.com")
				.senha("senha").build();
		
		when(repository.save(any(Usuario.class))).thenReturn(usuario);
		
		Usuario usuarioSalvo = service.salvarUsuario(new Usuario());
		
		assertNotNull(usuarioSalvo);
		assertEquals(Usuario.class, usuarioSalvo.getClass());
		assertEquals(usuario.getId(), usuarioSalvo.getId());
		assertEquals(usuario.getNome(), usuarioSalvo.getNome());
		assertEquals(usuario.getEmail(), usuarioSalvo.getEmail());
		assertEquals(usuario.getSenha(), usuarioSalvo.getSenha());
	}
	
	@Test
	public void naoDeveSalvarUmUsuarioComEmailJaCadastrado() {
		String email = "email@email.com";
		Usuario usuario = Usuario.builder().email(email).build();
		doThrow(RegraNegocioException.class).when(service).validarEmail(usuario.getEmail());
		
		try {
			service.salvarUsuario(usuario);
		} catch (Exception ex) {
			assertNotNull(ex);
			assertEquals(RegraNegocioException.class, ex.getClass());
			verify(repository, never()).save(usuario);
		}
	}
	
	@Test
	public void deveAutenticarUmUsuarioComSucesso() {
		String email = "email@email.com";
		String senha = "senha";
		
		Usuario usuario = Usuario.builder().email(email).senha(senha).id(1l).build();
		when(repository.findByEmail(usuario.getEmail())).thenReturn(Optional.of(usuario));
		
		Usuario resultUsuario = service.autenticar(email, senha);
		
		assertNotNull(resultUsuario);
		assertEquals(Usuario.class, resultUsuario.getClass());
	}
	
	@Test
	public void deveLancarErroQuandoNaoEncontrarUsuarioCadastradoComOEmailInformado() {
		when(repository.findByEmail(anyString()))
		.thenReturn(Optional.empty());
		
		try {
			service.autenticar("email@email.com", "senha");
		} catch (Exception ex) {
			assertNotNull(ex);
			assertEquals(ErroAutenticacao.class, ex.getClass());
			assertEquals("Usuário não encontrado para o email informado.", ex.getMessage());
		}
	}
	
	@Test
	public void deveLancarErroQuandoSenhaNaoBater() {
		String senha = "senha";
		Usuario usuario = Usuario.builder().email("email@email.com").senha(senha).build();
		
		when(repository.findByEmail(anyString()))
		.thenReturn(Optional.of(usuario));
		
		String mensagem = assertThrows(ErroAutenticacao.class, () -> {
			service.autenticar(usuario.getEmail(), "1234");
		}).getMessage();
	}
	
	@Test
	public void deveValidarEmail() {
		
		when(repository.existsByEmail(anyString())).thenReturn(false);
		
		service.validarEmail("email@email.com");
		
		verify(repository, times(1)).existsByEmail(anyString());
	}
	
	@Test
	public void deveLancarErroAoValidarEmailQuandoExistirEmailCadastrado() {
		Mockito.when(repository.existsByEmail(anyString()))
		.thenReturn(true);
		
		try {
			service.validarEmail(anyString());
		} catch (Exception ex) {
			assertEquals(RegraNegocioException.class, ex.getClass());		
			assertEquals(messageException, ex.getMessage());
		}
	}
}
