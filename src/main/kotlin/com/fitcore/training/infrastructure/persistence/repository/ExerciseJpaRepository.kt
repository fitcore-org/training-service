package com.fitcore.training.infrastructure.persistence.repository

import com.fitcore.training.infrastructure.persistence.entity.ExerciseEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface ExerciseJpaRepository : JpaRepository<ExerciseEntity, UUID> {
    fun findByName(name: String): ExerciseEntity?
}
