package com.fitcore.training.application.workouttemplate.service

import com.fitcore.training.application.workouttemplate.dto.*
import com.fitcore.training.application.workouttemplate.port.`in`.WorkoutTemplateUseCase
import com.fitcore.training.application.exception.ExerciseNotFoundException
import com.fitcore.training.application.exception.WorkoutTemplateNotFoundException
import com.fitcore.training.application.exercise.dto.toResponse
import com.fitcore.training.domain.exercise.port.out.ExerciseRepositoryPort
import com.fitcore.training.domain.workouttemplate.model.*
import com.fitcore.training.domain.workouttemplate.port.out.WorkoutTemplateRepositoryPort
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class WorkoutTemplateService(
    private val workoutRepository: WorkoutTemplateRepositoryPort,
    private val exerciseRepository: ExerciseRepositoryPort // Injetamos para validar os exercícios
) : WorkoutTemplateUseCase {

    override fun createPublic(request: WorkoutTemplateRequest): WorkoutTemplateResponse {
        // 1. Validar se todos os exercícios existem
        request.items.forEach {
            exerciseRepository.findById(it.exerciseId)
                ?: throw ExerciseNotFoundException("Exercise with ID ${it.exerciseId} not found.")
        }

        // 2. Mapear DTOs para o modelo de domínio
        val domainItems = request.items.map {
            WorkoutItem(
                exerciseId = it.exerciseId, 
                sets = it.sets, 
                reps = it.reps, 
                order = it.order, 
                restSeconds = it.restSeconds, 
                observation = it.observation
            )
        }

        // 3. Criar o agregado
        val workoutTemplate = WorkoutTemplate(
            name = request.name,
            description = request.description,
            isPublic = true,
            items = domainItems
        )

        // 4. Salvar e retornar a resposta
        return workoutRepository.save(workoutTemplate).toResponse()
    }

    override fun createPrivate(request: WorkoutTemplatePrivateRequest): WorkoutTemplateResponse {
        // 1. Validar se todos os exercícios existem
        request.items.forEach {
            exerciseRepository.findById(it.exerciseId)
                ?: throw ExerciseNotFoundException("Exercise with ID ${it.exerciseId} not found.")
        }

        // 2. Mapear DTOs para o modelo de domínio
        val domainItems = request.items.map {
            WorkoutItem(
                exerciseId = it.exerciseId, 
                sets = it.sets, 
                reps = it.reps, 
                order = it.order, 
                restSeconds = it.restSeconds, 
                observation = it.observation
            )
        }

        // 3. Criar o agregado privado
        val workoutTemplate = WorkoutTemplate(
            name = request.name,
            description = request.description,
            isPublic = false,
            items = domainItems,
            studentIds = request.studentIds
        )

        // 4. Salvar e retornar a resposta
        return workoutRepository.save(workoutTemplate).toResponse()
    }

    override fun findById(id: UUID): WorkoutTemplateResponse {
         return workoutRepository.findById(id)?.toResponse() 
             ?: throw WorkoutTemplateNotFoundException("Workout template with ID $id not found")
    }

    override fun findAllPublic(): List<WorkoutTemplateResponse> {
        return workoutRepository.findAllPublic().map { it.toResponse() }
    }

    // Métodos HIDRATADOS (com dados completos dos exercícios)
    override fun findByIdEnriched(id: UUID): WorkoutTemplateEnrichedResponse {
        val workoutTemplate = workoutRepository.findById(id)
            ?: throw WorkoutTemplateNotFoundException("Workout template with ID $id not found")
        
        return toEnrichedResponse(workoutTemplate)
    }

    override fun findAllPublicEnriched(): List<WorkoutTemplateEnrichedResponse> {
        return workoutRepository.findAllPublic().map { toEnrichedResponse(it) }
    }

    // Helper para hidratar com dados dos exercícios
    private fun toEnrichedResponse(workoutTemplate: WorkoutTemplate): WorkoutTemplateEnrichedResponse {
        val enrichedItems = workoutTemplate.items.map { item ->
            val exercise = exerciseRepository.findById(item.exerciseId)
                ?: throw ExerciseNotFoundException("Exercise with ID ${item.exerciseId} not found")
            
            item.toEnrichedResponse(exercise.toResponse())
        }

        return WorkoutTemplateEnrichedResponse(
            id = workoutTemplate.id,
            name = workoutTemplate.name,
            description = workoutTemplate.description,
            isPublic = workoutTemplate.isPublic,
            items = enrichedItems,
            studentIds = workoutTemplate.studentIds
        )
    }
}
