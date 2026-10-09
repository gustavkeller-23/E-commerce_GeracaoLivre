import { useEffect, useState } from 'react';
import { centesimos, subtotal, money, price, validQuantity } from '../cart';
import RibbonArt from '../components/RibbonArt';
import Quantity from '../components/Quantity';
export default function Cart({ itens, auth, change, remove, disabled }) {
  const totalCents = itens.reduce((total, item) => total + centesimos(subtotal(item.produto, item.quantidade)), 0);
  return <section className="cart-page wrap"><span className="eyebrow">SUAS PRÓXIMAS CRIAÇÕES</span><h1>Minha sacola</h1>{itens.length === 0 ? <div className="empty"><p>Sua sacola está vazia.</p><a className="button" href="#loja">Encontrar uma fita</a></div> : <div className="cart-layout"><div>{itens.map(item => <CartItem key={item.produtoId} item={item} change={change} remove={remove} disabled={disabled} />)}</div><aside className="cart-summary"><h2>Resumo da sacola</h2><div><span>Produtos</span><strong>{money(totalCents / 100)}</strong></div><p className="muted">Frete e prazo serão informados na finalização.</p>{auth ? <><button className="button wide" disabled>Finalização em preparação</button><p>O pagamento ainda não está disponível. Sua sacola está salva na conta.</p></> : <><a href="#entrar" className="button wide">Entrar e salvar minha sacola</a><p>Crie uma conta ou entre para continuar. Os itens serão reunidos na sua sacola.</p></>}<a href="#loja">← Continuar comprando</a></aside></div>}</section>;
}
function CartItem({ item, change, remove, disabled }) {
  const [value, setValue] = useState(String(item.quantidade));
  useEffect(() => { setValue(String(item.quantidade)); }, [item.quantidade]);
  const product = item.produto;
  return <article className="cart-item"><div className="cart-art"><RibbonArt product={product} /></div><div><h3><a href={`#produto/${item.produtoId}`}>{product.nome}</a></h3><p className="muted">{product.larguraMm} mm · {money(price(product))} / metro</p>{!validQuantity(product, item.quantidade) && <p role="alert">Confira a metragem: esta fita mudou ou está indisponível.</p>}<Quantity product={product} value={value} onChange={setValue} id={`quantity-${item.produtoId}`} disabled={disabled} /><div className="item-actions"><button disabled={disabled || !validQuantity(product, value)} onClick={() => change(item, value)}>Atualizar metragem</button><button className="text-button" disabled={disabled} onClick={() => remove(item)}>Remover</button></div></div><strong>{money(subtotal(product, item.quantidade))}</strong></article>;
}
