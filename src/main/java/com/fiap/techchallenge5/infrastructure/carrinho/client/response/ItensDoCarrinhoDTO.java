package com.fiap.techchallenge5.infrastructure.carrinho.client.response;

import java.math.BigDecimal;

public record ItensDoCarrinhoDTO(

		Long ean,
		BigDecimal valorTotal
) {}
