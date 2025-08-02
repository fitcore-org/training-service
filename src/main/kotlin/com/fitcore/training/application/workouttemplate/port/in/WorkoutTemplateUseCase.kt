package com.fitcore.training.application.workouttemplate.port.`in`

import com.fitcore.training.application.workouttemplate.dto.*
import java.util.UUID

interface WorkoutTemplateUseCase {
    fun createPublic(request: WorkoutTemplateRequest): WorkoutTemplateResponse
    fun createPrivate(request: WorkoutTemplatePrivateRequest): WorkoutTemplateResponse
    
    fun findById(id: UUID): WorkoutTemplateResponse
    fun findAllPublic(): List<WorkoutTemplateResponse>
    
    fun update(id: UUID, request: WorkoutTemplateRequest): WorkoutTemplateResponse
    fun delete(id: UUID)
}
