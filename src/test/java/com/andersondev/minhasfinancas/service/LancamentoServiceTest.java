package com.andersondev.minhasfinancas.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Example;
import org.springframework.test.context.ActiveProfiles;

import com.andersondev.minhasfinancas.exception.RegraNegocioException;
import com.andersondev.minhasfinancas.model.entity.Lancamento;
import com.andersondev.minhasfinancas.model.entity.Usuario;
import com.andersondev.minhasfinancas.model.enums.StatusLancamento;
import com.andersondev.minhasfinancas.model.repository.LancamentoRepository;
import com.andersondev.minhasfinancas.model.repository.LancamentoRepositoryTest;
import com.andersondev.minhasfinancas.service.impl.LancamentoServiceImpl;
@SpringBootTest
@ActiveProfiles("test")
public class LancamentoServiceTest {
	
	@InjectMocks
	@Spy
	private LancamentoServiceImpl service;
	
	@Mock
	private LancamentoRepository repository;
	
	@Test
	public void deveSalvarUmLancamento() {
		//cenário
		Lancamento lancamentoASalvar = LancamentoRepositoryTest.criarLancamento();
		doNothing().when(service).validar(lancamentoASalvar);
		
		Lancamento lancamentoSalvo = LancamentoRepositoryTest.criarLancamento();
		lancamentoSalvo.setId(1l);
		lancamentoSalvo.setStatus(StatusLancamento.PENDENTE);
		
		when(repository.save(lancamentoASalvar)).thenReturn(lancamentoSalvo);
		
		//execução
		Lancamento lancamento = service.salvar(lancamentoASalvar);
		
		//verificação
		assertEquals(lancamento.getId(), lancamentoSalvo.getId());
		assertEquals(lancamento.getStatus(), lancamentoSalvo.getStatus());
	}
	
	@Test
	public void naoDeveSalvarUmLancamentoQuandoHouverErroDeValidacao() {
		//cenário
		Lancamento lancamentoASalvar = LancamentoRepositoryTest.criarLancamento();
		doThrow( RegraNegocioException.class ).when(service).validar(lancamentoASalvar);
		
		//execução e verificação
		assertThrows(RegraNegocioException.class, () -> service.salvar(lancamentoASalvar));
		verify(repository, never()).save(lancamentoASalvar);
	}
	
	@Test
	public void deveAtualizarUmLancamento() {
		//cenário
		Lancamento lancamentoSalvo = LancamentoRepositoryTest.criarLancamento();
		lancamentoSalvo.setId(1l);
		lancamentoSalvo.setStatus(StatusLancamento.PENDENTE);
		
		doNothing().when(service).validar(lancamentoSalvo);
		when(repository.save(lancamentoSalvo)).thenReturn(lancamentoSalvo);
		
		//execução
		service.atualizar(lancamentoSalvo);
		
		//verificação
		verify(repository, times(1)).save(lancamentoSalvo);
	}
	
	@Test
	public void deveLancarErroAoTentarAtualizarUmLancamentoQueAindaNaoFoiSalvo() {
		//cenário
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		
		//execução e verificação
		assertThrows(NullPointerException.class, () -> service.atualizar(lancamento));
		verify(repository, never()).save(lancamento);
	}
	
	@Test
	public void deveDeletarUmLancamento() {
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(1l);
		
		doNothing().when(repository).delete(lancamento);
		
		service.deletar(lancamento);
		
		verify(repository, times(1)).delete(lancamento);
	}
	
	@Test
	public void deveLancarErroAoTentarDeletarUmLancamentoQueAindaNaoFoiSalvo() {
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		
		assertThrows(NullPointerException.class, () -> service.deletar(lancamento));
		verify(repository, never()).delete(lancamento);
	}
	
	@Test
	public void deveFiltrarLancamentos() {
		//cenário
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(1l);
		
		List<Lancamento> lista = Arrays.asList(lancamento);
		when(repository.findAll(any(Example.class))).thenReturn(lista);
		
		//execução
		List<Lancamento> resultado = service.buscar(lancamento);
		
		//verificação
		assertNotNull(resultado);
		assertEquals(1, resultado.size());
		assertEquals(Lancamento.class, resultado.get(0).getClass());
		assertEquals(lancamento, resultado.get(0));
	}
	
	@Test
	public void deveAtualizarOStatusDeUmLancamento() {
		//cenário
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(1l);
		doNothing().when(service).validar(any(Lancamento.class));;

		//execução
		service.atualizarStatus(lancamento, StatusLancamento.EFETIVADO);

		//verificação
		assertEquals(lancamento.getStatus(), StatusLancamento.EFETIVADO);
		verify(service, times(1)).atualizar(any(Lancamento.class));
	}
	
