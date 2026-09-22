package com.geracaolivre.ecommerce.service;

import com.geracaolivre.ecommerce.dto.CheckoutRequest;
import com.geracaolivre.ecommerce.model.*;
import com.geracaolivre.ecommerce.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final CarrinhoRepository carrinhoRepository;
    private final ProdutoRepository produtoRepository;
    private final PagamentoRepository pagamentoRepository;

    public PedidoService(PedidoRepository pedidoRepository,
                         CarrinhoRepository carrinhoRepository,
                         ProdutoRepository produtoRepository,
                         PagamentoRepository pagamentoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.carrinhoRepository = carrinhoRepository;
        this.produtoRepository = produtoRepository;
        this.pagamentoRepository = pagamentoRepository;
    }

    @Transactional
    public Pedido criarPedido(Usuario usuario, CheckoutRequest request) {
        Carrinho carrinho = carrinhoRepository.findByUsuarioId(usuario.getId())
                .orElseThrow(() -> new IllegalStateException("Carrinho não encontrado para este usuário."));

        if (carrinho.getItens() == null || carrinho.getItens().isEmpty()) {
            throw new IllegalStateException("Seu carrinho está vazio. Adicione produtos antes de finalizar o pedido.");
        }

        Pedido pedido = new Pedido();
        pedido.setUsuario(usuario);
        pedido.setDataPedido(LocalDateTime.now());
        pedido.setStatus(request.getFormaPagamento() == FormaPagamento.CARTAO_CREDITO ? StatusPedido.PAGO : StatusPedido.PENDENTE);

        // Gera número de pedido (NP)
        String np = "NP-" + System.currentTimeMillis() % 1000000 + "-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        pedido.setNumeroPedido(np);

        // Dados de entrega
        pedido.setCepEntrega(request.getCep());
        pedido.setEnderecoEntrega(request.getRua());
        pedido.setNumeroEntrega(request.getNumero());
        pedido.setComplementoEntrega(request.getComplemento());
        pedido.setBairroEntrega(request.getBairro());
        pedido.setCidadeEntrega(request.getCidade());
        pedido.setEstadoEntrega(request.getEstado());

        // Cálculo de Subtotal e Validação de Estoque
        BigDecimal subtotal = BigDecimal.ZERO;

        for (ItemCarrinho item : carrinho.getItens()) {
            Produto produto = item.getProduto();
            if (produto.getEstoque() < item.getQuantidade()) {
                throw new IllegalArgumentException("Estoque insuficiente para o produto: " + produto.getNome());
            }

            // Baixa no estoque
            produto.setEstoque(produto.getEstoque() - item.getQuantidade());
            produtoRepository.save(produto);

            BigDecimal precoUnitario = produto.getPrecoPromocional() != null ? produto.getPrecoPromocional() : produto.getPreco();
            ItemPedido itemPedido = new ItemPedido(pedido, produto, item.getQuantidade(), precoUnitario);
            pedido.getItens().add(itemPedido);

            subtotal = subtotal.add(itemPedido.getSubtotal());
        }

        pedido.setValorSubtotal(subtotal);

        // Regra de Frete: Grátis acima de R$ 150,00 ou R$ 14,90 fixo
        BigDecimal frete = subtotal.compareTo(new BigDecimal("150.00")) >= 0 ? BigDecimal.ZERO : new BigDecimal("14.90");

        // Regra de Cupom de Desconto
        BigDecimal desconto = BigDecimal.ZERO;
        if (request.getCupom() != null && !request.getCupom().trim().isEmpty()) {
            String cupom = request.getCupom().trim().toUpperCase();
            if ("BEMVINDO10".equals(cupom)) {
                desconto = subtotal.multiply(new BigDecimal("0.10")).setScale(2, RoundingMode.HALF_UP);
                pedido.setCupomAplicado("BEMVINDO10 (10% OFF)");
            } else if ("GERACAO5".equals(cupom)) {
                desconto = subtotal.multiply(new BigDecimal("0.05")).setScale(2, RoundingMode.HALF_UP);
                pedido.setCupomAplicado("GERACAO5 (5% OFF)");
            } else if ("FRETEGRATIS".equals(cupom)) {
                frete = BigDecimal.ZERO;
                pedido.setCupomAplicado("FRETEGRATIS");
            }
        }

        pedido.setValorFrete(frete);
        pedido.setValorDesconto(desconto);

        BigDecimal total = subtotal.add(frete).subtract(desconto);
        if (total.compareTo(BigDecimal.ZERO) < 0) total = BigDecimal.ZERO;
        pedido.setValorTotal(total);

        // Salva pedido e itens
        Pedido pedidoSalvo = pedidoRepository.save(pedido);

        // Criação do Registro de Pagamento
        Pagamento pagamento = new Pagamento(pedidoSalvo, request.getFormaPagamento(), total);

        if (request.getFormaPagamento() == FormaPagamento.PIX) {
            pagamento.setCodigoPix("00020126580014BR.GOV.BCB.PIX0136geracaolivre.bandeirantes@pix.com.br52040000530398654" +
                    total.toString() + "5802BR5920GERACAO LIVRE TECIDOS6012BANDEIRANTES62070503***6304" + np.replace("-", ""));
        } else if (request.getFormaPagamento() == FormaPagamento.BOLETO) {
            pagamento.setCodigoBarrasBoleto("34191.79001 01043.510047 91020.150008 1 965400000" + total.intValue());
        } else if (request.getFormaPagamento() == FormaPagamento.CARTAO_CREDITO) {
            String num = request.getNumeroCartao();
            pagamento.setCartaoUltimosDigitos(num != null && num.length() >= 4 ? num.substring(num.length() - 4) : "1234");
            pagamento.setParcelas(request.getParcelas() != null ? request.getParcelas() : 1);
        }

        pagamentoRepository.save(pagamento);
        pedidoSalvo.setPagamento(pagamento);

        // Limpa o carrinho após finalizar o pedido com sucesso
        carrinho.getItens().clear();
        carrinhoRepository.save(carrinho);

        return pedidoSalvo;
    }

    public List<Pedido> listarPedidosDoUsuario(Usuario usuario) {
        return pedidoRepository.findByUsuarioIdOrderByDataPedidoDesc(usuario.getId());
    }

    public Pedido buscarPorIdEUsuario(Long pedidoId, Usuario usuario) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado"));

        if (!pedido.getUsuario().getId().equals(usuario.getId()) && usuario.getRole() != Role.ROLE_ADMIN) {
            throw new IllegalStateException("Acesso não autorizado a este pedido.");
        }
        return pedido;
    }

    public List<Pedido> listarTodosPedidos() {
        return pedidoRepository.findAllByOrderByDataPedidoDesc();
    }

    @Transactional
    public Pedido atualizarStatus(Long pedidoId, StatusPedido novoStatus) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado"));

        pedido.setStatus(novoStatus);

        if (novoStatus == StatusPedido.PAGO && pedido.getPagamento() != null) {
            pedido.getPagamento().setStatus(StatusPagamento.CONFIRMADO);
            pedido.getPagamento().setDataPagamento(LocalDateTime.now());
        }

        return pedidoRepository.save(pedido);
    }
}
