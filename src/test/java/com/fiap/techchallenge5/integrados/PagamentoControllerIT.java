package com.fiap.techchallenge5.integrados;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.techchallenge5.infrastructure.carrinho.client.CarrinhoClient;
import com.fiap.techchallenge5.infrastructure.carrinho.client.response.CarrinhoDisponivelParaPagamentoDTO;
import com.fiap.techchallenge5.infrastructure.carrinho.client.response.ItensDoCarrinhoDTO;
import com.fiap.techchallenge5.infrastructure.usuario.client.UsuarioClient;
import com.fiap.techchallenge5.infrastructure.pagamento.repository.PagamentoRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Stream;

import static com.fiap.techchallenge5.infrastructure.pagamento.controller.PagamentoController.URL_PAGAMENTO;


@AutoConfigureMockMvc
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PagamentoControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    @MockBean
    CarrinhoClient clientCarrinho;

    @Autowired
    @MockBean
    UsuarioClient clientUsuario;

    @Autowired
    PagamentoRepository repositoryPagamento;

    @Autowired
    private ObjectMapper objectMapper;

    private final String token = JwtUtil.geraJwt();

    @BeforeEach
    void inicializaLimpezaDoDatabase() {
        this.repositoryPagamento.deleteAll();
    }

    @AfterAll
    void finalizaLimpezaDoDatabase() {
        this.repositoryPagamento.deleteAll();
    }

    @Test
    public void realiza_deveRetornar201_salvaNaBaseDeDados() throws Exception {
        Mockito.when(this.clientUsuario.usuarioExiste("teste", "Bearer " + this.token))
                .thenReturn(
                        true
                        );

        Mockito.when(this.clientCarrinho.carrinhoAberto("Bearer " + this.token))
                .thenReturn(
                        new CarrinhoDisponivelParaPagamentoDTO(
                                "teste",
                                new BigDecimal("100.00"),
                                List.of(
                                        new ItensDoCarrinhoDTO(
                                                123456L,
                                                new BigDecimal("100.00")
                                        )
                                )
                        )
                );

        Mockito.when(this.clientCarrinho.finaliza("Bearer " + this.token))
                .thenReturn(
                        new ResponseEntity<>(HttpStatus.OK)
                );

        this.mockMvc
                .perform(MockMvcRequestBuilders.post(URL_PAGAMENTO)
                        .header("Authorization", "Bearer " + this.token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers
                        .status()
                        .isCreated()
                );

        var pagamento = this.repositoryPagamento.findAll().get(0);

        Assertions.assertEquals("teste", pagamento.getUsuario());
        Assertions.assertEquals(new BigDecimal("100.00"), pagamento.getValorTotal());
        Assertions.assertNotNull(pagamento.getDataDeCriacao());
    }

    @Test
    public void realiza_deveRetornar409_carrinhoFinalizadoOuCarrinhoNaoExiste_naoSalvaNaBaseDeDados() throws Exception {
        Mockito.when(this.clientUsuario.usuarioExiste("teste", "Bearer " + this.token))
                .thenReturn(
                        true
                );

        Mockito.when(this.clientCarrinho.carrinhoAberto("Bearer " + this.token))
                .thenReturn(
                        null
                );

        Mockito.when(this.clientCarrinho.finaliza("Bearer " + this.token))
                .thenReturn(
                        new ResponseEntity<>(HttpStatus.OK)
                );

        this.mockMvc
                .perform(MockMvcRequestBuilders.post(URL_PAGAMENTO)
                        .header("Authorization", "Bearer " + this.token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers
                        .status()
                        .isConflict()
                );

        Assertions.assertEquals(0, this.repositoryPagamento.findAll().size());
    }

    @Test
    public void realiza_deveRetornar409_carrinhoComProblemaNaFinalizacao_naoSalvaNaBaseDeDados() throws Exception {
        Mockito.when(this.clientUsuario.usuarioExiste("teste", "Bearer " + this.token))
                .thenReturn(
                        true
                );

        Mockito.when(this.clientCarrinho.carrinhoAberto("Bearer " + this.token))
                .thenReturn(
                        new CarrinhoDisponivelParaPagamentoDTO(
                                "teste",
                                new BigDecimal("100.00"),
                                List.of(
                                        new ItensDoCarrinhoDTO(
                                                123456L,
                                                new BigDecimal("100.00")
                                        )
                                )
                        )
                );

        Mockito.when(this.clientCarrinho.finaliza("Bearer " + this.token))
                .thenReturn(
                        new ResponseEntity<>(HttpStatus.NO_CONTENT)
                );

        this.mockMvc
                .perform(MockMvcRequestBuilders.post(URL_PAGAMENTO)
                        .header("Authorization", "Bearer " + this.token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers
                        .status()
                        .isConflict()
                );

        Assertions.assertEquals(0, this.repositoryPagamento.findAll().size());
    }

    @Test
    public void realiza_deveRetornar409_carrinhoComProblemaNaFinalizacao2_naoSalvaNaBaseDeDados() throws Exception {
        Mockito.when(this.clientUsuario.usuarioExiste("teste", "Bearer " + this.token))
                .thenReturn(
                        true
                );

        Mockito.when(this.clientCarrinho.carrinhoAberto("Bearer " + this.token))
                .thenReturn(
                        new CarrinhoDisponivelParaPagamentoDTO(
                                "teste",
                                new BigDecimal("100.00"),
                                List.of(
                                        new ItensDoCarrinhoDTO(
                                                123456L,
                                                new BigDecimal("100.00")
                                        )
                                )
                        )
                );

        Mockito.when(this.clientCarrinho.finaliza("Bearer " + this.token))
                .thenReturn(
                        null
                );

        this.mockMvc
                .perform(MockMvcRequestBuilders.post(URL_PAGAMENTO)
                        .header("Authorization", "Bearer " + this.token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers
                        .status()
                        .isConflict()
                );

        Assertions.assertEquals(0, this.repositoryPagamento.findAll().size());
    }

    @Test
    public void insere_deveRetornar409_usuarioNaoExiste_naoSalvaNaBaseDeDados() throws Exception {
        Mockito.when(this.clientUsuario.usuarioExiste("teste", "Bearer " + this.token))
                .thenReturn(
                        false
                );

        Mockito.when(this.clientCarrinho.carrinhoAberto("Bearer " + this.token))
                .thenReturn(
                        new CarrinhoDisponivelParaPagamentoDTO(
                                "teste",
                                new BigDecimal("100.00"),
                                List.of(
                                        new ItensDoCarrinhoDTO(
                                                123456L,
                                                new BigDecimal("100.00")
                                        )
                                )
                        )
                );

        Mockito.when(this.clientCarrinho.finaliza("Bearer " + this.token))
                .thenReturn(
                        new ResponseEntity<>(HttpStatus.OK)
                );

        this.mockMvc
                .perform(MockMvcRequestBuilders.post(URL_PAGAMENTO)
                        .header("Authorization", "Bearer " + this.token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers
                        .status()
                        .isConflict()
                );

        Assertions.assertEquals(0, this.repositoryPagamento.findAll().size());
    }

    @Test
    public void insere_deveRetornar401_semToken() throws Exception {
        this.mockMvc
                .perform(MockMvcRequestBuilders.post(URL_PAGAMENTO)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers
                        .status()
                        .isUnauthorized()
                );

        Assertions.assertEquals(0, this.repositoryPagamento.findAll().size());
    }

    @Test
    public void insere_deveRetornar401_tokenInvalido() throws Exception {
        this.mockMvc
                .perform(MockMvcRequestBuilders.post(URL_PAGAMENTO)
                        .header("Authorization", "Bearer TESTE")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers
                        .status()
                        .isUnauthorized()
                );
        
        Assertions.assertEquals(0, this.repositoryPagamento.findAll().size());
    }

    @Test
    public void insere_deveRetornar401_tokenExpirado() throws Exception {
        this.mockMvc
                .perform(MockMvcRequestBuilders.post(URL_PAGAMENTO)
                        .header("Authorization", "Bearer " + JwtUtil.geraJwt(LocalDateTime.now()
                                .minusHours(3)
                                .toInstant(ZoneOffset.of("-03:00"))))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers
                        .status()
                        .isUnauthorized()
                );

        Assertions.assertEquals(0, this.repositoryPagamento.findAll().size());
    }

    @Test
    public void realiza_deveRetornar201_comRoleUSER_salvaNaBaseDeDados() throws Exception {
        final var tokenUser = JwtUtil.geraJwt("USER", "teste");
        
        Mockito.when(this.clientUsuario.usuarioExiste("teste", "Bearer " + tokenUser))
                .thenReturn(
                        true
                );

        Mockito.when(this.clientCarrinho.carrinhoAberto("Bearer " + tokenUser))
                .thenReturn(
                        new CarrinhoDisponivelParaPagamentoDTO(
                                "teste",
                                new BigDecimal("100.00"),
                                List.of(
                                        new ItensDoCarrinhoDTO(
                                                123456L,
                                                new BigDecimal("100.00")
                                        )
                                )
                        )
                );

        Mockito.when(this.clientCarrinho.finaliza("Bearer " + tokenUser))
                .thenReturn(
                        new ResponseEntity<>(HttpStatus.OK)
                );

        this.mockMvc
                .perform(MockMvcRequestBuilders.post(URL_PAGAMENTO)
                        .header("Authorization", "Bearer " + tokenUser)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers
                        .status()
                        .isCreated()
                );

        var pagamento = this.repositoryPagamento.findAll().get(0);

        Assertions.assertEquals("teste", pagamento.getUsuario());
        Assertions.assertEquals(new BigDecimal("100.00"), pagamento.getValorTotal());
        Assertions.assertNotNull(pagamento.getDataDeCriacao());
    }

    @ParameterizedTest
    @MethodSource("requestValidandoCampos")
    public void realiza_deveRetornar409_dadosVindoErrados_naoSalvaNaBaseDeDados(String usuario,
                                                                                BigDecimal valor) throws Exception {
        Mockito.when(this.clientUsuario.usuarioExiste("teste", "Bearer " + this.token))
                .thenReturn(
                        true
                );

        Mockito.when(this.clientCarrinho.carrinhoAberto("Bearer " + this.token))
                .thenReturn(
                        new CarrinhoDisponivelParaPagamentoDTO(
                                usuario,
                                valor,
                                List.of(
                                        new ItensDoCarrinhoDTO(
                                                123456L,
                                                new BigDecimal("100.00")
                                        )
                                )
                        )
                );

        Mockito.when(this.clientCarrinho.finaliza("Bearer " + this.token))
                .thenReturn(
                        new ResponseEntity<>(HttpStatus.OK)
                );

        this.mockMvc
                .perform(MockMvcRequestBuilders.post(URL_PAGAMENTO)
                        .header("Authorization", "Bearer " + this.token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers
                        .status()
                        .isBadRequest()
                );

        Assertions.assertEquals(0, this.repositoryPagamento.findAll().size());
    }

    private static Stream<Arguments> requestValidandoCampos() {
        return Stream.of(
                Arguments.of(null, new BigDecimal("100.00")),
                Arguments.of("", new BigDecimal("100.00")),
                Arguments.of("ab", new BigDecimal("100.00")),
                Arguments.of("aaaaaaaaaabbbbbbbbbbccccccccccddddddddddeeeeeeeeeef", new BigDecimal("100.00")),
                Arguments.of("teste", null),
                Arguments.of("teste", BigDecimal.ZERO),
                Arguments.of("teste", new BigDecimal("-500.00"))
        );
    }

}
