package com.fiap.techchallenge5.infrastructure.carrinho.client;

import com.fiap.techchallenge5.infrastructure.carrinho.client.response.CarrinhoDisponivelParaPagamentoDTO;
import feign.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "carrinho", url = "http://172.17.0.1:8082/carrinho")
public interface CarrinhoClient {

    @GetMapping(value = "/disponivel-para-pagamento")
    CarrinhoDisponivelParaPagamentoDTO carrinhoAberto(@RequestHeader("Authorization") final String token);

    @PutMapping(value = "/finaliza")
    Response finaliza(@RequestHeader("Authorization") final String token);
}
