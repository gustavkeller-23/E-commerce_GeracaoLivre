import { useEffect, useState } from 'react';
import { api } from '../api';
import { money, meters, price, validQuantity, subtotal } from '../cart';
import RibbonArt from '../components/RibbonArt';
import Quantity from '../components/Quantity';
export default function Product({ id, revision, add, disabled }) {
  const [product, setProduct] = useState(null);
  const [quantity, setQuantity] = useState('');
  const [error, setError] = useState('');
  const [retry, setRetry] = useState(0);
  useEffect(() => {
    let cancelled = false; setError('');
    api(`/produtos/${id}`).then(data => { if (!cancelled) { setProduct(data); setQuantity(value => value || String(data.quantidadeMinima)); } }).catch(e => { if (!cancelled) { setProduct(null); setError(e.message); } });
    return () => { cancelled = true; };
  }, [id, revision, retry]);
  if (error) return <div className="empty wrap" role="alert"><p>{error}</p><button onClick={() => setRetry(value => value + 1)}>Tentar novamente</button><a href="#loja">Voltar à loja</a></div>;
  if (!product) return <p className="empty wrap">Carregando fita…</p>;
  return <section className="detail wrap"><a className="back-link" href="#loja">← Voltar à coleção</a><div className="detail-grid"><div className="detail-art"><RibbonArt product={product} /><small>Imagem ilustrativa quando não houver foto do produto.</small></div><div><span className="eyebrow">{product.material === 'CETIM' ? 'CETIM' : 'GORGURÃO'} · {product.larguraMm} MM</span><h1>{product.nome}</h1><p className="muted">{product.descricao}</p><p className="detail-price">{money(price(product))}<small> / metro</small></p><dl className="specs"><div><dt>Cor</dt><dd>{product.cor}</dd></div><div><dt>Estampa</dt><dd>{product.estampa || 'Lisa'}</dd></div><div><dt>Disponível</dt><dd>{meters(product.estoque)} m</dd></div><div><dt>Referência</dt><dd>{product.sku}</dd></div></dl><Quantity product={product} value={quantity} onChange={setQuantity} disabled={disabled} /><button className="button wide" disabled={disabled || !validQuantity(product, quantity)} onClick={() => add(product, quantity)}>{Number(product.estoque) < Number(product.quantidadeMinima) ? 'Fita indisponível' : `Adicionar à sacola · ${money(subtotal(product, quantity || 0))}`}</button></div></div></section>;
}
