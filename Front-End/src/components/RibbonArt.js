import { useId, useEffect, useState } from 'react';

export default function RibbonArt({ product, decorative = false }) {
  const id = useId().replace(/:/g, '');
  const [failed, setFailed] = useState(false);
  useEffect(() => { setFailed(false); }, [product?.imagemUrl]);
  const cor = product?.cor?.toLowerCase() || '';
  const color = cor.includes('turquesa') ? '#5ce0e4' : cor.includes('rosa') ? '#ba7f90' : cor.includes('verde') ? '#8fa38b' : cor.includes('azul') ? '#90abc5' : cor.includes('lavanda') ? '#b09ec2' : cor.includes('terra') ? '#b87861' : '#d6c39c';
  if (product?.imagemUrl && !failed) return <img className="ribbon-image" src={product.imagemUrl} alt={product.nome} onError={() => setFailed(true)} />;
  return <svg viewBox="0 0 400 300" role={decorative ? undefined : 'img'} aria-hidden={decorative || undefined} aria-label={decorative ? undefined : `Ilustração de fita ${product?.cor || ''}`}>
    <defs>
      <pattern id={`lines-${id}`} width="5" height="5" patternUnits="userSpaceOnUse"><rect width="5" height="5" fill={color} /><path d="M0 1h5" stroke="#fff" strokeOpacity=".22" /><path d="M0 4h5" stroke="#000" strokeOpacity=".07" /></pattern>
      <filter id={`shadow-${id}`}><feDropShadow dx="0" dy="10" stdDeviation="9" floodOpacity=".12" /></filter>
    </defs>
    <path d="M210 170c34 16 27 73 88 76l39-24c-78 5-57-59-109-73" fill={`url(#lines-${id})`} />
    <g filter={`url(#shadow-${id})`} transform="rotate(-17 190 144)">
      <ellipse cx="186" cy="150" rx="88" ry="66" fill={color} />
      <rect x="98" y="113" width="176" height="42" fill={`url(#lines-${id})`} />
      <ellipse cx="186" cy="113" rx="88" ry="64" fill="#f8f2e5" stroke={color} strokeWidth="8" />
      <ellipse cx="186" cy="113" rx="73" ry="51" fill="none" stroke={color} strokeWidth="14" />
      <ellipse cx="186" cy="113" rx="51" ry="35" fill="none" stroke={color} strokeWidth="12" />
      <ellipse cx="186" cy="113" rx="23" ry="15" fill="#b7a994" />
      <ellipse cx="186" cy="115" rx="15" ry="9" fill="#796b58" />
    </g>
  </svg>;
}
