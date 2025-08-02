package com.fitcore.training.domain.workouttemplate.port.out

import com.fitcore.training.domain.workouttemplate.model.WorkoutTemplate
import java.util.UUID

interface WorkoutTemplateRepositoryPort {
    fun save(workoutTemplate: WorkoutTemplate): WorkoutTemplate
    fun findById(id: UUID): WorkoutTemplate?
    fun findAllPublic(): List<WorkoutTemplate>
    fun deleteById(id: UUID)
    fun deleteAll()
}
