import { render, screen } from '@testing-library/react';
import App from './App';

test('exibe o catálogo vazio sem criar produtos fictícios quando a API retorna vazio', async () => {
  global.fetch = jest.fn(async () => ({ ok: true, status: 200, json: async () => ({ itens: [], total: 0, totalPaginas: 0 }) }));
  global.fetch.mockImplementation(async url => ({ ok: true, status: 200, json: async () => url.includes('/categorias') ? [] : url.includes('/configuracao') ? { demonstracao: false } : { itens: [], total: 0, totalPaginas: 0 } }));
  render(<App />);
  expect(await screen.findByText('Nenhuma fita encontrada')).toBeInTheDocument();
  expect(screen.getByRole('link', { name: /Minha conta/ })).toBeInTheDocument();
  delete global.fetch;
});
