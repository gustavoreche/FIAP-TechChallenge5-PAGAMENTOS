package com.fiap.techchallenge5.unitario;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.fiap.techchallenge5.infrastructure.carrinho.client.CarrinhoClient;
import com.fiap.techchallenge5.infrastructure.carrinho.client.response.CarrinhoDisponivelParaPagamentoDTO;
import com.fiap.techchallenge5.infrastructure.carrinho.client.response.ItensDoCarrinhoDTO;
import com.fiap.techchallenge5.infrastructure.usuario.client.UsuarioClient;
import com.fiap.techchallenge5.infrastructure.pagamento.model.PagamentoEntity;
import com.fiap.techchallenge5.infrastructure.pagamento.repository.PagamentoRepository;
import com.fiap.techchallenge5.useCase.pagamento.impl.PagamentoUseCaseImpl;
import com.fiap.techchallenge5.useCase.token.impl.TokenUseCaseImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;

public class PagamentoUseCaseTest {

    @Test
    public void realiza_salvaNaBaseDeDados() {
        // preparação
        var clientCarrinho = Mockito.mock(CarrinhoClient.class);
        var clientUsuario = Mockito.mock(UsuarioClient.class);
        var repositoryPagamento = Mockito.mock(PagamentoRepository.class);
        var serviceToken = Mockito.mock(TokenUseCaseImpl.class);

        Mockito.when(serviceToken.pegaJwt(Mockito.any()))
                .thenReturn(
                        mock(DecodedJWT.class)
                );

        Mockito.when(serviceToken.pegaUsuario(Mockito.any()))
                .thenReturn(
                        "tokenTeste"
                );

        Mockito.when(clientUsuario.usuarioExiste(Mockito.any(), Mockito.any()))
                .thenReturn(
                        true
                );

        Mockito.when(clientCarrinho.carrinhoAberto(Mockito.any()))
                .thenReturn(
                        new CarrinhoDisponivelParaPagamentoDTO(
                                "usuario de teste",
                                new BigDecimal("100.00"),
                                List.of(
                                        new ItensDoCarrinhoDTO(
                                                123456L,
                                                new BigDecimal("100.00")
                                        )
                                )
                        )
                );

        Mockito.when(clientCarrinho.finaliza(Mockito.any()))
                .thenReturn(
                        new ResponseEntity<>(HttpStatus.OK)
                );

        Mockito.when(repositoryPagamento.save(Mockito.any()))
                .thenReturn(
                        new PagamentoEntity(
                                "usuario de teste",
                                new BigDecimal("100.00"),
                                LocalDateTime.now()
                        )
                );

        var service = new PagamentoUseCaseImpl(clientCarrinho, clientUsuario, repositoryPagamento, serviceToken);

        // execução
        final var realiza = service.realiza("tokenTeste");

        // avaliação
        verify(serviceToken, times(1)).pegaJwt(Mockito.any());
        verify(serviceToken, times(1)).pegaUsuario(Mockito.any());
        verify(clientUsuario, times(1)).usuarioExiste(Mockito.any(), Mockito.any());
        verify(clientCarrinho, times(1)).carrinhoAberto(Mockito.any());
        verify(clientCarrinho, times(1)).finaliza(Mockito.any());
        verify(repositoryPagamento, times(1)).save(Mockito.any());

        Assertions.assertTrue(realiza);
    }

