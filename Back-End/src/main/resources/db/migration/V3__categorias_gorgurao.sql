INSERT INTO categorias(nome, descricao, icone)
SELECT 'Lisas', 'Fitas de gorgurão em cores lisas', ''
WHERE NOT EXISTS (SELECT 1 FROM categorias WHERE LOWER(nome) = 'lisas');
INSERT INTO categorias(nome, descricao, icone)
SELECT 'Estampadas', 'Fitas de gorgurão com estampas', ''
WHERE NOT EXISTS (SELECT 1 FROM categorias WHERE LOWER(nome) = 'estampadas');
