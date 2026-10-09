const base = process.env.REACT_APP_API_URL || '';
export async function api(path, { token, ...options } = {}) {
  let response;
  try {
    response = await fetch(`${base}/api${path}`, {
      ...options,
      headers: { 'Content-Type': 'application/json', ...(token ? { Authorization: `Bearer ${token}` } : {}), ...options.headers },
    });
  } catch (error) { throw new Error('Não foi possível conectar à loja. Tente novamente.'); }
  const data = response.status === 204 ? null : await response.json().catch(() => null);
  if (!response.ok) {
    const error = new Error(data?.message || 'Não foi possível concluir a operação.');
    error.status = response.status;
    error.campos = data?.campos;
    throw error;
  }
  return data;
}
export const catalogEventsUrl = `${base}/api/produtos/eventos`;
