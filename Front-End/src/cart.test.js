import { validQuantity, subtotal, loadGuest } from './cart';
const product = { preco: '3.90', estoque: '10.00', quantidadeMinima: '0.50', incremento: '0.10', ativo: true };
test('valida corte mínimo, incremento e estoque em centésimos de metro', () => {
  expect(validQuantity(product, '0.70')).toBe(true);
  expect(validQuantity(product, '0.55')).toBe(false);
  expect(validQuantity(product, '0.40')).toBe(false);
  expect(validQuantity(product, '10.10')).toBe(false);
  expect(validQuantity(product, '0.501')).toBe(false);
  expect(validQuantity({ ...product, ativo: false }, '0.70')).toBe(false);
});
test('calcula subtotal decimal e arredonda em centavos', () => {
  expect(subtotal(product, '0.70')).toBe(2.73);
  expect(subtotal({ ...product, preco: '1.99' }, '0.50')).toBe(1);
});
test('ignora dados de sacola corrompidos no navegador', () => {
  localStorage.setItem('geracao-livre-visitante-v1', 'corrompido');
  expect(loadGuest()).toEqual({ chave: null, itens: [] });
  localStorage.clear();
});
