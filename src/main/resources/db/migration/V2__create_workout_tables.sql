-- V2__create_workout_tables.sql

CREATE TABLE IF NOT EXISTS workout_templates (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    is_public BOOLEAN DEFAULT false NOT NULL
);

CREATE TABLE IF NOT EXISTS workout_items (
    id UUID PRIMARY KEY,
    template_id UUID NOT NULL REFERENCES workout_templates(id) ON DELETE CASCADE,
    exercise_id UUID NOT NULL REFERENCES exercises(id),
    sets VARCHAR(50),
    reps VARCHAR(50),
    rest_seconds INTEGER,
    observation TEXT,
    item_order INTEGER NOT NULL -- Para ordenar os exercícios dentro do treino
);

-- Índice para melhorar a busca de itens por template
CREATE INDEX idx_workout_items_template_id ON workout_items(template_id);