	@Test
	public void deveObterUmLancamentoPorID() {
		//cenário
		Long id = 1l;
		
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(id);
		
		when(repository.findById(id)).thenReturn(Optional.of(lancamento));
		
		//execução
		Optional<Lancamento> resultado = service.obterPorId(id);
		
		//verificação
		assertEquals(true, resultado.isPresent());
		assertEquals(Optional.class, resultado.getClass());
	}
	
	@Test
	public void deveRetornarVazioQuandoOLancamentoNaoExite() {
		//cenário
		Long id = 1l;
		
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(id);
		
		when(repository.findById(id)).thenReturn(Optional.empty());
		
		//execução
		Optional<Lancamento> resultado = service.obterPorId(id);
		
		//verificação
		assertEquals(false, resultado.isPresent());
		assertEquals(Optional.class, resultado.getClass());
	}
	
	@Test
	public void deveLancarErrosAoValidarUmLancamento() {
		String mensagemEsperada1 = "Informe uma Descrição válida.";
		String mensagemEsperada2 = "Informe um Mês válido.";
		String mensagemEsperada3 = "Informe um Ano válido.";
		String mensagemEsperada4 = "Informe o Usuário";
		String mensagemEsperada5 = "Informe um Valor válido.";
		String mensagemEsperada6 = "Informe um Tipo de lançamento.";
		
		Lancamento lancamento = new Lancamento();
		
		String mensagem1 = assertThrows(RegraNegocioException.class, () -> { 
			service.validar(lancamento);
		}).getMessage();
		assertEquals(mensagemEsperada1, mensagem1);
		
		lancamento.setDescricao("");
		
		mensagem1 = assertThrows(RegraNegocioException.class, () -> { 
			service.validar(lancamento);
		}).getMessage();
		assertEquals(mensagemEsperada1, mensagem1);
		
		lancamento.setDescricao("Salário");
		
		String mensagem2 = assertThrows(RegraNegocioException.class, () -> {
			service.validar(lancamento);
		}).getMessage();
		assertEquals(mensagemEsperada2, mensagem2);
		
		lancamento.setMes(0);
		
		mensagem2 = assertThrows(RegraNegocioException.class, () -> {
			service.validar(lancamento);
		}).getMessage();
		assertEquals(mensagemEsperada2, mensagem2);
		
		lancamento.setMes(13);
		
		mensagem2 = assertThrows(RegraNegocioException.class, () -> {
			service.validar(lancamento);
		}).getMessage();
		assertEquals(mensagemEsperada2, mensagem2);
		
		lancamento.setMes(1);
		
		String mensagem3 = assertThrows(RegraNegocioException.class, () -> {
			service.validar(lancamento);
		}).getMessage();
		assertEquals(mensagemEsperada3, mensagem3);
		
		lancamento.setAno(999);
		
		mensagem3 = assertThrows(RegraNegocioException.class, () -> {
			service.validar(lancamento);
		}).getMessage();
		assertEquals(mensagemEsperada3, mensagem3);
		
		lancamento.setAno(2022);
		
		String mensagem4 = assertThrows(RegraNegocioException.class, () -> {
			service.validar(lancamento);
		}).getMessage();
		assertEquals(mensagemEsperada4, mensagem4);
		
		lancamento.setUsuario(new Usuario());
		lancamento.getUsuario().setId(null);
		
		mensagem4 = assertThrows(RegraNegocioException.class, () -> {
			service.validar(lancamento);
		}).getMessage();
		assertEquals(mensagemEsperada4, mensagem4);
		
		lancamento.getUsuario().setId(1l);
		
		String mensagem5 = assertThrows(RegraNegocioException.class, () -> {
			service.validar(lancamento);
		}).getMessage();
		assertEquals(mensagemEsperada5, mensagem5);
		
		lancamento.setValor(BigDecimal.ZERO);
		
		mensagem5 = assertThrows(RegraNegocioException.class, () -> {
			service.validar(lancamento);
		}).getMessage();
		assertEquals(mensagemEsperada5, mensagem5);
		
		lancamento.setValor(BigDecimal.valueOf(1));
	
		String mensagem6 = assertThrows(RegraNegocioException.class, () -> {
			service.validar(lancamento);
		}).getMessage();
		assertEquals(mensagemEsperada6, mensagem6);
	}
}
