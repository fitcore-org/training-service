package com.fitcore.training.infrastructure.persistence.repository

import com.fitcore.training.infrastructure.persistence.entity.WorkoutStudentEntity
import com.fitcore.training.infrastructure.persistence.entity.WorkoutStudentId
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.UUID

interface WorkoutStudentJpaRepository : JpaRepository<WorkoutStudentEntity, WorkoutStudentId> {
    
    @Modifying
    @Query("DELETE FROM WorkoutStudentEntity w WHERE w.workoutTemplateId = :templateId")
    fun deleteByWorkoutTemplateId(@Param("templateId") templateId: UUID)
    
    fun findByWorkoutTemplateId(workoutTemplateId: UUID): List<WorkoutStudentEntity>
}
