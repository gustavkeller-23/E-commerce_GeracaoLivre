ALTER TABLE produtos DROP CONSTRAINT material_gorgurao;
ALTER TABLE produtos ADD CONSTRAINT material_fitas CHECK (material IS NULL OR material IN ('GORGURAO', 'CETIM'));
UPDATE categorias SET descricao = 'Fitas em cores lisas' WHERE LOWER(nome) = 'lisas';
UPDATE categorias SET descricao = 'Fitas com estampas' WHERE LOWER(nome) = 'estampadas';
