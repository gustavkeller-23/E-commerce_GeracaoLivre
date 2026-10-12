import { useEffect, useState } from 'react';
import { api, catalogEventsUrl } from './api';
import { centesimos, decimal, loadGuest, saveGuest, newKey, validQuantity, money } from './cart';
import Catalog from './pages/Catalog';
import Product from './pages/Product';
import Cart from './pages/Cart';
import Auth from './pages/Auth';
import Admin from './pages/Admin';
import './App.css';

function routeFromHash() {
  const parts = window.location.hash.slice(1).split('/').filter(Boolean);
  return { page: parts[0] || 'loja', id: parts[1] };
}
function navigate(page) { window.location.hash = page; }
const emptyCart = { itens: [], total: 0 };

export default function App() {
  const [route, setRoute] = useState(routeFromHash);
  const [guest, setGuest] = useState(loadGuest);
  const [auth, setAuth] = useState(null);
  const [cart, setCart] = useState(emptyCart);
  const [notice, setNotice] = useState('');
  const [busy, setBusy] = useState(false);
  const [config, setConfig] = useState(null);
  const [revision, setRevision] = useState(0);
  const [restoring, setRestoring] = useState(true);
  useEffect(() => {
    const change = () => { setRoute(routeFromHash()); window.scrollTo?.(0, 0); };
    window.addEventListener('hashchange', change);
    return () => window.removeEventListener('hashchange', change);
  }, []);
  useEffect(() => {
    api('/produtos/configuracao').then(setConfig).catch(() => {});
    let alive = true;
    let saved;
    try { saved = JSON.parse(sessionStorage.getItem('gl-session')); } catch (error) { saved = null; }
    if (!saved?.token) { setRestoring(false); return; }
    api('/auth/me', { token: saved.token }).then(async user => {
      const response = await api('/carrinho', { token: saved.token });
      if (alive) { setAuth({ ...user, token: saved.token }); setCart(response); }
    }).catch(error => {
      if (error.status === 401) sessionStorage.removeItem('gl-session');
      if (alive) setNotice('Não foi possível restaurar sua sessão. Entre novamente para acessar seu carrinho salvo.');
    }).finally(() => { if (alive) setRestoring(false); });
    return () => { alive = false; };
  }, []);
  useEffect(() => {
    if (!window.EventSource) return;
    const events = new EventSource(catalogEventsUrl);
    events.addEventListener('catalogo', () => setRevision(value => value + 1));
    return () => events.close();
  }, []);
  useEffect(() => {
    if (revision === 0) return;
    let cancelled = false;
    if (auth) api('/carrinho', { token: auth.token }).then(data => { if (!cancelled) setCart(data); }).catch(error => setNotice(error.message));
    else {
      Promise.all(guest.itens.map(async item => {
        try { return { ...item, produto: await api(`/produtos/${item.produtoId}`) }; }
        catch (error) { if (error.status === 404) return { ...item, produto: { ...item.produto, ativo: false, estoque: 0 } }; throw error; }
      })).then(itens => { if (!cancelled) setGuest(current => ({ ...current, itens })); }).catch(error => setNotice(error.message));
    }
    return () => { cancelled = true; };
    // Local changes are validated in cart actions; this effect reconciles catalog events.
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [revision, auth]);
  useEffect(() => { if (!saveGuest(guest) && guest.itens.length) setNotice('Seu navegador não permitiu salvar a sacola. Mantenha esta página aberta.'); }, [guest]);

  const action = async task => {
    if (busy || restoring) return;
    setBusy(true);
    try { await task(); }
    catch (error) {
      setNotice(error.campos ? `${error.message} ${Object.values(error.campos).join(' ')}` : error.message);
      if (error.status === 401) { setAuth(null); setCart(emptyCart); sessionStorage.removeItem('gl-session'); navigate('entrar'); }
    } finally { setBusy(false); }
  };
  const add = (product, quantity) => action(async () => {
    const fresh = await api(`/produtos/${product.id}`);
    if (auth) setCart(await api('/carrinho/adicionar', { token: auth.token, method: 'POST', body: JSON.stringify({ produtoId: product.id, quantidade: quantity }) }));
    else {
      const existing = guest.itens.find(item => item.produtoId === product.id);
      const totalQuantity = decimal(centesimos(existing?.quantidade || 0) + centesimos(quantity));
      if (!validQuantity(fresh, quantity) || !validQuantity(fresh, totalQuantity)) throw new Error('Confira o corte mínimo, incremento e estoque disponível para esta metragem.');
      const itens = guest.itens.filter(item => item.produtoId !== product.id);
      itens.push({ produtoId: product.id, quantidade: totalQuantity, produto: fresh });
      setGuest({ chave: newKey(), itens });
    }
    setNotice(`Fita adicionada à sua sacola. Preço atual: ${money(fresh.precoPromocional ?? fresh.preco)} por metro.`);
  });
  const changeQuantity = (item, quantity) => action(async () => {
    if (auth) setCart(await api(`/carrinho/item/${item.id}`, { token: auth.token, method: 'PUT', body: JSON.stringify({ quantidade: quantity }) }));
    else {
      const product = await api(`/produtos/${item.produtoId}`);
      if (!validQuantity(product, quantity)) throw new Error('Metragem inválida para o corte mínimo, incremento ou estoque desta fita.');
      setGuest({ chave: newKey(), itens: guest.itens.map(current => current.produtoId === item.produtoId ? { ...current, quantidade: quantity, produto: product } : current) });
    }
  });
  const remove = item => action(async () => {
    if (auth) setCart(await api(`/carrinho/item/${item.id}`, { token: auth.token, method: 'DELETE' }));
    else setGuest({ chave: newKey(), itens: guest.itens.filter(current => current.produtoId !== item.produtoId) });
    setNotice('Fita removida da sacola.');
  });
  const login = (form, register) => action(async () => {
    const user = await api(register ? '/auth/register' : '/auth/login', { method: 'POST', body: JSON.stringify(form) });
    let response;
    if (guest.itens.length) {
      const chave = guest.chave || newKey();
      saveGuest({ ...guest, chave }); setGuest(current => ({ ...current, chave }));
      try {
        response = await api('/carrinho/mesclar', { token: user.token, method: 'POST', body: JSON.stringify({ chave, itens: guest.itens.map(({ produtoId, quantidade }) => ({ produtoId, quantidade })) }) });
      } catch (error) {
        throw new Error(`${register ? 'Sua conta foi criada.' : 'Suas credenciais foram aceitas.'} Não foi possível reunir as sacolas: ${error.message} Os itens de visitante foram preservados. Revise sua sacola e entre novamente.`);
      }
    } else response = await api('/carrinho', { token: user.token });
    sessionStorage.setItem('gl-session', JSON.stringify(user));
    setAuth(user); setCart(response); setGuest({ chave: null, itens: [] });
    setNotice(`Bem-vinda(o), ${user.nome}. Sua sacola está salva na conta.`); navigate('sacola');
  });
  const logout = () => { setAuth(null); setCart(emptyCart); sessionStorage.removeItem('gl-session'); navigate('loja'); setNotice('Você saiu da conta. Sua sacola permanece salva para o próximo acesso.'); };
  const itens = auth ? cart.itens : guest.itens;
  const disabled = busy || restoring;
  return <>
    <div className="topbar">Pequenos detalhes. Infinitas possibilidades.</div>
    <header className="header wrap">
      <a className="brand" href="#loja" aria-label="Geração Livre — início"><img className="brand-logo" src={`${process.env.PUBLIC_URL}/brand/geracao-livre-logo.png`} alt="Geração Livre" width="112" height="112" /><span className="brand-copy">GERAÇÃO LIVRE<small>CRIATIVIDADE POR METRO</small></span></a>
      <nav aria-label="Navegação principal"><a href="#loja">Nossas fitas</a>{auth?.role === 'ROLE_ADMIN' && <a href="#admin">Administrar</a>}</nav>
      <div className="header-actions">{auth ? <><span className="user-name">Olá, {auth.nome.split(' ')[0]}</span><button className="text-button" disabled={disabled} onClick={logout}>Sair</button></> : <a href="#entrar">Minha conta</a>}<a className="bag-link" href="#sacola">Sacola <span>{itens.length}</span></a></div>
    </header>
    {config?.demonstracao && <div className="demo-bar">Loja de demonstração · Produtos, preços e regras de corte ilustrativos · Sem cobranças</div>}
    {notice && <div className="notice wrap" role="status"><span>{notice}</span><button aria-label="Fechar aviso" onClick={() => setNotice('')}>×</button></div>}
    <main>
      {route.page === 'loja' && <Catalog revision={revision} add={add} disabled={disabled} />}
      {route.page === 'produto' && <Product id={route.id} revision={revision} add={add} disabled={disabled} />}
      {route.page === 'sacola' && <Cart itens={itens} auth={auth} change={changeQuantity} remove={remove} disabled={disabled} />}
      {route.page === 'entrar' && <Auth onSubmit={login} disabled={disabled} />}
      {route.page === 'admin' && (restoring ? <p className="wrap">Verificando acesso…</p> : auth?.role === 'ROLE_ADMIN' ? <Admin auth={auth} action={action} disabled={disabled} updated={() => setRevision(value => value + 1)} /> : <div className="empty wrap"><h1>Acesso administrativo</h1><p>Entre com uma conta de administrador para continuar.</p><a href="#entrar" className="button">Entrar</a></div>)}
      {!['loja', 'produto', 'sacola', 'entrar', 'admin'].includes(route.page) && <div className="empty wrap"><h1>Página não encontrada</h1><a href="#loja">Voltar à loja</a></div>}
    </main>
    <footer className="footer wrap"><div className="footer-brand"><img src={`${process.env.PUBLIC_URL}/brand/geracao-livre-logo.png`} alt="Geração Livre" width="88" height="88" /><div><strong>GERAÇÃO LIVRE</strong><p>Fitas para ideias que ganham vida.</p></div></div><span>Bandeirantes · Paraná</span><a href="#loja">Continuar criando ↗</a></footer>
  </>;
}
