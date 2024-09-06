package com.fiap.techchallenge5.useCase.pagamento.impl;

import com.fiap.techchallenge5.domain.Pagamento;
import com.fiap.techchallenge5.infrastructure.carrinho.client.CarrinhoClient;
import com.fiap.techchallenge5.infrastructure.usuario.client.UsuarioClient;
import com.fiap.techchallenge5.infrastructure.pagamento.model.PagamentoEntity;
import com.fiap.techchallenge5.infrastructure.pagamento.repository.PagamentoRepository;
import com.fiap.techchallenge5.useCase.pagamento.PagamentoUseCase;
import com.fiap.techchallenge5.useCase.token.TokenUseCase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;


@Service
@Slf4j
public class PagamentoUseCaseImpl implements PagamentoUseCase {

    private final CarrinhoClient clientCarrinho;
    private final UsuarioClient clientUsuario;
    private final PagamentoRepository repositoryPagamento;
    private final TokenUseCase serviceToken;

    public PagamentoUseCaseImpl(final CarrinhoClient clientCarrinho,
                                final UsuarioClient clientUsuario,
                                final PagamentoRepository repositoryPagamento,
                                final TokenUseCase serviceToken) {
        this.clientCarrinho = clientCarrinho;
        this.clientUsuario = clientUsuario;
        this.repositoryPagamento = repositoryPagamento;
        this.serviceToken = serviceToken;
    }

    @Override
    public boolean realiza(final String token) {
        final var jwt = this.serviceToken.pegaJwt(token.replace("Bearer ", ""));
        if(Objects.isNull(jwt)){
            log.error("Token inválido");
            return false;
        }

        final var usuario = this.serviceToken.pegaUsuario(jwt);
        try {
            final var usuarioExiste = this.clientUsuario.usuarioExiste(usuario, token);
            if(Objects.isNull(usuarioExiste) || !usuarioExiste) {
                log.error("Usuario não encontrado");
                return false;
            }
        } catch (Exception e) {
            log.error("Erro ao buscar usuário ", e);
            return false;
        }

        final var carrinho = this.clientCarrinho.carrinhoAberto(token);
        if(Objects.isNull(carrinho)) {
            log.error("Carrinho não encontrado");
            return false;
        }

        final var pagamento = new Pagamento(
                carrinho.usuario(),
                carrinho.valorTotal()
        );

        final var finalizaCarrinho = this.clientCarrinho.finaliza(token);
        if(Objects.isNull(finalizaCarrinho)
                || finalizaCarrinho.getStatusCode() != HttpStatus.OK) {
            log.error("Carrinho não finalizado o pagamento");
            return false;
        }

        final var pagamentoEntity = new PagamentoEntity(
                pagamento.usuario(),
                pagamento.valorTotal(),
                LocalDateTime.now()
        );
        this.repositoryPagamento.save(pagamentoEntity);
        return true;
    }
}
