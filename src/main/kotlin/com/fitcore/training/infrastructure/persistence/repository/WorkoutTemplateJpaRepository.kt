package com.fitcore.training.infrastructure.persistence.repository

import com.fitcore.training.infrastructure.persistence.entity.WorkoutTemplateEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface WorkoutTemplateJpaRepository : JpaRepository<WorkoutTemplateEntity, UUID> {
    fun findByIsPublicTrue(): List<WorkoutTemplateEntity>
}
