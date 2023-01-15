package com.andersondev.minhasfinancas.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.andersondev.minhasfinancas.model.entity.Usuario;
import com.andersondev.minhasfinancas.model.repository.UsuarioRepository;

@Service
public class SecurityUserDetailsService implements UserDetailsService {

	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		Usuario usuarioEncontrado = usuarioRepository
				.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("Email não cadastrado."));
		return User.builder()
				.username(usuarioEncontrado.getEmail())
				.password(usuarioEncontrado.getSenha())
				.roles("USER")
				.build(); 
	}
	
}