    @Test
    public void realiza_carrinhoFinalizadoOuCarrinhoNaoExiste_naoSalvaNaBaseDeDados() {
        // preparação
        var clientCarrinho = Mockito.mock(CarrinhoClient.class);
        var clientUsuario = Mockito.mock(UsuarioClient.class);
        var repositoryPagamento = Mockito.mock(PagamentoRepository.class);
        var serviceToken = Mockito.mock(TokenUseCaseImpl.class);

        Mockito.when(serviceToken.pegaJwt(Mockito.any()))
                .thenReturn(
                        mock(DecodedJWT.class)
                );

        Mockito.when(serviceToken.pegaUsuario(Mockito.any()))
                .thenReturn(
                        "tokenTeste"
                );

        Mockito.when(clientUsuario.usuarioExiste(Mockito.any(), Mockito.any()))
                .thenReturn(
                        true
                );

        Mockito.when(clientCarrinho.carrinhoAberto(Mockito.any()))
                .thenReturn(
                        null
                );

        Mockito.when(clientCarrinho.finaliza(Mockito.any()))
                .thenReturn(
                        new ResponseEntity<>(HttpStatus.OK)
                );

        Mockito.when(repositoryPagamento.save(Mockito.any()))
                .thenReturn(
                        new PagamentoEntity(
                                "usuario de teste",
                                new BigDecimal("100.00"),
                                LocalDateTime.now()
                        )
                );

        var service = new PagamentoUseCaseImpl(clientCarrinho, clientUsuario, repositoryPagamento, serviceToken);

        // execução
        final var realiza = service.realiza("tokenTeste");

        // avaliação
        verify(serviceToken, times(1)).pegaJwt(Mockito.any());
        verify(serviceToken, times(1)).pegaUsuario(Mockito.any());
        verify(clientUsuario, times(1)).usuarioExiste(Mockito.any(), Mockito.any());
        verify(clientCarrinho, times(1)).carrinhoAberto(Mockito.any());
        verify(clientCarrinho, times(0)).finaliza(Mockito.any());
        verify(repositoryPagamento, times(0)).save(Mockito.any());

        Assertions.assertFalse(realiza);
    }

    @Test
    public void realiza_carrinhoComProblemaNaFinalizacao_naoSalvaNaBaseDeDados() {
        // preparação
        var clientCarrinho = Mockito.mock(CarrinhoClient.class);
        var clientUsuario = Mockito.mock(UsuarioClient.class);
        var repositoryPagamento = Mockito.mock(PagamentoRepository.class);
        var serviceToken = Mockito.mock(TokenUseCaseImpl.class);

        Mockito.when(serviceToken.pegaJwt(Mockito.any()))
                .thenReturn(
                        mock(DecodedJWT.class)
                );

        Mockito.when(serviceToken.pegaUsuario(Mockito.any()))
                .thenReturn(
                        "tokenTeste"
                );

        Mockito.when(clientUsuario.usuarioExiste(Mockito.any(), Mockito.any()))
                .thenReturn(
                        true
                );

        Mockito.when(clientCarrinho.carrinhoAberto(Mockito.any()))
                .thenReturn(
                        new CarrinhoDisponivelParaPagamentoDTO(
                                "usuario de teste",
                                new BigDecimal("100.00"),
                                List.of(
                                        new ItensDoCarrinhoDTO(
                                                123456L,
                                                new BigDecimal("100.00")
                                        )
                                )
                        )
                );

        Mockito.when(clientCarrinho.finaliza(Mockito.any()))
                .thenReturn(
                        new ResponseEntity<>(HttpStatus.NO_CONTENT)
                );

        Mockito.when(repositoryPagamento.save(Mockito.any()))
                .thenReturn(
                        new PagamentoEntity(
                                "usuario de teste",
                                new BigDecimal("100.00"),
                                LocalDateTime.now()
                        )
                );

        var service = new PagamentoUseCaseImpl(clientCarrinho, clientUsuario, repositoryPagamento, serviceToken);

        // execução
        final var realiza = service.realiza("tokenTeste");

        // avaliação
        verify(serviceToken, times(1)).pegaJwt(Mockito.any());
        verify(serviceToken, times(1)).pegaUsuario(Mockito.any());
        verify(clientUsuario, times(1)).usuarioExiste(Mockito.any(), Mockito.any());
        verify(clientCarrinho, times(1)).carrinhoAberto(Mockito.any());
        verify(clientCarrinho, times(1)).finaliza(Mockito.any());
        verify(repositoryPagamento, times(0)).save(Mockito.any());

        Assertions.assertFalse(realiza);
    }

