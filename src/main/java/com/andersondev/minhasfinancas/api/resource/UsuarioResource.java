package com.andersondev.minhasfinancas.api.resource;

import java.math.BigDecimal;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.andersondev.minhasfinancas.api.dto.UsuarioDTO;
import com.andersondev.minhasfinancas.exception.ErroAutenticacao;
import com.andersondev.minhasfinancas.exception.RegraNegocioException;
import com.andersondev.minhasfinancas.model.entity.Usuario;
import com.andersondev.minhasfinancas.service.LancamentoService;
import com.andersondev.minhasfinancas.service.UsuarioService;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioResource {
	
	
	@Autowired
	private UsuarioService service;
	
	@Autowired
	private LancamentoService lancamentoService;
	
	@Autowired
	private ModelMapper mapper;
	
	@PostMapping("/autenticar")
	public ResponseEntity autenticarUsuario(@RequestBody UsuarioDTO dto){
		
			try {
				return ResponseEntity.ok(service.autenticar(dto.getEmail(), dto.getSenha()));
				
			}catch(ErroAutenticacao e) {
				return ResponseEntity.badRequest().body(e.getMessage());
			}
	}
	
	@PostMapping
	public ResponseEntity salvar(@RequestBody UsuarioDTO dto){
		
		Usuario usuario = mapper.map(dto, Usuario.class);
		
		try {
			return new ResponseEntity(service.salvarUsuario(usuario), HttpStatus.CREATED);
		} catch (RegraNegocioException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	@GetMapping("{id}/saldo")
	public ResponseEntity obterSaldo(@PathVariable("id") Long id) {
			Optional<Usuario> usuario = service.obterPorId(id);
			
			if(!usuario.isPresent()) {
				return new ResponseEntity(HttpStatus.NOT_FOUND);
			}
			
			BigDecimal saldo = lancamentoService.obterSaldoPorUsuario(id);
			return ResponseEntity.ok(saldo);
	}
}
