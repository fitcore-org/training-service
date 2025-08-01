-- Criar tabela para associação workout_template <-> students
CREATE TABLE workout_students (
    workout_template_id UUID NOT NULL,
    student_id UUID NOT NULL,
    PRIMARY KEY (workout_template_id, student_id),
    FOREIGN KEY (workout_template_id) REFERENCES workout_templates(id) ON DELETE CASCADE
);

-- Criar índice para melhorar performance de consultas
CREATE INDEX idx_workout_students_template_id ON workout_students(workout_template_id);
CREATE INDEX idx_workout_students_student_id ON workout_students(student_id);
