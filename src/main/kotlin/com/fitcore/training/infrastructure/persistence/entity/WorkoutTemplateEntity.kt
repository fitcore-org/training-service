package com.fitcore.training.infrastructure.persistence.entity

import jakarta.persistence.*
import java.util.UUID

@Entity
@Table(name = "workout_templates")
class WorkoutTemplateEntity(
    @Id val id: UUID,
    val name: String,
    val description: String?,
    @Column(name = "is_public") val isPublic: Boolean,
    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "template_id")
    @OrderBy("order ASC")
    val items: List<WorkoutItemEntity>,
    
    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "workout_template_id")
    val students: List<WorkoutStudentEntity> = emptyList()
)
