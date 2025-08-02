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
    private val exerciseRepository: ExerciseRepositoryPort 
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
        val savedTemplate = workoutRepository.save(workoutTemplate)
        return toResponse(savedTemplate)
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
        val savedTemplate = workoutRepository.save(workoutTemplate)
        return toResponse(savedTemplate)
    }

    // Métodos de consulta
    override fun findById(id: UUID): WorkoutTemplateResponse {
        val workoutTemplate = workoutRepository.findById(id)
            ?: throw WorkoutTemplateNotFoundException("Workout template with ID $id not found")
        
        return toResponse(workoutTemplate)
    }

    override fun findAllPublic(): List<WorkoutTemplateResponse> {
        return workoutRepository.findAllPublic().map { toResponse(it) }
    }

    override fun update(id: UUID, request: WorkoutTemplateRequest): WorkoutTemplateResponse {
        // 1. Verificar se o workout existe
        val existingWorkout = workoutRepository.findById(id)
            ?: throw WorkoutTemplateNotFoundException("Workout template with ID $id not found")
        
        // 2. Validar se todos os exercícios existem
        request.items.forEach {
            exerciseRepository.findById(it.exerciseId)
                ?: throw ExerciseNotFoundException("Exercise with ID ${it.exerciseId} not found.")
        }

        // 3. Mapear DTOs para o modelo de domínio
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

        // 4. Atualizar o workout mantendo as propriedades originais
        val updatedWorkout = WorkoutTemplate(
            id = existingWorkout.id,
            name = request.name,
            description = request.description,
            isPublic = existingWorkout.isPublic,
            items = domainItems,
            studentIds = existingWorkout.studentIds
        )

        // 5. Salvar e retornar a resposta
        val savedTemplate = workoutRepository.save(updatedWorkout)
        return toResponse(savedTemplate)
    }

    override fun delete(id: UUID) {
        // Verificar se o workout existe antes de deletar
        workoutRepository.findById(id)
            ?: throw WorkoutTemplateNotFoundException("Workout template with ID $id not found")
        
        workoutRepository.deleteById(id)
    }

    // Helper para converter com dados dos exercícios
    private fun toResponse(workoutTemplate: WorkoutTemplate): WorkoutTemplateResponse {
        val items = workoutTemplate.items.map { item ->
            val exercise = exerciseRepository.findById(item.exerciseId)
                ?: throw ExerciseNotFoundException("Exercise with ID ${item.exerciseId} not found")
            
            item.toResponse(exercise.toResponse())
        }

        return WorkoutTemplateResponse(
            id = workoutTemplate.id,
            name = workoutTemplate.name,
            description = workoutTemplate.description,
            isPublic = workoutTemplate.isPublic,
            items = items,
            studentIds = workoutTemplate.studentIds
        )
    }
}
