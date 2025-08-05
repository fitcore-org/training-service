package com.fitcore.training.infrastructure.persistence.repository

import com.fitcore.training.infrastructure.persistence.entity.WorkoutTemplateEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.UUID

interface WorkoutTemplateJpaRepository : JpaRepository<WorkoutTemplateEntity, UUID> {
    fun findByIsPublicTrue(): List<WorkoutTemplateEntity>
    fun findByIsPublicFalse(): List<WorkoutTemplateEntity>
    
    @Query("SELECT w FROM WorkoutTemplateEntity w JOIN w.students s WHERE s.studentId = :studentId")
    fun findByStudentId(@Param("studentId") studentId: UUID): List<WorkoutTemplateEntity>
}