    @Test
    public void realiza_carrinhoComProblemaNaFinalizacao2_naoSalvaNaBaseDeDados() {
        // preparação
        var clientCarrinho = Mockito.mock(CarrinhoClient.class);
        var clientUsuario = Mockito.mock(UsuarioClient.class);
        var repositoryPagamento = Mockito.mock(PagamentoRepository.class);
        var serviceToken = Mockito.mock(TokenUseCaseImpl.class);

        Mockito.when(serviceToken.pegaJwt(Mockito.any()))
                .thenReturn(
                        mock(DecodedJWT.class)
                );

        Mockito.when(serviceToken.pegaUsuario(Mockito.any()))
                .thenReturn(
                        "tokenTeste"
                );

        Mockito.when(clientUsuario.usuarioExiste(Mockito.any(), Mockito.any()))
                .thenReturn(
                        true
                );

        Mockito.when(clientCarrinho.carrinhoAberto(Mockito.any()))
                .thenReturn(
                        new CarrinhoDisponivelParaPagamentoDTO(
                                "usuario de teste",
                                new BigDecimal("100.00"),
                                List.of(
                                        new ItensDoCarrinhoDTO(
                                                123456L,
                                                new BigDecimal("100.00")
                                        )
                                )
                        )
                );

        Mockito.when(clientCarrinho.finaliza(Mockito.any()))
                .thenReturn(
                        null
                );

        Mockito.when(repositoryPagamento.save(Mockito.any()))
                .thenReturn(
                        new PagamentoEntity(
                                "usuario de teste",
                                new BigDecimal("100.00"),
                                LocalDateTime.now()
                        )
                );

        var service = new PagamentoUseCaseImpl(clientCarrinho, clientUsuario, repositoryPagamento, serviceToken);

        // execução
        final var realiza = service.realiza("tokenTeste");

        // avaliação
        verify(serviceToken, times(1)).pegaJwt(Mockito.any());
        verify(serviceToken, times(1)).pegaUsuario(Mockito.any());
        verify(clientUsuario, times(1)).usuarioExiste(Mockito.any(), Mockito.any());
        verify(clientCarrinho, times(1)).carrinhoAberto(Mockito.any());
        verify(clientCarrinho, times(1)).finaliza(Mockito.any());
        verify(repositoryPagamento, times(0)).save(Mockito.any());

        Assertions.assertFalse(realiza);
    }

    @Test
    public void realiza_erroNoToken_naoSalvaNaBaseDeDados() {
        // preparação
        var clientCarrinho = Mockito.mock(CarrinhoClient.class);
        var clientUsuario = Mockito.mock(UsuarioClient.class);
        var repositoryPagamento = Mockito.mock(PagamentoRepository.class);
        var serviceToken = Mockito.mock(TokenUseCaseImpl.class);

        Mockito.when(serviceToken.pegaJwt(Mockito.any()))
                .thenReturn(
                        null
                );

        Mockito.when(serviceToken.pegaUsuario(Mockito.any()))
                .thenReturn(
                        "tokenTeste"
                );

        Mockito.when(clientUsuario.usuarioExiste(Mockito.any(), Mockito.any()))
                .thenReturn(
                        true
                );

        Mockito.when(clientCarrinho.carrinhoAberto(Mockito.any()))
                .thenReturn(
                        new CarrinhoDisponivelParaPagamentoDTO(
                                "usuario de teste",
                                new BigDecimal("100.00"),
                                List.of(
                                        new ItensDoCarrinhoDTO(
                                                123456L,
                                                new BigDecimal("100.00")
                                        )
                                )
                        )
                );

        Mockito.when(clientCarrinho.finaliza(Mockito.any()))
                .thenReturn(
                        new ResponseEntity<>(HttpStatus.OK)
                );

        Mockito.when(repositoryPagamento.save(Mockito.any()))
                .thenReturn(
                        new PagamentoEntity(
                                "usuario de teste",
                                new BigDecimal("100.00"),
                                LocalDateTime.now()
                        )
                );

        var service = new PagamentoUseCaseImpl(clientCarrinho, clientUsuario, repositoryPagamento, serviceToken);

        // execução
        final var realiza = service.realiza("tokenTeste");

        // avaliação
        verify(serviceToken, times(1)).pegaJwt(Mockito.any());
        verify(serviceToken, times(0)).pegaUsuario(Mockito.any());
        verify(clientUsuario, times(0)).usuarioExiste(Mockito.any(), Mockito.any());
        verify(clientCarrinho, times(0)).carrinhoAberto(Mockito.any());
        verify(clientCarrinho, times(0)).finaliza(Mockito.any());
        verify(repositoryPagamento, times(0)).save(Mockito.any());

        Assertions.assertFalse(realiza);
    }

