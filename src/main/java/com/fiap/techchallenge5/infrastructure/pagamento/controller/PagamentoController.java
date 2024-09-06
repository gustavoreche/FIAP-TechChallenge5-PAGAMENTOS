package com.fiap.techchallenge5.infrastructure.pagamento.controller;

import com.fiap.techchallenge5.useCase.pagamento.PagamentoUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.fiap.techchallenge5.infrastructure.pagamento.controller.PagamentoController.URL_PAGAMENTO;

@Tag(
		name = "Pagamento",
		description = "Serviço para realizar o pagamento do carrinho de compras"
)
@RestController
@RequestMapping(URL_PAGAMENTO)
public class PagamentoController {

	public static final String URL_PAGAMENTO = "/pagamento";

	private final PagamentoUseCase service;

	public PagamentoController(final PagamentoUseCase service) {
		this.service = service;
	}

	@Operation(
			summary = "Serviço para realizar o pagamento do carrinho vinculado ao usuário"
	)
	@PostMapping
	public ResponseEntity<Void> realiza(@RequestHeader("Authorization") final String token) {
		final var realizou = this.service.realiza(token);
		if(realizou) {
			return ResponseEntity
					.status(HttpStatus.CREATED)
					.build();
		}
		return ResponseEntity
				.status(HttpStatus.CONFLICT)
				.build();
	}

}
