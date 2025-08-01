package com.fitcore.training.application.workouttemplate.port.`in`

import com.fitcore.training.application.workouttemplate.dto.*
import java.util.UUID

interface WorkoutTemplateUseCase {
    fun createPublic(request: WorkoutTemplateRequest): WorkoutTemplateResponse
    fun createPrivate(request: WorkoutTemplatePrivateRequest): WorkoutTemplateResponse
    fun findById(id: UUID): WorkoutTemplateResponse
    fun findAllPublic(): List<WorkoutTemplateResponse>
    
    // Métodos HIDRATADOS (com dados completos dos exercícios)
    fun findByIdEnriched(id: UUID): WorkoutTemplateEnrichedResponse
    fun findAllPublicEnriched(): List<WorkoutTemplateEnrichedResponse>
}
