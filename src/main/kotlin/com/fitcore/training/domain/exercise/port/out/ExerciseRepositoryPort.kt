package com.fitcore.training.domain.exercise.port.out

import com.fitcore.training.domain.exercise.model.Exercise
import java.util.UUID

// Contrato que o domínio exige para persistência. "Preciso que alguém implemente isso."
interface ExerciseRepositoryPort {
    fun save(exercise: Exercise): Exercise
    fun findAll(): List<Exercise>
    fun findById(id: UUID): Exercise?
    fun findByName(name: String): Exercise?
    fun deleteAll()
}
