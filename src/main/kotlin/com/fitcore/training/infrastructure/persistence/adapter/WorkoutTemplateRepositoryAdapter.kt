package com.fitcore.training.infrastructure.persistence.adapter

import com.fitcore.training.domain.workouttemplate.model.WorkoutTemplate
import com.fitcore.training.domain.workouttemplate.port.out.WorkoutTemplateRepositoryPort
import com.fitcore.training.infrastructure.persistence.entity.*
import com.fitcore.training.infrastructure.persistence.repository.WorkoutTemplateJpaRepository
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class WorkoutTemplateRepositoryAdapter(
    private val jpaRepository: WorkoutTemplateJpaRepository
) : WorkoutTemplateRepositoryPort {

    override fun save(workoutTemplate: WorkoutTemplate): WorkoutTemplate {
        return jpaRepository.save(workoutTemplate.toEntity()).toDomain()
    }

    override fun findById(id: UUID): WorkoutTemplate? {
        return jpaRepository.findById(id).map { it.toDomain() }.orElse(null)
    }

    override fun findAllPublic(): List<WorkoutTemplate> {
        return jpaRepository.findByIsPublicTrue().map { it.toDomain() }
    }

    override fun findAllPrivate(): List<WorkoutTemplate> {
        return jpaRepository.findByIsPublicFalse()
            .filter { it.students.isNotEmpty() } // Filtrar apenas treinos com estudantes
            .map { it.toDomain() }
    }

    override fun findByStudentId(studentId: UUID): List<WorkoutTemplate> {
        return jpaRepository.findByStudentId(studentId).map { it.toDomain() }
    }

    override fun deleteById(id: UUID) {
        jpaRepository.deleteById(id)
    }

    override fun deleteAll() {
        jpaRepository.deleteAll()
    }
}
