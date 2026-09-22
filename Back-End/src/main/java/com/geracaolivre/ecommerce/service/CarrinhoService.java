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

@Service
public class CarrinhoService {

    private final CarrinhoRepository carrinhoRepository;
    private final ItemCarrinhoRepository itemCarrinhoRepository;
    private final ProdutoRepository produtoRepository;

    public CarrinhoService(CarrinhoRepository carrinhoRepository,
                           ItemCarrinhoRepository itemCarrinhoRepository,
                           ProdutoRepository produtoRepository) {
        this.carrinhoRepository = carrinhoRepository;
        this.itemCarrinhoRepository = itemCarrinhoRepository;
        this.produtoRepository = produtoRepository;
    }

    @Transactional
    public Carrinho obterOuCriarCarrinho(Usuario usuario) {
        return carrinhoRepository.findByUsuarioId(usuario.getId())
                .orElseGet(() -> carrinhoRepository.save(new Carrinho(usuario)));
    }

    @Transactional
    public Carrinho adicionarItem(Usuario usuario, Long produtoId, Integer quantidade) {
        if (quantidade <= 0) {
            throw new IllegalArgumentException("A quantidade deve ser maior que zero.");
        }

        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado."));

        if (!produto.isDisponivel()) {
            throw new IllegalStateException("Produto indisponível no momento.");
        }

        Carrinho carrinho = obterOuCriarCarrinho(usuario);

        Optional<ItemCarrinho> itemExistente = itemCarrinhoRepository.findByCarrinhoIdAndProdutoId(carrinho.getId(), produtoId);

        if (itemExistente.isPresent()) {
            ItemCarrinho item = itemExistente.get();
            int novaQuantidade = item.getQuantidade() + quantidade;
            if (novaQuantidade > produto.getEstoque()) {
                throw new IllegalArgumentException("Estoque insuficiente. Quantidade disponível: " + produto.getEstoque());
            }
            item.setQuantidade(novaQuantidade);
            itemCarrinhoRepository.save(item);
        } else {
            if (quantidade > produto.getEstoque()) {
                throw new IllegalArgumentException("Estoque insuficiente. Quantidade disponível: " + produto.getEstoque());
            }
            ItemCarrinho novoItem = new ItemCarrinho(carrinho, produto, quantidade);
            carrinho.getItens().add(novoItem);
            itemCarrinhoRepository.save(novoItem);
        }

        return carrinhoRepository.save(carrinho);
    }

    @Transactional
    public Carrinho atualizarQuantidade(Usuario usuario, Long itemId, Integer quantidade) {
        Carrinho carrinho = obterOuCriarCarrinho(usuario);

        ItemCarrinho item = itemCarrinhoRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item do carrinho não encontrado."));

        if (!item.getCarrinho().getId().equals(carrinho.getId())) {
            throw new IllegalStateException("O item não pertence ao seu carrinho.");
        }

        if (quantidade <= 0) {
            carrinho.getItens().remove(item);
            itemCarrinhoRepository.delete(item);
        } else {
            if (quantidade > item.getProduto().getEstoque()) {
                throw new IllegalArgumentException("Estoque insuficiente. Quantidade máxima: " + item.getProduto().getEstoque());
            }
            item.setQuantidade(quantidade);
            itemCarrinhoRepository.save(item);
        }

        return carrinhoRepository.save(carrinho);
    }

    @Transactional
    public Carrinho removerItem(Usuario usuario, Long itemId) {
        Carrinho carrinho = obterOuCriarCarrinho(usuario);

        ItemCarrinho item = itemCarrinhoRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item do carrinho não encontrado."));

        if (!item.getCarrinho().getId().equals(carrinho.getId())) {
            throw new IllegalStateException("O item não pertence ao seu carrinho.");
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
}
