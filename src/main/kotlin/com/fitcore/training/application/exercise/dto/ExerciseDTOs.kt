package com.fitcore.training.application.exercise.dto

import com.fitcore.training.domain.exercise.model.Exercise
import java.util.UUID

data class ExerciseRequest(
    val name: String,
    val description: String?,
    val muscleGroup: String?,
    val equipment: String?,
    val mediaUrl: String?,
    val mediaUrl2: String?
)

data class ExerciseResponse(
    val id: UUID,
    val name: String,
    val description: String?,
    val muscleGroup: String?,
    val equipment: String?,
    val mediaUrl: String?,
    val mediaUrl2: String?
)

fun Exercise.toResponse(): ExerciseResponse = ExerciseResponse(
    id = this.id,
    name = this.name,
    description = this.description,
    muscleGroup = this.muscleGroup,
    equipment = this.equipment,
    mediaUrl = this.mediaUrl,
    mediaUrl2 = this.mediaUrl2
)
