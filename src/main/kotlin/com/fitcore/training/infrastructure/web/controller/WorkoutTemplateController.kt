package com.fitcore.training.infrastructure.web.controller

import com.fitcore.training.application.workouttemplate.dto.*
import com.fitcore.training.application.workouttemplate.port.`in`.WorkoutTemplateUseCase
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/v1/workouts")
class WorkoutTemplateController(private val useCase: WorkoutTemplateUseCase) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@RequestBody request: WorkoutTemplateRequest): WorkoutTemplateResponse {
        // Por enquanto, todos os treinos criados por essa rota serão públicos
        return useCase.createPublic(request)
    }

    @GetMapping("/public")
    fun getPublicTemplates(): List<WorkoutTemplateResponse> {
        return useCase.findAllPublic()
    }

    @GetMapping("/{id}")
    fun getById(@PathVariable id: UUID): WorkoutTemplateResponse {
        return useCase.findById(id)
    }

    // NOVOS ENDPOINTS HIDRATADOS (otimizados para mobile)
    @GetMapping("/public/enriched")
    fun getPublicTemplatesEnriched(): List<WorkoutTemplateEnrichedResponse> {
        return useCase.findAllPublicEnriched()
    }

    @GetMapping("/{id}/enriched")
    fun getByIdEnriched(@PathVariable id: UUID): WorkoutTemplateEnrichedResponse {
        return useCase.findByIdEnriched(id)
    }
}
