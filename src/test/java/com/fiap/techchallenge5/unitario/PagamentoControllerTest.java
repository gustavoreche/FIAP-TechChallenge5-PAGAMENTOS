package com.fiap.techchallenge5.unitario;

import com.fiap.techchallenge5.infrastructure.pagamento.controller.PagamentoController;
import com.fiap.techchallenge5.useCase.pagamento.impl.PagamentoUseCaseImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;

import static org.mockito.ArgumentMatchers.any;

public class PagamentoControllerTest {

    @Test
    public void realiza_deveRetornar201_salvaNaBaseDeDados() {
        // preparação
        var service = Mockito.mock(PagamentoUseCaseImpl.class);
        Mockito.when(service.realiza(
                            any(String.class)
                        )
                )
                .thenReturn(
                        true
                );

        var controller = new PagamentoController(service);

        // execução
        var carrinho = controller.realiza(
                "tokenTeste"
        );

        // avaliação
        Assertions.assertEquals(HttpStatus.CREATED, carrinho.getStatusCode());
    }

    @Test
    public void realiza_deveRetornar409_naoSalvaNaBaseDeDados() {
        // preparação
        var service = Mockito.mock(PagamentoUseCaseImpl.class);
        Mockito.when(service.realiza(
                            any(String.class)
                        )
                )
                .thenReturn(
                        false
                );

        var controller = new PagamentoController(service);

        // execução
        var carrinho = controller.realiza(
                "tokenTeste"
        );

        // avaliação
        Assertions.assertEquals(HttpStatus.CONFLICT, carrinho.getStatusCode());
    }

}
