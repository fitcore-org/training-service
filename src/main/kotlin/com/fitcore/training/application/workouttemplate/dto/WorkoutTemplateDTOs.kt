package com.fitcore.training.application.workouttemplate.dto

import com.fitcore.training.application.exercise.dto.ExerciseResponse
import com.fitcore.training.domain.workouttemplate.model.*
import java.util.UUID

// DTOs para os itens
data class WorkoutItemRequest(
    val exerciseId: UUID, 
    val sets: String, 
    val reps: String, 
    val restSeconds: Int? = null,
    val observation: String? = null,
    val order: Int
)

// DTOs para os treinos
data class WorkoutItemResponse(
    val id: UUID,
    val exercise: ExerciseResponse, // ← Objeto completo do exercício!
    val sets: String, 
    val reps: String, 
    val restSeconds: Int?,
    val observation: String?,
    val order: Int
)

// DTOs para o template
data class WorkoutTemplateRequest(
    val name: String, 
    val description: String?, 
    val items: List<WorkoutItemRequest>
)

// DTO para criação de treinos privados
data class WorkoutTemplatePrivateRequest(
    val name: String, 
    val description: String?, 
    val items: List<WorkoutItemRequest>,
    val studentIds: List<UUID> // IDs dos estudantes que terão acesso
)

data class WorkoutTemplateResponse(
    val id: UUID, 
    val name: String, 
    val description: String?, 
    val isPublic: Boolean,
    val items: List<WorkoutItemResponse>,
    val studentIds: List<UUID> = emptyList() // IDs dos estudantes (vazio para treinos públicos)
)

// Mappers

fun WorkoutItem.toResponse(exercise: ExerciseResponse): WorkoutItemResponse = WorkoutItemResponse(
    id = this.id,
    exercise = exercise,
    sets = this.sets, 
    reps = this.reps, 
    restSeconds = this.restSeconds,
    observation = this.observation,
    order = this.order
)
