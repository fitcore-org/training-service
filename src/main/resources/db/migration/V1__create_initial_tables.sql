CREATE TABLE exercises (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    muscle_group VARCHAR(100),
    equipment VARCHAR(100),
    media_url VARCHAR(255)
);

CREATE TABLE workout_templates (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    is_public BOOLEAN DEFAULT false
);

CREATE TABLE workout_items (
    id UUID PRIMARY KEY,
    template_id UUID REFERENCES workout_templates(id) ON DELETE CASCADE,
    exercise_id UUID REFERENCES exercises(id),
    sets VARCHAR(50), -- Usar VARCHAR para flexibilidade como "3-4"
    reps VARCHAR(50), -- Usar VARCHAR para "8-12", "Até a falha"
    rest_seconds INTEGER,
    observation TEXT,
    item_order INTEGER NOT NULL -- Para ordenar os exercícios no treino
);