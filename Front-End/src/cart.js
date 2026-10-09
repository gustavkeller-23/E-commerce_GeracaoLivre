const STORAGE_KEY = 'geracao-livre-visitante-v1';
// Quantidades em centésimos de metro e preços em centavos evitam erros de soma binária.
export const centesimos = value => Math.round(Number(value) * 100);
export const decimal = value => (value / 100).toFixed(2);
export const money = value => Number(value).toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
export const meters = value => Number(value).toLocaleString('pt-BR', { maximumFractionDigits: 2 });
export const price = product => product.precoPromocional ?? product.preco;
export function validQuantity(product, value) {
  if (!/^\d+(?:[.,]\d{1,2})?$/.test(String(value))) return false;
  const quantity = centesimos(String(value).replace(',', '.'));
  const minimum = centesimos(product.quantidadeMinima);
  const step = centesimos(product.incremento);
  return step > 0 && quantity >= minimum && quantity <= centesimos(product.estoque) && (quantity - minimum) % step === 0 && product.ativo !== false;
}
export function subtotal(product, quantity) {
  return Math.round(centesimos(price(product)) * centesimos(quantity) / 100) / 100;
}
export function loadGuest() {
  try {
    const data = JSON.parse(localStorage.getItem(STORAGE_KEY));
    if (Array.isArray(data?.itens) && data.itens.length <= 100) {
      return { chave: data.chave || null, itens: data.itens.filter(item => Number.isSafeInteger(item.produtoId) && Number(item.quantidade) > 0 && item.produto) };
    }
  } catch (error) { /* A loja continua utilizável se o armazenamento estiver indisponível. */ }
  return { chave: null, itens: [] };
}
export function saveGuest(data) {
  try { localStorage.setItem(STORAGE_KEY, JSON.stringify(data)); return true; }
  catch (error) { return false; }
}
export function newKey() {
  return `cart-${Date.now()}-${Math.random().toString(36).slice(2, 12)}`;
}
