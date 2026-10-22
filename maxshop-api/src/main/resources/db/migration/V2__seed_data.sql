-- Usuário vendedor "sistema", dono dos produtos de catálogo inicial.
-- Senha (apenas para ambiente de testes): senha1234
INSERT INTO users (id, name, email, password_hash, role, enabled)
VALUES (
    '11111111-1111-1111-1111-111111111111',
    'MaxShop Catálogo',
    'catalogo@maxshop.com.br',
    '$2b$12$7vWo8jyEr61uPQvEfVLMyO/JsZnqVvR8JiC5Gt5EJUB4lHB.CgCZG',
    'VENDEDOR',
    true
);

-- Categorias
INSERT INTO categories (id, name, slug) VALUES
    ('22222222-2222-2222-2222-222222222201', 'Casa e Limpeza', 'casa-e-limpeza'),
    ('22222222-2222-2222-2222-222222222202', 'Cozinha', 'cozinha'),
    ('22222222-2222-2222-2222-222222222203', 'Pet', 'pet'),
    ('22222222-2222-2222-2222-222222222204', 'Eletrônicos', 'eletronicos'),
    ('22222222-2222-2222-2222-222222222205', 'Organização', 'organizacao'),
    ('22222222-2222-2222-2222-222222222206', 'Ferramentas', 'ferramentas');

-- Produtos
INSERT INTO products (id, seller_id, category_id, name, slug, description, price, compare_at_price, stock_quantity, active) VALUES
    ('33333333-3333-3333-3333-333333333301', '11111111-1111-1111-1111-111111111111', '22222222-2222-2222-2222-222222222201',
     'Mop Giratório com Balde Espremedor', 'mop-giratorio-balde-espremedor',
     'Mop giratório 360° com balde espremedor, fibras de microfibra e cabo telescópico. Ideal para pisos frios, laminados e cerâmica.',
     103.90, 129.90, 48, true),

    ('33333333-3333-3333-3333-333333333302', '11111111-1111-1111-1111-111111111111', '22222222-2222-2222-2222-222222222202',
     'Cortador de Legumes Multifuncional 8 em 1', 'cortador-legumes-multifuncional-8-em-1',
     'Kit cortador multifuncional com 8 lâminas intercambiáveis para fatiar, ralar, picar e espiralar legumes com segurança.',
     79.90, NULL, 120, true),

    ('33333333-3333-3333-3333-333333333303', '11111111-1111-1111-1111-111111111111', '22222222-2222-2222-2222-222222222203',
     'Caixa de Areia Sanitária para Gatos', 'caixa-areia-sanitaria-gatos',
     'Caixa sanitária espaçosa com bordas altas para evitar respingos de areia, fácil de higienizar.',
     94.90, NULL, 60, true),

    ('33333333-3333-3333-3333-333333333304', '11111111-1111-1111-1111-111111111111', '22222222-2222-2222-2222-222222222204',
     'Suporte de TV Articulado até 55"', 'suporte-tv-articulado-55',
     'Suporte articulado para TVs de 32" a 55", com braço retrátil e inclinação ajustável, suporta até 25kg.',
     119.90, NULL, 35, true),

    ('33333333-3333-3333-3333-333333333305', '11111111-1111-1111-1111-111111111111', '22222222-2222-2222-2222-222222222201',
     'Vassoura e Rodo com Cabo Reforçado', 'vassoura-rodo-cabo-reforcado',
     'Kit vassoura e rodo com cerdas resistentes e cabo de alumínio reforçado, antiferrugem.',
     44.90, NULL, 90, true),

    ('33333333-3333-3333-3333-333333333306', '11111111-1111-1111-1111-111111111111', '22222222-2222-2222-2222-222222222205',
     'Organizador de Gavetas Ajustável 4 Peças', 'organizador-gavetas-ajustavel-4-pecas',
     'Kit com 4 organizadores de gaveta ajustáveis, ideal para cozinha, escritório e closet.',
     39.90, 54.90, 150, true),

    ('33333333-3333-3333-3333-333333333307', '11111111-1111-1111-1111-111111111111', '22222222-2222-2222-2222-222222222203',
     'Comedouro Duplo Elevado para Cães e Gatos', 'comedouro-duplo-elevado-pets',
     'Comedouro duplo elevado em inox, reduz esforço no pescoço do pet durante as refeições.',
     69.90, NULL, 75, true),

    ('33333333-3333-3333-3333-333333333308', '11111111-1111-1111-1111-111111111111', '22222222-2222-2222-2222-222222222206',
     'Furadeira Parafusadeira 12V com Maleta', 'furadeira-parafusadeira-12v-maleta',
     'Furadeira/parafusadeira sem fio 12V, bateria de lítio, com maleta e kit de bits.',
     189.90, 229.90, 25, true);
