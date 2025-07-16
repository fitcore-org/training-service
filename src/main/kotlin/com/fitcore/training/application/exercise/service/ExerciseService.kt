package com.fitcore.training.application.exercise.service

import com.fitcore.training.application.exercise.dto.*
import com.fitcore.training.application.exercise.port.`in`.ExerciseUseCase
import com.fitcore.training.application.exception.ExerciseNotFoundException
import com.fitcore.training.domain.exercise.model.Exercise
import com.fitcore.training.domain.exercise.port.out.ExerciseRepositoryPort
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class ExerciseService(
    private val repositoryPort: ExerciseRepositoryPort 
) : ExerciseUseCase {

    override fun create(request: ExerciseRequest): ExerciseResponse {
        val exercise = Exercise(
            name = request.name,
            description = request.description,
            muscleGroup = request.muscleGroup,
            equipment = request.equipment,
            mediaUrl = request.mediaUrl,
            mediaUrl2 = request.mediaUrl2
        )
        return repositoryPort.save(exercise).toResponse()
    }

    override fun findAll(): List<ExerciseResponse> {
        return repositoryPort.findAll().map { it.toResponse() }
    }

    override fun findById(id: UUID): ExerciseResponse {
        return repositoryPort.findById(id)?.toResponse()
            ?: throw ExerciseNotFoundException("Exercise with ID $id not found.")
    }
}
