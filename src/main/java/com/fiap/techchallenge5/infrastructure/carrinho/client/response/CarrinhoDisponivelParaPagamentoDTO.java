package com.fiap.techchallenge5.infrastructure.carrinho.client.response;

import java.math.BigDecimal;

public record CarrinhoDisponivelParaPagamentoDTO(

		String usuario,
		BigDecimal valorTotal
) {}
