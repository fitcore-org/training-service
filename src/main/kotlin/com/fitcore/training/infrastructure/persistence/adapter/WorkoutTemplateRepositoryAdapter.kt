package com.fitcore.training.infrastructure.persistence.adapter

import com.fitcore.training.domain.workouttemplate.model.WorkoutTemplate
import com.fitcore.training.domain.workouttemplate.port.out.WorkoutTemplateRepositoryPort
import com.fitcore.training.infrastructure.persistence.entity.*
import com.fitcore.training.infrastructure.persistence.repository.WorkoutTemplateJpaRepository
import com.fitcore.training.infrastructure.persistence.repository.WorkoutStudentJpaRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Component
class WorkoutTemplateRepositoryAdapter(
    private val jpaRepository: WorkoutTemplateJpaRepository,
    private val studentRepository: WorkoutStudentJpaRepository
) : WorkoutTemplateRepositoryPort {

    @Transactional
    override fun save(workoutTemplate: WorkoutTemplate): WorkoutTemplate {
        // Salva o template primeiro
        val savedEntity = jpaRepository.save(workoutTemplate.toEntity())
        
        // Remove relacionamentos antigos se for um update
        studentRepository.deleteByWorkoutTemplateId(savedEntity.id)
        
        // Salva novos relacionamentos
        val studentEntities = workoutTemplate.studentIds.map { 
            WorkoutStudentEntity(savedEntity.id, it) 
        }
        studentRepository.saveAll(studentEntities)
        
        // Buscar os estudantes associados para retornar o modelo completo
        val savedStudents = studentRepository.findByWorkoutTemplateId(savedEntity.id)
        return savedEntity.toDomain(savedStudents.map { it.studentId })
    }

    override fun findById(id: UUID): WorkoutTemplate? {
        return jpaRepository.findById(id).map { entity ->
            val students = studentRepository.findByWorkoutTemplateId(entity.id)
            entity.toDomain(students.map { it.studentId })
        }.orElse(null)
    }

    override fun findAllPublic(): List<WorkoutTemplate> {
        return jpaRepository.findByIsPublicTrue().map { entity ->
            val students = studentRepository.findByWorkoutTemplateId(entity.id)
            entity.toDomain(students.map { it.studentId })
        }
    }

    override fun findAllPrivate(): List<WorkoutTemplate> {
        return jpaRepository.findByIsPublicFalse().map { entity ->
            // Buscar estudantes associados para este treino
            val students = studentRepository.findByWorkoutTemplateId(entity.id)
            entity.toDomain(students.map { it.studentId })
        }
    }

    override fun findByStudentId(studentId: UUID): List<WorkoutTemplate> {
        return jpaRepository.findByStudentId(studentId).map { entity ->
            val students = studentRepository.findByWorkoutTemplateId(entity.id)
            entity.toDomain(students.map { it.studentId })
        }
    }

    @Transactional
    override fun deleteById(id: UUID) {
        // Primeiro remove os relacionamentos na tabela workout_students
        studentRepository.deleteByWorkoutTemplateId(id)
        // Depois remove o workout template
        jpaRepository.deleteById(id)
    }

    override fun deleteAll() {
        jpaRepository.deleteAll()
    }
}
