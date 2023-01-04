package com.andersondev.minhasfinancas.model.repository;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.andersondev.minhasfinancas.model.entity.Lancamento;
import com.andersondev.minhasfinancas.model.enums.StatusLancamento;
import com.andersondev.minhasfinancas.model.enums.TipoLancamento;

@SpringBootTest
@ActiveProfiles("test")
public class LancamentoRepositoryTest {

	@Autowired
	private LancamentoRepository repository;
	
	@Test
	public void deveSalvarUmLancamento() {
		Lancamento lancamento = criarLancamento();
		lancamento = repository.save(lancamento);
		
		assertNotNull(lancamento.getId());
		repository.deleteAll();
	}
	
	@Test
	public void deveDeletarUmLancamento() {
		Lancamento lancamento = criarLancamento();
		lancamento = repository.save(lancamento);
		
		repository.delete(lancamento);
		
		assertFalse(repository.existsById(lancamento.getId()));
	}
	
	@Test
	public void deveAtualizarUmLancamento() {
		Lancamento lancamento = criarLancamento();
		Lancamento lancamentoSalvo = repository.save(lancamento);
		
		lancamentoSalvo.setAno(2018);
		lancamentoSalvo.setDescricao("Teste Atualizar");
		lancamentoSalvo.setStatus(StatusLancamento.CANCELADO);
		
		Lancamento lancamentoAtualizado = repository.save(lancamentoSalvo);
		
		assertEquals(lancamentoAtualizado.getAno(), 2018);
		assertEquals(lancamentoAtualizado.getDescricao(), "Teste Atualizar");
		assertEquals(lancamentoAtualizado.getStatus(), StatusLancamento.CANCELADO);
	}
	
	@Test
	public void deveBuscarUmLancamentoPorId() {
		Lancamento lancamento = criarLancamento();
		lancamento = repository.save(lancamento);
		
		Optional<Lancamento> lancamentoEncontrado = repository.findById(lancamento.getId());
		
		assertTrue(lancamentoEncontrado.isPresent());
	}
	
	public static Lancamento criarLancamento() {
		return Lancamento.builder()
				.ano(2022)
				.mes(1)
				.descricao("lancamento qualquer")
				.valor(BigDecimal.valueOf(10))
				.tipo(TipoLancamento.RECEITA)
				.status(StatusLancamento.PENDENTE)
				.dataCadastro(LocalDate.now())
				.build();
	}
}
