package com.fitcore.training.infrastructure.persistence.adapter

import com.fitcore.training.domain.exercise.model.Exercise
import com.fitcore.training.domain.exercise.port.out.ExerciseRepositoryPort
import com.fitcore.training.infrastructure.persistence.entity.*
import com.fitcore.training.infrastructure.persistence.repository.ExerciseJpaRepository
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class ExerciseRepositoryAdapter(
    private val jpaRepository: ExerciseJpaRepository
) : ExerciseRepositoryPort {

    override fun save(exercise: Exercise): Exercise {
        return jpaRepository.save(exercise.toEntity()).toDomain()
    }

    override fun findAll(): List<Exercise> {
        return jpaRepository.findAll().map { it.toDomain() }
    }

    override fun findById(id: UUID): Exercise? {
        return jpaRepository.findById(id).map { it.toDomain() }.orElse(null)
    }

    override fun findByName(name: String): Exercise? {
        return jpaRepository.findByName(name)?.toDomain()
    }

    override fun deleteAll() {
        jpaRepository.deleteAll()
    }
}
