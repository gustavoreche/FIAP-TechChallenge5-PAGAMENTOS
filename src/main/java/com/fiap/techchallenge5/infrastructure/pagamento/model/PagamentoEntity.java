package com.fiap.techchallenge5.infrastructure.pagamento.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_pagamento")
@Getter
@NoArgsConstructor
public class PagamentoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String usuario;
    private BigDecimal valorTotal;
    private LocalDateTime dataDeCriacao;

    public PagamentoEntity(final String usuario,
                           final BigDecimal valorTotal,
                           final LocalDateTime dataDeCriacao) {
        this.usuario = usuario;
        this.valorTotal = valorTotal;
        this.dataDeCriacao = dataDeCriacao;
    }
}
