package com.fitcore.training.application.exercise.port.`in`

import com.fitcore.training.application.exercise.dto.*
import java.util.UUID

// Contrato que a aplicação oferece para o mundo exterior. "Isso é o que eu sei fazer."
interface ExerciseUseCase {
    fun create(request: ExerciseRequest): ExerciseResponse
    fun findAll(): List<ExerciseResponse>
    fun findById(id: UUID): ExerciseResponse
}
