import { useEffect, useState } from 'react';
import { api } from '../api';
import { money, price } from '../cart';
import RibbonArt from '../components/RibbonArt';

export default function Catalog({ revision, add, disabled }) {
  const [busca, setBusca] = useState('');
  const [categoria, setCategoria] = useState('');
  const [largura, setLargura] = useState('');
  const [page, setPage] = useState(0);
  const [categories, setCategories] = useState([]);
  const [result, setResult] = useState(null);
  const [error, setError] = useState('');
  const [retry, setRetry] = useState(0);
  useEffect(() => { api('/categorias').then(setCategories).catch(() => {}); }, []);
  useEffect(() => {
    let cancelled = false; setError(''); setResult(null);
    const timeout = setTimeout(() => {
      const params = new URLSearchParams({ pagina: page, tamanho: 12, busca });
      if (categoria) params.set('categoriaId', categoria);
      if (largura) params.set('largura', largura);
      api(`/produtos/paginado?${params}`).then(data => { if (!cancelled) setResult(data); }).catch(e => { if (!cancelled) setError(e.message); });
    }, 200);
    return () => { cancelled = true; clearTimeout(timeout); };
  }, [busca, categoria, largura, page, revision, retry]);
  return <>
    <section className="hero wrap"><div className="hero-copy"><span className="eyebrow">UM METRO DE INSPIRAÇÃO</span><h1>Suas ideias merecem<br /><em>um belo laço.</em></h1><p>Encontre a cor, a textura e o detalhe certo.<br />Fitas de cetim e gorgurão, na medida da sua criatividade.</p><a href="#colecao" className="button" onClick={event => { event.preventDefault(); document.getElementById('colecao')?.scrollIntoView({ behavior: 'smooth' }); }}>Explore as fitas <span>↗</span></a><div className="hero-caption">Para laços, presentes e tudo que você imaginar.</div></div><div className="hero-art"><div className="hero-circle" /><RibbonArt decorative product={{ cor: 'turquesa' }} /><div className="hero-tag"><span>Textura que encanta</span><strong>fitas & laços</strong></div></div></section>
    <div className="benefits wrap"><span>↔ &nbsp; Vendidas por metro</span><span>✧ &nbsp; Cores para cada ideia</span><span>♡ &nbsp; Feito para quem cria</span></div>
    <section className="collection wrap" id="colecao"><div className="section-heading"><div><span className="eyebrow">ESCOLHA SEU PRÓXIMO DETALHE</span><h2>Nossa coleção de fitas</h2></div><span className="muted">{result ? `${result.total} fitas encontradas` : 'Consultando a coleção…'}</span></div>
      <div className="filters"><label className="search"><span className="sr-only">Buscar fitas</span><input placeholder="Busque por nome ou cor…" value={busca} onChange={event => { setBusca(event.target.value); setPage(0); }} /></label><label><span className="sr-only">Categoria</span><select value={categoria} onChange={event => { setCategoria(event.target.value); setPage(0); }}><option value="">Todas as fitas</option>{categories.map(item => <option value={item.id} key={item.id}>{item.nome}</option>)}</select></label><label><span className="sr-only">Largura</span><select value={largura} onChange={event => { setLargura(event.target.value); setPage(0); }}><option value="">Todas as larguras</option>{[10, 15, 22, 38, 50].map(value => <option value={value} key={value}>{value} mm</option>)}</select></label></div>
      {error ? <div className="empty" role="alert"><p>{error}</p><button onClick={() => setRetry(value => value + 1)}>Tentar novamente</button></div> : !result ? <p className="empty" role="status">Carregando fitas…</p> : result.itens.length === 0 ? <div className="empty"><h3>Nenhuma fita encontrada</h3><p>Tente outra cor ou ajuste os filtros.</p></div> : <div className="product-grid">{result.itens.map(product => <article className="product-card" key={product.id}><a className="product-art" href={`#produto/${product.id}`}><RibbonArt product={product} /><span className="width-tag">{product.larguraMm} mm</span>{Number(product.estoque) < Number(product.quantidadeMinima) && <span className="sold-out">Esgotada</span>}</a><div className="card-description"><span className="eyebrow">{product.categoriaNome}</span><h3><a href={`#produto/${product.id}`}>{product.nome}</a></h3><div className="card-bottom"><div>{product.precoPromocional && <del>{money(product.preco)}</del>}<strong>{money(price(product))}<small> / metro</small></strong></div><button className="add-button" aria-label={`Adicionar ${product.nome} à sacola`} disabled={disabled || Number(product.estoque) < Number(product.quantidadeMinima)} onClick={() => add(product, String(product.quantidadeMinima))}>+</button></div></div></article>)}</div>}
      {result?.totalPaginas > 1 && <div className="pagination"><button disabled={page === 0} onClick={() => setPage(value => value - 1)}>Anterior</button><span>Página {page + 1} de {result.totalPaginas}</span><button disabled={page + 1 >= result.totalPaginas} onClick={() => setPage(value => value + 1)}>Próxima</button></div>}
    </section><section className="creative-note wrap"><span>✧</span><div><h2>O detalhe faz toda a diferença.</h2><p>Combine cores, experimente texturas e dê seu toque a cada criação.</p></div></section>
  </>;
}
