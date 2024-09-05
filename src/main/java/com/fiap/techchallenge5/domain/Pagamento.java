package com.fiap.techchallenge5.domain;

import java.math.BigDecimal;
import java.util.Objects;


public record Pagamento(
    String usuario,
    BigDecimal valorTotal
) {

        public Pagamento {
            if (Objects.isNull(usuario) || usuario.isEmpty()) {
                throw new IllegalArgumentException("USUARIO NAO PODE SER NULO OU VAZIO!");
            }
            if (usuario.length() < 3 || usuario.length() > 50) {
                throw new IllegalArgumentException("O USUARIO deve ter no mínimo 3 letras e no máximo 50 letras");
            }

            if (Objects.isNull(valorTotal) || valorTotal.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("VALOR TOTAL NAO PODE SER NULO OU MENOR E IGUAL A ZERO!");
            }
        }
}
