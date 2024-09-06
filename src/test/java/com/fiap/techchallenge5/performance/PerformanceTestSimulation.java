package com.fiap.techchallenge5.performance;

import com.fiap.techchallenge5.integrados.JwtUtil;
import io.gatling.javaapi.core.ActionBuilder;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;
import lombok.extern.slf4j.Slf4j;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.model.MediaType;

import java.time.Duration;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;


@Slf4j
public class PerformanceTestSimulation extends Simulation {

    private final String token = JwtUtil.geraJwt();
    private final String tokenTeste1 = "Bearer " + this.token;
    private final ClientAndServer mockServerCarrinho = this.criaMockServerCarrinho(this.tokenTeste1);
    private final ClientAndServer mockServerUsuario = this.criaMockServerUsuario(this.tokenTeste1);
    private final HttpProtocolBuilder httpProtocol = http
            .baseUrl("http://localhost:8083");

    ActionBuilder realizaPagamentoRequest = http("realiza pagamento")
            .post("/pagamento")
            .header("Content-Type", "application/json")
            .header("Authorization", this.tokenTeste1)
            .check(status().is(201));

    ScenarioBuilder cenarioRealizaPagamento = scenario("Realiza pagamento")
            .exec(realizaPagamentoRequest);

    {

        setUp(
                cenarioRealizaPagamento.injectOpen(
                        rampUsersPerSec(1)
                                .to(10)
                                .during(Duration.ofSeconds(10)),
                        constantUsersPerSec(10)
                                .during(Duration.ofSeconds(20)),
                        rampUsersPerSec(10)
                                .to(1)
                                .during(Duration.ofSeconds(10)))
        )
                .protocols(httpProtocol)
                .assertions(
                        global().responseTime().max().lt(600),
                        global().failedRequests().count().is(0L));

    }

    private ClientAndServer criaMockServerCarrinho(final String token1) {
        final var clientAndServer = ClientAndServer.startClientAndServer(8082);

        clientAndServer.when(
               HttpRequest.request()
                       .withMethod("GET")
                       .withPath("/carrinho/disponivel-para-pagamento")
                       .withHeader("Authorization", token1)
                )
                .respond(
                        HttpResponse.response()
                                .withContentType(MediaType.APPLICATION_JSON)
                                .withStatusCode(200)
                                .withBody("""
                                        {
                                            "usuario": "teste",
                                            "valorTotal": 100.00,
                                            "itens": [
                                                {
                                                    "ean": 12345,
                                                    "valorTotal": 100.00
                                                }
                                            ]
                                        }
                                        """)
                );

        clientAndServer.when(
                        HttpRequest.request()
                                .withMethod("PUT")
                                .withPath("/carrinho/finaliza")
                                .withHeader("Authorization", token1)
                )
                .respond(
                        HttpResponse.response()
                                .withContentType(MediaType.APPLICATION_JSON)
                                .withStatusCode(200)
                );
        
        return clientAndServer;
    }

    private ClientAndServer criaMockServerUsuario(final String token1) {
        final var clientAndServer = ClientAndServer.startClientAndServer(8080);

        clientAndServer.when(
                        HttpRequest.request()
                                .withMethod("GET")
                                .withPath("/usuario/teste")
                                .withHeader("Authorization", token1)
                )
                .respond(
                        HttpResponse.response()
                                .withContentType(MediaType.APPLICATION_JSON)
                                .withStatusCode(200)
                                .withBody(String.valueOf(true))
                );

        return clientAndServer;
    }

}