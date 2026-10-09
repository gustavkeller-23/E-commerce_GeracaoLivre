import { useCallback, useEffect, useState } from 'react';
import { api } from '../api';
import { meters } from '../cart';
const blankProduct = { nome: '', descricao: '', sku: '', cor: '', estampa: '', larguraMm: 38, preco: '', precoPromocional: '', estoque: '', quantidadeMinima: '', incremento: '', imagemUrl: '', categoriaId: '', material: 'GORGURAO', ativo: true };
export default function Admin({ auth, action, disabled, updated }) {
  const [products, setProducts] = useState([]);
  const [categories, setCategories] = useState([]);
  const [form, setForm] = useState(blankProduct);
  const [error, setError] = useState('');
  const [saved, setSaved] = useState('');
  const load = useCallback(() => Promise.all([api('/admin/produtos', { token: auth.token }), api('/categorias')]).then(([items, cats]) => { setProducts(items); setCategories(cats); }), [auth.token]);
  useEffect(() => { load().catch(e => setError(e.message)); }, [load]);
  const save = event => {
    event.preventDefault();
    action(async () => {
      await api(`/admin/produtos${form.id ? `/${form.id}` : ''}`, { token: auth.token, method: form.id ? 'PUT' : 'POST', body: JSON.stringify({ ...form, material: 'GORGURAO', precoPromocional: form.precoPromocional || null }) });
      setForm(blankProduct); await load(); updated(); setSaved('Fita salva com sucesso.');
    });
  };
  return <section className="admin-page wrap"><span className="eyebrow">PAINEL DA LOJA</span><h1>Gerenciar fitas de gorgurão</h1>{error && <p role="alert">{error}</p>}{saved && <p role="status">{saved}</p>}<div className="admin-grid"><form className="admin-form" onSubmit={save}><h2>{form.id ? 'Editar fita' : 'Cadastrar fita'}</h2>{[['nome', 'Nome'], ['sku', 'Referência (SKU)'], ['cor', 'Cor'], ['estampa', 'Estampa (opcional)'], ['larguraMm', 'Largura (mm)'], ['preco', 'Preço por metro (R$)'], ['precoPromocional', 'Preço promocional (opcional)'], ['estoque', 'Estoque (m)'], ['quantidadeMinima', 'Corte mínimo (m)'], ['incremento', 'Incremento (m)'], ['imagemUrl', 'URL da foto (opcional)']].map(([key, label]) => { const numeric = ['larguraMm', 'preco', 'precoPromocional', 'estoque', 'quantidadeMinima', 'incremento'].includes(key); return <label key={key}>{label}<input required={!['estampa', 'imagemUrl', 'precoPromocional'].includes(key)} type={numeric ? 'number' : key === 'imagemUrl' ? 'url' : 'text'} min={key === 'estoque' ? 0 : key === 'larguraMm' ? 1 : numeric ? 0.01 : undefined} step={key === 'larguraMm' ? 1 : numeric ? 0.01 : undefined} value={form[key] ?? ''} onChange={event => setForm({ ...form, [key]: event.target.value })} /></label>; })}<label>Categoria<select required value={form.categoriaId} onChange={event => setForm({ ...form, categoriaId: event.target.value })}><option value="">Selecione</option>{categories.map(item => <option key={item.id} value={item.id}>{item.nome}</option>)}</select></label><label>Descrição<textarea value={form.descricao || ''} onChange={event => setForm({ ...form, descricao: event.target.value })} /></label><label className="checkbox"><input type="checkbox" checked={form.ativo} onChange={event => setForm({ ...form, ativo: event.target.checked })} />Exibir no catálogo</label><button className="button" disabled={disabled}>Salvar fita</button>{form.id && <button type="button" onClick={() => setForm(blankProduct)}>Cancelar edição</button>}</form><div className="admin-list"><h2>Catálogo cadastrado</h2>{products.map(product => <article key={product.id}><h3>{product.nome}</h3><p>{product.sku || 'Cadastro antigo: revisar características'} · {meters(product.estoque)} m · {product.ativo ? 'Ativa' : 'Inativa'}</p><button disabled={disabled} onClick={() => setForm({ ...blankProduct, ...product, material: 'GORGURAO' })}>Editar</button><button disabled={disabled} onClick={() => { if (window.confirm(`Retirar ${product.nome} do catálogo? O histórico será preservado.`)) action(async () => { await api(`/admin/produtos/${product.id}`, { token: auth.token, method: 'DELETE' }); await load(); updated(); setSaved('Fita retirada do catálogo.'); }); }}>Retirar do catálogo</button></article>)}</div></div></section>;
}
