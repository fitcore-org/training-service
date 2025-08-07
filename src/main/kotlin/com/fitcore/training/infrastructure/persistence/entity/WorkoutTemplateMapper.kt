package com.fitcore.training.infrastructure.persistence.entity

import com.fitcore.training.domain.workouttemplate.model.WorkoutTemplate
import com.fitcore.training.domain.workouttemplate.model.WorkoutItem

fun WorkoutTemplate.toEntity() = WorkoutTemplateEntity(
    id = this.id,
    name = this.name,
    description = this.description,
    isPublic = this.isPublic,
    items = this.items.map { it.toEntity(this.id) }
)

fun WorkoutTemplateEntity.toDomain() = WorkoutTemplate(
    id = this.id,
    name = this.name,
    description = this.description,
    isPublic = this.isPublic,
    items = this.items.map { it.toDomain() },
    studentIds = emptyList() // Será preenchido no repositório
)

fun WorkoutTemplateEntity.toDomain(studentIds: List<java.util.UUID>) = WorkoutTemplate(
    id = this.id,
    name = this.name,
    description = this.description,
    isPublic = this.isPublic,
    items = this.items.map { it.toDomain() },
    studentIds = studentIds
)

fun WorkoutItem.toEntity(templateId: java.util.UUID) = WorkoutItemEntity(
    id = this.id,
    exerciseId = this.exerciseId,
    sets = this.sets,
    reps = this.reps,
    restSeconds = this.restSeconds,
    observation = this.observation,
    order = this.order,
    templateId = templateId
)

fun WorkoutItemEntity.toDomain() = WorkoutItem(
    id = this.id,
    exerciseId = this.exerciseId,
    sets = this.sets,
    reps = this.reps,
    restSeconds = this.restSeconds,
    observation = this.observation,
    order = this.order
)
