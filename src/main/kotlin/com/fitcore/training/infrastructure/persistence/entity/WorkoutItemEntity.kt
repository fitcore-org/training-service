package com.fitcore.training.infrastructure.persistence.entity

import jakarta.persistence.*
import java.util.UUID

@Entity
@Table(name = "workout_items")
class WorkoutItemEntity(
    @Id val id: UUID,
    @Column(name = "exercise_id") val exerciseId: UUID,
    val sets: String,
    val reps: String,
    @Column(name = "rest_seconds") val restSeconds: Int?,
    val observation: String?,
    @Column(name = "item_order") val order: Int,
    @Column(name = "template_id") val templateId: UUID? = null
)
