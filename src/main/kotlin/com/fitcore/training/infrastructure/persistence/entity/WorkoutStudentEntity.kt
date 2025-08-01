package com.fitcore.training.infrastructure.persistence.entity

import jakarta.persistence.*
import java.util.UUID

@Entity
@Table(name = "workout_students")
@IdClass(WorkoutStudentId::class)
class WorkoutStudentEntity(
    @Id
    @Column(name = "workout_template_id")
    val workoutTemplateId: UUID,
    
    @Id
    @Column(name = "student_id")
    val studentId: UUID
)

// Classe para a chave composta
class WorkoutStudentId(
    val workoutTemplateId: UUID = UUID.randomUUID(),
    val studentId: UUID = UUID.randomUUID()
) : java.io.Serializable {
    
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is WorkoutStudentId) return false
        return workoutTemplateId == other.workoutTemplateId && studentId == other.studentId
    }
    
    override fun hashCode(): Int {
        return workoutTemplateId.hashCode() + studentId.hashCode()
    }
}
