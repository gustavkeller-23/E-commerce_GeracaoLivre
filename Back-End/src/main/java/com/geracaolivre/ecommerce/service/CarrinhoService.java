package com.geracaolivre.ecommerce.service;

import com.geracaolivre.ecommerce.model.Carrinho;
import com.geracaolivre.ecommerce.model.ItemCarrinho;
import com.geracaolivre.ecommerce.model.Produto;
import com.geracaolivre.ecommerce.model.Usuario;
import com.geracaolivre.ecommerce.repository.CarrinhoRepository;
import com.geracaolivre.ecommerce.repository.ItemCarrinhoRepository;
import com.geracaolivre.ecommerce.repository.ProdutoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.math.BigDecimal;

@Service
public class CarrinhoService {

    private final CarrinhoRepository carrinhoRepository;
    private final ItemCarrinhoRepository itemCarrinhoRepository;
    private final ProdutoRepository produtoRepository;
    private final org.springframework.jdbc.core.JdbcTemplate jdbc;

    public CarrinhoService(CarrinhoRepository carrinhoRepository,
                           ItemCarrinhoRepository itemCarrinhoRepository,
                           ProdutoRepository produtoRepository, org.springframework.jdbc.core.JdbcTemplate jdbc) {
        this.carrinhoRepository = carrinhoRepository;
        this.itemCarrinhoRepository = itemCarrinhoRepository;
        this.produtoRepository = produtoRepository;
        this.jdbc = jdbc;
    }

    @Transactional
    public Carrinho obterOuCriarCarrinho(Usuario usuario) {
        jdbc.queryForList("SELECT id FROM usuarios WHERE id = ? FOR UPDATE", usuario.getId());
        return carrinhoRepository.findByUsuarioId(usuario.getId())
                .orElseGet(() -> carrinhoRepository.save(new Carrinho(usuario)));
    }

    @Transactional
    public Carrinho adicionarItem(Usuario usuario, Long produtoId, BigDecimal quantidade) {
        if (quantidade == null || quantidade.signum() <= 0) {
            throw new IllegalArgumentException("A quantidade deve ser maior que zero.");
        }

        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado."));

        if (!produto.isDisponivel()) {
            throw new IllegalStateException("Produto indisponível no momento.");
        }

        produto.validarQuantidade(quantidade);
        Carrinho carrinho = obterOuCriarCarrinho(usuario);

        Optional<ItemCarrinho> itemExistente = itemCarrinhoRepository.findByCarrinhoIdAndProdutoId(carrinho.getId(), produtoId);

        if (itemExistente.isPresent()) {
            ItemCarrinho item = itemExistente.get();
            BigDecimal novaQuantidade = item.getQuantidade().add(quantidade);
            produto.validarQuantidade(novaQuantidade);
            if (novaQuantidade.compareTo(produto.getEstoque()) > 0) {
                throw new IllegalArgumentException("Estoque insuficiente. Quantidade disponível: " + produto.getEstoque());
            }
            item.setQuantidade(novaQuantidade);
            itemCarrinhoRepository.save(item);
        } else {
            if (quantidade.compareTo(produto.getEstoque()) > 0) {
                throw new IllegalArgumentException("Estoque insuficiente. Quantidade disponível: " + produto.getEstoque());
            }
            ItemCarrinho novoItem = new ItemCarrinho(carrinho, produto, quantidade);
            carrinho.getItens().add(novoItem);
            itemCarrinhoRepository.save(novoItem);
        }

        return carrinhoRepository.save(carrinho);
    }

    @Transactional
    public Carrinho atualizarQuantidade(Usuario usuario, Long itemId, BigDecimal quantidade) {
        Carrinho carrinho = obterOuCriarCarrinho(usuario);

        ItemCarrinho item = itemCarrinhoRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item do carrinho não encontrado."));

        if (!item.getCarrinho().getId().equals(carrinho.getId())) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.FORBIDDEN, "O item não pertence ao seu carrinho.");
        }

        item.getProduto().validarQuantidade(quantidade);
        item.setQuantidade(quantidade);
        itemCarrinhoRepository.save(item);

        return carrinhoRepository.save(carrinho);
    }

    @Transactional
    public Carrinho removerItem(Usuario usuario, Long itemId) {
        Carrinho carrinho = obterOuCriarCarrinho(usuario);

        ItemCarrinho item = itemCarrinhoRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item do carrinho não encontrado."));

        if (!item.getCarrinho().getId().equals(carrinho.getId())) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.FORBIDDEN, "O item não pertence ao seu carrinho.");
        }

        carrinho.getItens().remove(item);
        itemCarrinhoRepository.delete(item);
        return carrinhoRepository.save(carrinho);
    }

    @Transactional
    public void limparCarrinho(Usuario usuario) {
        Carrinho carrinho = obterOuCriarCarrinho(usuario);
        carrinho.getItens().clear();
        carrinhoRepository.save(carrinho);
    }

    @Transactional
    public Carrinho mesclar(Usuario usuario, com.geracaolivre.ecommerce.dto.MesclarCarrinhoRequest request) {
        Carrinho carrinho = obterOuCriarCarrinho(usuario);
        Integer processado = jdbc.queryForObject("SELECT COUNT(*) FROM carrinho_mesclagens WHERE usuario_id = ? AND chave = ?", Integer.class, usuario.getId(), request.chave());
        if (processado != null && processado > 0) return carrinho;
        for (var item : request.itens()) adicionarItem(usuario, item.getProdutoId(), item.getQuantidade());
        jdbc.update("INSERT INTO carrinho_mesclagens(usuario_id, chave) VALUES (?, ?)", usuario.getId(), request.chave());
        return carrinho;
    }
}
