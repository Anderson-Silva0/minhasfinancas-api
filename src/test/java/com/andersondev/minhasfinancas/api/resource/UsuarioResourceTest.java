package com.andersondev.minhasfinancas.api.resource;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.andersondev.minhasfinancas.api.dto.UsuarioDTO;
import com.andersondev.minhasfinancas.exception.ErroAutenticacao;
import com.andersondev.minhasfinancas.exception.RegraNegocioException;
import com.andersondev.minhasfinancas.model.entity.Usuario;
import com.andersondev.minhasfinancas.model.repository.UsuarioRepositoryTest;
import com.andersondev.minhasfinancas.service.LancamentoService;
import com.andersondev.minhasfinancas.service.UsuarioService;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = UsuarioResource.class)
@AutoConfigureMockMvc
public class UsuarioResourceTest {
	
	@MockBean
	private UsuarioService service;
	
	@MockBean
	private LancamentoService lancamentoService;
	
	@MockBean
	private ModelMapper mapper;
	
	@Autowired
	MockMvc mvc;
	
	static final String API = "/api/usuarios";
	static final MediaType JSON = MediaType.APPLICATION_JSON;
	
	@BeforeEach
    void setUp() {
		MockitoAnnotations.openMocks(this);
    }
	
	@Test
	public void deveAutenticarUmUsuario() throws Exception  {
		//cenário
		String email = "anderson@gmail.com";
		String senha = "123";
		
		UsuarioDTO dto = UsuarioDTO.builder()
							.email(email)
							.senha(senha)
							.build();
		Usuario usuario = UsuarioRepositoryTest.criarUsuario();
		usuario.setId(1l);
		
		when( service.autenticar(email, senha) ).thenReturn(usuario);
		
		String json = new ObjectMapper().writeValueAsString(dto); 
		
		//execução e verificação
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
													.post(API.concat("/autenticar"))
													.accept( JSON )
													.contentType( JSON )
													.content( json );

		mvc
			.perform(request)
			.andExpect( MockMvcResultMatchers.status().isOk() )
			.andExpect( MockMvcResultMatchers.jsonPath("id").value(usuario.getId()) )
			.andExpect( MockMvcResultMatchers.jsonPath("nome").value(usuario.getNome()) )
			.andExpect( MockMvcResultMatchers.jsonPath("email").value(usuario.getEmail()) );
	}
	
	@Test
	public void deveRetornarBadRequestAoObterErroDeAutenticacao() throws Exception  {
		//cenário
		String email = "anderson@gmail.com";
		String senha = "123";
		
		UsuarioDTO dto = UsuarioDTO.builder()
							.email(email)
							.senha(senha)
							.build();
		
		when( service.autenticar(dto.getEmail(), dto.getSenha()) ).thenThrow(ErroAutenticacao.class);
		
		String json = new ObjectMapper().writeValueAsString(dto); 
		
		//execução e verificação
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
													.post(API.concat("/autenticar"))
													.accept( JSON )
													.contentType( JSON )
													.content( json );

		mvc
			.perform(request)
			.andExpect( MockMvcResultMatchers.status().isBadRequest() );
	}
	
	@Test
	public void deveCriarUmNovoUsuario() throws Exception {
	
		Usuario usuario = UsuarioRepositoryTest.criarUsuario();
		usuario.setId(1l);
		
		UsuarioDTO dto = UsuarioDTO.builder()
				.nome("Anderson")
				.email("anderson@gmail.com")
				.senha("123")
				.build();
		
		when( mapper.map(any(), any()) ).thenReturn(usuario);
		
		when( service.salvarUsuario(usuario) ).thenReturn(usuario);
		
		String json = new ObjectMapper().writeValueAsString(dto);
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
													.post( API )
													.accept( JSON )
													.contentType( JSON )
													.content( json );
		
		mvc
			.perform( request )
			.andExpect( MockMvcResultMatchers.status().isCreated() )
			.andExpect( MockMvcResultMatchers.jsonPath("id").value(usuario.getId()) )
			.andExpect( MockMvcResultMatchers.jsonPath("nome").value(usuario.getNome()) )
			.andExpect( MockMvcResultMatchers.jsonPath("email").value(usuario.getEmail()) );
	}
	
	@Test
	public void deveRetornarBadRequestAoTentarCriarUmUsuarioInvalido() throws Exception {
	
		Usuario usuario = UsuarioRepositoryTest.criarUsuario();
		usuario.setId(1l);
		
		UsuarioDTO dto = UsuarioDTO.builder()
				.nome("Anderson")
				.email("anderson@gmail.com")
				.senha("123")
				.build();
		
		when( mapper.map(any(), any()) ).thenReturn(usuario);
		
		when( service.salvarUsuario(usuario) ).thenThrow(RegraNegocioException.class);
		
		String json = new ObjectMapper().writeValueAsString(dto);
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
													.post( API )
													.accept( JSON )
													.contentType( JSON )
													.content( json );
		
		mvc
			.perform( request )
			.andExpect( MockMvcResultMatchers.status().isBadRequest() );
	}
	
}
