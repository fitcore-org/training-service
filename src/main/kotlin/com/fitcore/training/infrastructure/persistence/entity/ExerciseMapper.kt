package com.fitcore.training.infrastructure.persistence.entity

import com.fitcore.training.domain.exercise.model.Exercise

fun Exercise.toEntity() = ExerciseEntity(id, name, description, muscleGroup, equipment, mediaUrl)
fun ExerciseEntity.toDomain() = Exercise(id, name, description, muscleGroup, equipment, mediaUrl)
