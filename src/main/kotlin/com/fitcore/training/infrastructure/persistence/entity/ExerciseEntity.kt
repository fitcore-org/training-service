package com.fitcore.training.infrastructure.persistence.entity

import jakarta.persistence.*
import java.util.UUID

@Entity 
@Table(name = "exercises")
class ExerciseEntity(
    @Id val id: UUID,
    var name: String,
    @Column(columnDefinition = "TEXT") var description: String?,
    var muscleGroup: String?,
    var equipment: String?,
    var mediaUrl: String?
)
