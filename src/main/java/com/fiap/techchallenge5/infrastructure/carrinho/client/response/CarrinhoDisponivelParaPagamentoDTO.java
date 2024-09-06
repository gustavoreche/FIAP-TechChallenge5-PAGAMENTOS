package com.fiap.techchallenge5.infrastructure.carrinho.client.response;

import java.math.BigDecimal;
import java.util.List;

public record CarrinhoDisponivelParaPagamentoDTO(

		String usuario,
		BigDecimal valorTotal,
		List<ItensDoCarrinhoDTO> itens
) {}