    @Test
    public void realiza_usuarioNaoExiste_naoSalvaNaBaseDeDados() {
        // preparação
        var clientCarrinho = Mockito.mock(CarrinhoClient.class);
        var clientUsuario = Mockito.mock(UsuarioClient.class);
        var repositoryPagamento = Mockito.mock(PagamentoRepository.class);
        var serviceToken = Mockito.mock(TokenUseCaseImpl.class);

        Mockito.when(serviceToken.pegaJwt(Mockito.any()))
                .thenReturn(
                        mock(DecodedJWT.class)
                );

        Mockito.when(serviceToken.pegaUsuario(Mockito.any()))
                .thenReturn(
                        "tokenTeste"
                );

        Mockito.when(clientUsuario.usuarioExiste(Mockito.any(), Mockito.any()))
                .thenReturn(
                        null
                );

        Mockito.when(clientCarrinho.carrinhoAberto(Mockito.any()))
                .thenReturn(
                        new CarrinhoDisponivelParaPagamentoDTO(
                                "usuario de teste",
                                new BigDecimal("100.00"),
                                List.of(
                                        new ItensDoCarrinhoDTO(
                                                123456L,
                                                new BigDecimal("100.00")
                                        )
                                )
                        )
                );

        Mockito.when(clientCarrinho.finaliza(Mockito.any()))
                .thenReturn(
                        new ResponseEntity<>(HttpStatus.OK)
                );

        Mockito.when(repositoryPagamento.save(Mockito.any()))
                .thenReturn(
                        new PagamentoEntity(
                                "usuario de teste",
                                new BigDecimal("100.00"),
                                LocalDateTime.now()
                        )
                );

        var service = new PagamentoUseCaseImpl(clientCarrinho, clientUsuario, repositoryPagamento, serviceToken);

        // execução
        final var realiza = service.realiza("tokenTeste");

        // avaliação
        verify(serviceToken, times(1)).pegaJwt(Mockito.any());
        verify(serviceToken, times(1)).pegaUsuario(Mockito.any());
        verify(clientUsuario, times(1)).usuarioExiste(Mockito.any(), Mockito.any());
        verify(clientCarrinho, times(0)).carrinhoAberto(Mockito.any());
        verify(clientCarrinho, times(0)).finaliza(Mockito.any());
        verify(repositoryPagamento, times(0)).save(Mockito.any());

        Assertions.assertFalse(realiza);
    }

