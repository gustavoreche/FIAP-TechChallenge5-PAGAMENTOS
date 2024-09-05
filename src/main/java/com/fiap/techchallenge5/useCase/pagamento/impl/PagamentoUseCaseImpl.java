package com.fiap.techchallenge5.useCase.pagamento.impl;

import com.fiap.techchallenge5.domain.Pagamento;
import com.fiap.techchallenge5.infrastructure.carrinho.client.CarrinhoClient;
import com.fiap.techchallenge5.infrastructure.pagamento.model.PagamentoEntity;
import com.fiap.techchallenge5.infrastructure.pagamento.repository.PagamentoRepository;
import com.fiap.techchallenge5.useCase.pagamento.PagamentoUseCase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;


@Service
@Slf4j
public class PagamentoUseCaseImpl implements PagamentoUseCase {

    private final CarrinhoClient clientCarrinho;
    private final PagamentoRepository repositoryPagamento;

    public PagamentoUseCaseImpl(final CarrinhoClient clientCarrinho,
                               final PagamentoRepository repositoryPagamento) {
        this.clientCarrinho = clientCarrinho;
        this.repositoryPagamento = repositoryPagamento;
    }

    @Override
    public boolean realiza(final String token) {
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
                || finalizaCarrinho.status() != HttpStatus.OK.value()) {
            log.error("Carrinho não finalizado o pagamento");
            return false;
        }

        final var pagamentoEntity = new PagamentoEntity(
                pagamento.usuario(),
                pagamento.valorTotal(),
                LocalDateTime.now()
        );
        repositoryPagamento.save(pagamentoEntity);
        return true;
    }
}
