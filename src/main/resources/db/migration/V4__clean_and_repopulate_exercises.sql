-- Limpa os dados existentes para permitir que o seeder repopule com as duas imagens
-- Primeiro remove os itens de workout que referenciam exercícios
DELETE FROM workout_items;

-- Depois remove os templates de workout
DELETE FROM workout_templates;

-- Por último remove os exercícios
DELETE FROM exercises;

-- O seeder vai repopular tudo automaticamente na próxima inicialização
