package com.fitcore.training.domain.workouttemplate.model

import java.util.UUID

// Parte do agregado WorkoutTemplate
class WorkoutItem(
    val id: UUID = UUID.randomUUID(),
    val exerciseId: UUID, // Apenas a referência ao ID do exercício
    val sets: String,
    val reps: String,
    val restSeconds: Int?,
    val observation: String?,
    val order: Int
)
