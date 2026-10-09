import { meters } from '../cart';
export default function Quantity({ product, value, onChange, id = 'quantity', disabled }) {
  return <label className="quantity-field" htmlFor={id}>Metragem (m)<input id={id} type="number" min={product.quantidadeMinima} step={product.incremento} max={product.estoque} value={value} disabled={disabled} onChange={event => onChange(event.target.value)} /><small>Mín. {meters(product.quantidadeMinima)} m · incremento {meters(product.incremento)} m</small></label>;
}
