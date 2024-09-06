package com.fiap.techchallenge5.bdd;

import com.fiap.techchallenge5.integrados.JwtUtil;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static com.fiap.techchallenge5.infrastructure.pagamento.controller.PagamentoController.URL_PAGAMENTO;
import static io.restassured.RestAssured.given;


public class RealizaPagamentoSteps {

    private Response response;
    private Long ean;
    private String token;
    private ClientAndServer mockServerCarrinho;
    private ClientAndServer mockServerUsuario;

    @Dado("que tenho um carrinho pronto para ser finalizado")
    public void queTenhoUmCarrinhoProntoParaSerFinalizado() {
        this.token = JwtUtil.geraJwt();
        this.ean = System.currentTimeMillis();

        this.mockServerCarrinho = this.criaMockServerCarrinho(this.token, "nada1", "nada2");
        this.mockServerUsuario = this.criaMockServerUsuario();
    }

    @Dado("que tenho um carrinho já finalizado ou um carrinho que não existe")
    public void queTenhoUmCarrinhoJaFinalizadoOuUmCarrinhoQueNaoExiste() {
        this.token = JwtUtil.geraJwt("USER", "loginCarrinhoJaFinalizado");
        this.ean = System.currentTimeMillis();

        this.mockServerCarrinho = this.criaMockServerCarrinho("nada3", this.token, "nada4");
        this.mockServerUsuario = this.criaMockServerUsuario();
    }

    @Dado("que tenho um carrinho com problema na finalização")
    public void queTenhoUmCarrinhoComProblemaNaFinalizacao() {
        this.token = JwtUtil.geraJwt("USER", "loginCarrinhoComProblemaNaFinalizacao");
        this.ean = System.currentTimeMillis();

        this.mockServerCarrinho = this.criaMockServerCarrinho("nada5", "nada6", this.token);
        this.mockServerUsuario = this.criaMockServerUsuario();
    }

    @Dado("que tenho um carrinho para ser finalizado com um usuário que não existe no sistema")
    public void queTenhoUmCarrinhoParaSerFinalizadoComUmUsuarioQueNaoExisteNoSistema() {
        this.token = JwtUtil.geraJwt("USER", "novoLogin");
        this.ean = System.currentTimeMillis();

        this.mockServerCarrinho = this.criaMockServerCarrinho(this.token, "nada7", "nada8");
        this.mockServerUsuario = this.criaMockServerUsuario();
    }

    @Quando("realizo o pagamento")
    public void realizoOPagamento() {
        RestAssured.baseURI = "http://localhost:8083";
        this.response = given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", "Bearer " + token)
                .when()
                .post(URL_PAGAMENTO);
    }

    @Entao("recebo uma resposta que o pagamento foi realizado com sucesso")
    public void receboUmaRespostaQueOPagamentoFoiRealizadoComSucesso() {
        this.response
                .prettyPeek()
                .then()
                .statusCode(HttpStatus.CREATED.value())
        ;

        this.mockServerCarrinho.stop();
        this.mockServerUsuario.stop();

    }

    @Entao("recebo uma resposta que o pagamento não foi realizado")
    public void receboUmaRespostaQueOPagamentoNaoFoiRealizado() {
        this.response
                .prettyPeek()
                .then()
                .statusCode(HttpStatus.CONFLICT.value())
        ;

        this.mockServerCarrinho.stop();
        this.mockServerUsuario.stop();
    }

    private ClientAndServer criaMockServerCarrinho(final String tokenSucesso,
                                                   final String tokenFalha,
                                                   final String tokenFalhaNoFinal) {
        final var clientAndServer = ClientAndServer.startClientAndServer(8082);

        clientAndServer.when(
                        HttpRequest.request()
                                .withMethod("GET")
                                .withPath("/carrinho/disponivel-para-pagamento")
                                .withHeader("Authorization", "Bearer " + tokenSucesso)
                )
                .respond(
                        HttpResponse.response()
                                .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
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
                                .withHeader("Authorization", "Bearer " + tokenSucesso)
                )
                .respond(
                        HttpResponse.response()
                                .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                                .withStatusCode(200)
                );

        clientAndServer.when(
                        HttpRequest.request()
                                .withMethod("GET")
                                .withPath("/carrinho/disponivel-para-pagamento")
                                .withHeader("Authorization", "Bearer " + tokenFalha)
                )
                .respond(
                        HttpResponse.response()
                                .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                                .withStatusCode(204)
                );

        clientAndServer.when(
                        HttpRequest.request()
                                .withMethod("GET")
                                .withPath("/carrinho/disponivel-para-pagamento")
                                .withHeader("Authorization", "Bearer " + tokenFalhaNoFinal)
                )
                .respond(
                        HttpResponse.response()
                                .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
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
                                .withHeader("Authorization", "Bearer " + tokenFalhaNoFinal)
                )
                .respond(
                        HttpResponse.response()
                                .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                                .withStatusCode(204)
                );

        return clientAndServer;
    }

    private ClientAndServer criaMockServerUsuario() {
        final var clientAndServer = ClientAndServer.startClientAndServer(8080);

        clientAndServer.when(
                        HttpRequest.request()
                                .withMethod("GET")
                                .withPath("/usuario/teste")
                                .withHeader("Authorization", "Bearer " + this.token)
                )
                .respond(
                        HttpResponse.response()
                                .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                                .withStatusCode(200)
                                .withBody(String.valueOf(true))
                );

        clientAndServer.when(
                        HttpRequest.request()
                                .withMethod("GET")
                                .withPath("/usuario/loginCarrinhoJaFinalizado")
                                .withHeader("Authorization", "Bearer " + this.token)
                )
                .respond(
                        HttpResponse.response()
                                .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                                .withStatusCode(200)
                                .withBody(String.valueOf(true))
                );

        clientAndServer.when(
                        HttpRequest.request()
                                .withMethod("GET")
                                .withPath("/usuario/loginCarrinhoComProblemaNaFinalizacao")
                                .withHeader("Authorization", "Bearer " + this.token)
                )
                .respond(
                        HttpResponse.response()
                                .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                                .withStatusCode(200)
                                .withBody(String.valueOf(true))
                );

        clientAndServer.when(
                        HttpRequest.request()
                                .withMethod("GET")
                                .withPath("/usuario/novoLogin")
                                .withHeader("Authorization", "Bearer " + this.token)
                )
                .respond(
                        HttpResponse.response()
                                .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                                .withStatusCode(204)
                );

        return clientAndServer;
    }

}
