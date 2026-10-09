package com.geracaolivre.ecommerce;

import com.fasterxml.jackson.databind.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.http.MediaType;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest @AutoConfigureMockMvc @ActiveProfiles("dev")
class LojaIntegrationTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;
    @Autowired JdbcTemplate jdbc;
    private String register() throws Exception {
        String email = "cliente-" + java.util.UUID.randomUUID() + "@teste.local";
        String body = "{\"nome\":\"Cliente\",\"email\":\"" + email + "\",\"senha\":\"Teste123!\"}";
        return mapper.readTree(mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString()).get("token").asText();
    }
    private String admin() throws Exception {
        return mapper.readTree(mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"admin@demo.local\",\"senha\":\"DemoAdmin123!\"}"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString()).get("token").asText();
    }
    @Test void catalogoPublicoComPaginacaoEFiltragem() throws Exception {
        mvc.perform(get("/api/produtos/paginado").param("tamanho", "2").param("largura", "38"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.itens.length()").value(2))
                .andExpect(jsonPath("$.total").value(3)).andExpect(jsonPath("$.itens[0].material").value("GORGURAO"));
        mvc.perform(get("/api/produtos/paginado").param("busca", "inexistente"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.total").value(0));
    }
    @Test void metragemDecimalSubtotalEMinimo() throws Exception {
        String token = register();
        mvc.perform(post("/api/carrinho/adicionar").header("Authorization", "Bearer " + token).contentType(MediaType.APPLICATION_JSON)
                .content("{\"produtoId\":1,\"quantidade\":0.70}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.itens[0].quantidade").value(0.7)).andExpect(jsonPath("$.total").value(2.73));
        mvc.perform(post("/api/carrinho/adicionar").header("Authorization", "Bearer " + token).contentType(MediaType.APPLICATION_JSON)
                .content("{\"produtoId\":1,\"quantidade\":0.55}"))
                .andExpect(status().isBadRequest());
        mvc.perform(post("/api/carrinho/adicionar").header("Authorization", "Bearer " + token).contentType(MediaType.APPLICATION_JSON)
                .content("{\"produtoId\":1,\"quantidade\":99}"))
                .andExpect(status().isConflict());
    }
    @Test void mesclagemRepetidaNaoDuplicaItens() throws Exception {
        String token = register();
        String body = "{\"chave\":\"cart-teste-12345\",\"itens\":[{\"produtoId\":1,\"quantidade\":0.50}]}";
        for (int i = 0; i < 2; i++) mvc.perform(post("/api/carrinho/mesclar").header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON).content(body)).andExpect(status().isOk())
                .andExpect(jsonPath("$.itens.length()").value(1)).andExpect(jsonPath("$.itens[0].quantidade").value(0.5));
    }
    @Test void mesclagemFalhaSemSalvarParcialmente() throws Exception {
        String token = register();
        mvc.perform(post("/api/carrinho/mesclar").header("Authorization", "Bearer " + token).contentType(MediaType.APPLICATION_JSON)
                .content("{\"chave\":\"cart-falha-12345\",\"itens\":[{\"produtoId\":1,\"quantidade\":0.50},{\"produtoId\":6,\"quantidade\":0.50}]}"))
                .andExpect(status().isConflict());
        mvc.perform(get("/api/carrinho").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk()).andExpect(jsonPath("$.itens.length()").value(0));
    }
    @Test void acessoAdministrativoProtegido() throws Exception {
        mvc.perform(get("/api/admin/produtos")).andExpect(status().isUnauthorized());
        mvc.perform(get("/api/admin/produtos").header("Authorization", "Bearer " + register())).andExpect(status().isForbidden());
        mvc.perform(get("/api/admin/produtos").header("Authorization", "Bearer " + admin())).andExpect(status().isOk());
    }
    @Test void itensDeOutraContaNaoPodemSerAlterados() throws Exception {
        String a = register(), b = register();
        JsonNode cart = mapper.readTree(mvc.perform(post("/api/carrinho/adicionar").header("Authorization", "Bearer " + a)
                .contentType(MediaType.APPLICATION_JSON).content("{\"produtoId\":1,\"quantidade\":0.50}"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString());
        long id = cart.get("itens").get(0).get("id").asLong();
        mvc.perform(delete("/api/carrinho/item/" + id).header("Authorization", "Bearer " + b)).andExpect(status().isForbidden());
    }
    @Test void produtoLegadoNaoApareceNemPodeSerComprado() throws Exception {
        jdbc.update("INSERT INTO produtos(nome,preco,estoque,categoria_id,ativo) VALUES ('Legado',1,10,1,true)");
        Long id = jdbc.queryForObject("SELECT id FROM produtos WHERE nome = 'Legado'", Long.class);
        mvc.perform(get("/api/produtos/" + id)).andExpect(status().isNotFound());
        mvc.perform(get("/api/produtos/99999999")).andExpect(status().isNotFound());
        mvc.perform(delete("/api/admin/produtos/" + id).header("Authorization", "Bearer " + admin())).andExpect(status().isNoContent());
    }
    @Test void administradorPodeCadastrarEEditarSomenteGorgurao() throws Exception {
        String token = admin();
        String body = "{\"nome\":\"Fita teste\",\"sku\":\"GL-TESTE\",\"cor\":\"Amarelo\",\"larguraMm\":22,\"preco\":2.90,\"estoque\":10.50,\"quantidadeMinima\":0.50,\"incremento\":0.10,\"categoriaId\":1,\"material\":\"GORGURAO\"}";
        JsonNode produto = mapper.readTree(mvc.perform(post("/api/admin/produtos").header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON).content(body)).andExpect(status().isCreated()).andReturn().getResponse().getContentAsString());
        mvc.perform(put("/api/admin/produtos/" + produto.get("id").asLong()).header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON).content(body.replace("2.90", "3.90"))).andExpect(status().isOk());
        mvc.perform(post("/api/admin/produtos").header("Authorization", "Bearer " + token).contentType(MediaType.APPLICATION_JSON)
                .content(body.replace("GORGURAO", "CETIM"))).andExpect(status().isBadRequest());
    }
    @Test void checkoutNaoConfirmaPagamentoSimulado() throws Exception {
        mvc.perform(post("/api/pedidos/checkout").header("Authorization", "Bearer " + register()).contentType(MediaType.APPLICATION_JSON)
                .content("{\"cep\":\"86360000\",\"rua\":\"Rua\",\"numero\":\"1\",\"bairro\":\"Centro\",\"cidade\":\"Bandeirantes\",\"estado\":\"PR\",\"formaPagamento\":\"CARTAO_CREDITO\"}"))
                .andExpect(status().isServiceUnavailable());
    }
}
