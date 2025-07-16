package com.fitcore.training.domain.exercise.model

import java.util.UUID

// O Aggregate Root. Contém estado e comportamento (regras de negócio).
class Exercise(
    val id: UUID = UUID.randomUUID(),
    var name: String,
    var description: String?,
    var muscleGroup: String?,
    var equipment: String?,
    var mediaUrl: String?,
    var mediaUrl2: String?
) {
    // Invariante de negócio garantida pelo domínio.
    init {
        require(name.isNotBlank()) { "O nome do exercício não pode ser vazio." }
    }
}
