package com.geracaolivre.ecommerce.config;

import com.geracaolivre.ecommerce.model.*;
import com.geracaolivre.ecommerce.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;

@Component
@org.springframework.core.annotation.Order(0)
@Profile("dev")
public class DemoData implements CommandLineRunner {
    private final CategoriaRepository categorias;
    private final ProdutoRepository produtos;
    private final UsuarioRepository usuarios;
    private final PasswordEncoder encoder;
    public DemoData(CategoriaRepository categorias, ProdutoRepository produtos, UsuarioRepository usuarios, PasswordEncoder encoder) {
        this.categorias = categorias; this.produtos = produtos; this.usuarios = usuarios; this.encoder = encoder;
    }
    @Override @Transactional
    public void run(String... args) {
        if (produtos.count() != 0) return;
        var lisas = categorias.findByNomeIgnoreCase("Lisas").orElseThrow();
        var estampadas = categorias.findByNomeIgnoreCase("Estampadas").orElseThrow();
        criar("Gorgurão Rosa Antigo", "GL-ROSA-38", "Rosa antigo", "#c68f9b", 38, "3.90", "60.00", lisas, null);
        criar("Gorgurão Verde Sálvia", "GL-SALVIA-22", "Verde sálvia", "#90a68c", 22, "2.90", "45.00", lisas, null);
        criar("Gorgurão Azul Sereno", "GL-AZUL-38", "Azul", "#93afc7", 38, "3.90", "32.50", lisas, null);
        criar("Gorgurão Creme", "GL-CREME-10", "Creme", "#decfb0", 10, "1.90", "75.00", lisas, null);
        criar("Gorgurão Poá Terracota", "GL-POA-38", "Terracota", "#bc7964", 38, "5.90", "25.00", estampadas, "Poá");
        criar("Gorgurão Lavanda", "GL-LAVANDA-22", "Lavanda", "#b8a3cb", 22, "2.90", "0.00", lisas, null);
        usuarios.save(new Usuario("Admin de demonstração", "admin@demo.local", encoder.encode("DemoAdmin123!"), "", Role.ROLE_ADMIN));
    }
    private void criar(String nome, String sku, String cor, String hex, int largura, String preco, String estoque, Categoria categoria, String estampa) {
        var produto = new Produto(nome, "Fita de gorgurão com textura canelada. Ideal para laços, acabamentos e projetos criativos. Venda por metro.", new BigDecimal(preco), null, new BigDecimal(estoque), "", categoria, true);
        produto.setSku(sku); produto.setCor(cor); produto.setLarguraMm(largura); produto.setEstampa(estampa);
        // Parâmetros ilustrativos, ainda sujeitos à definição comercial da loja.
        produto.setQuantidadeMinima(new BigDecimal("0.50")); produto.setIncremento(new BigDecimal("0.10"));
        produtos.save(produto);
    }
}