    @Test
    public void realiza_usuarioNaoExiste2_naoSalvaNaBaseDeDados() {
        // preparação
        var clientCarrinho = Mockito.mock(CarrinhoClient.class);
        var clientUsuario = Mockito.mock(UsuarioClient.class);
        var repositoryPagamento = Mockito.mock(PagamentoRepository.class);
        var serviceToken = Mockito.mock(TokenUseCaseImpl.class);

        Mockito.when(serviceToken.pegaJwt(Mockito.any()))
                .thenReturn(
                        mock(DecodedJWT.class)
                );

        Mockito.when(serviceToken.pegaUsuario(Mockito.any()))
                .thenReturn(
                        "tokenTeste"
                );

        Mockito.when(clientUsuario.usuarioExiste(Mockito.any(), Mockito.any()))
                .thenReturn(
                        false
                );

        Mockito.when(clientCarrinho.carrinhoAberto(Mockito.any()))
                .thenReturn(
                        new CarrinhoDisponivelParaPagamentoDTO(
                                "usuario de teste",
                                new BigDecimal("100.00"),
                                List.of(
                                        new ItensDoCarrinhoDTO(
                                                123456L,
                                                new BigDecimal("100.00")
                                        )
                                )
                        )
                );

        Mockito.when(clientCarrinho.finaliza(Mockito.any()))
                .thenReturn(
                        new ResponseEntity<>(HttpStatus.OK)
                );

        Mockito.when(repositoryPagamento.save(Mockito.any()))
                .thenReturn(
                        new PagamentoEntity(
                                "usuario de teste",
                                new BigDecimal("100.00"),
                                LocalDateTime.now()
                        )
                );

        var service = new PagamentoUseCaseImpl(clientCarrinho, clientUsuario, repositoryPagamento, serviceToken);

        // execução
        final var realiza = service.realiza("tokenTeste");

        // avaliação
        verify(serviceToken, times(1)).pegaJwt(Mockito.any());
        verify(serviceToken, times(1)).pegaUsuario(Mockito.any());
        verify(clientUsuario, times(1)).usuarioExiste(Mockito.any(), Mockito.any());
        verify(clientCarrinho, times(0)).carrinhoAberto(Mockito.any());
        verify(clientCarrinho, times(0)).finaliza(Mockito.any());
        verify(repositoryPagamento, times(0)).save(Mockito.any());

        Assertions.assertFalse(realiza);
    }

    @Test
    public void realiza_usuarioNaoExiste3_naoSalvaNaBaseDeDados() {
        // preparação
        var clientCarrinho = Mockito.mock(CarrinhoClient.class);
        var clientUsuario = Mockito.mock(UsuarioClient.class);
        var repositoryPagamento = Mockito.mock(PagamentoRepository.class);
        var serviceToken = Mockito.mock(TokenUseCaseImpl.class);

        Mockito.when(serviceToken.pegaJwt(Mockito.any()))
                .thenReturn(
                        mock(DecodedJWT.class)
                );

        Mockito.when(serviceToken.pegaUsuario(Mockito.any()))
                .thenReturn(
                        "tokenTeste"
                );

        Mockito.doThrow(
                        new IllegalArgumentException("usuario nao existe!")
                )
                .when(clientUsuario)
                .usuarioExiste(
                        Mockito.any(),
                        Mockito.any()
                );

        Mockito.when(clientCarrinho.carrinhoAberto(Mockito.any()))
                .thenReturn(
                        new CarrinhoDisponivelParaPagamentoDTO(
                                "usuario de teste",
                                new BigDecimal("100.00"),
                                List.of(
                                        new ItensDoCarrinhoDTO(
                                                123456L,
                                                new BigDecimal("100.00")
                                        )
                                )
                        )
                );

        Mockito.when(clientCarrinho.finaliza(Mockito.any()))
                .thenReturn(
                        new ResponseEntity<>(HttpStatus.OK)
                );

        Mockito.when(repositoryPagamento.save(Mockito.any()))
                .thenReturn(
                        new PagamentoEntity(
                                "usuario de teste",
                                new BigDecimal("100.00"),
                                LocalDateTime.now()
                        )
                );

        var service = new PagamentoUseCaseImpl(clientCarrinho, clientUsuario, repositoryPagamento, serviceToken);

        // execução
        final var realiza = service.realiza("tokenTeste");

        // avaliação
        verify(serviceToken, times(1)).pegaJwt(Mockito.any());
        verify(serviceToken, times(1)).pegaUsuario(Mockito.any());
        verify(clientUsuario, times(1)).usuarioExiste(Mockito.any(), Mockito.any());
        verify(clientCarrinho, times(0)).carrinhoAberto(Mockito.any());
        verify(clientCarrinho, times(0)).finaliza(Mockito.any());
        verify(repositoryPagamento, times(0)).save(Mockito.any());

        Assertions.assertFalse(realiza);
    }

}
