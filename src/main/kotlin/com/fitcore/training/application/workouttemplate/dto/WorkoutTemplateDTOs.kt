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

data class WorkoutItemResponse(
    val id: UUID,
    val exerciseId: UUID, 
    val sets: String, 
    val reps: String, 
    val restSeconds: Int?,
    val observation: String?,
    val order: Int
)

// DTOs HIDRATADOS (com dados completos do exercício)
data class WorkoutItemEnrichedResponse(
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

// DTOs HIDRATADOS para o template  
data class WorkoutTemplateEnrichedResponse(
    val id: UUID, 
    val name: String, 
    val description: String?, 
    val isPublic: Boolean,
    val items: List<WorkoutItemEnrichedResponse>, // ← Com exercícios completos!
    val studentIds: List<UUID> = emptyList() // IDs dos estudantes (vazio para treinos públicos)
)

// Mappers
fun WorkoutTemplate.toResponse(): WorkoutTemplateResponse = WorkoutTemplateResponse(
    id = this.id, 
    name = this.name, 
    description = this.description,
    isPublic = this.isPublic,
    items = this.items.map { it.toResponse() },
    studentIds = this.studentIds
)

fun WorkoutItem.toResponse(): WorkoutItemResponse = WorkoutItemResponse(
    id = this.id,
    exerciseId = this.exerciseId, 
    sets = this.sets, 
    reps = this.reps, 
    restSeconds = this.restSeconds,
    observation = this.observation,
    order = this.order
)

// Mappers HIDRATADOS
fun WorkoutItem.toEnrichedResponse(exercise: ExerciseResponse): WorkoutItemEnrichedResponse = WorkoutItemEnrichedResponse(
    id = this.id,
    exercise = exercise, // ← Exercício completo!
    sets = this.sets, 
    reps = this.reps, 
    restSeconds = this.restSeconds,
    observation = this.observation,
    order = this.order
)
