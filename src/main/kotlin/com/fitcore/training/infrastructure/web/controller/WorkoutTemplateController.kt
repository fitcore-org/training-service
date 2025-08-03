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

    @PostMapping("/private")
    @ResponseStatus(HttpStatus.CREATED)
    fun createPrivate(@RequestBody request: WorkoutTemplatePrivateRequest): WorkoutTemplateResponse {
        return useCase.createPrivate(request)
    }

    @GetMapping("/public")
    fun getPublicTemplates(): List<WorkoutTemplateResponse> {
        return useCase.findAllPublic()
    }

    @GetMapping("/private")
    fun getPrivateTemplates(): List<WorkoutTemplateResponse> {
        return useCase.findAllPrivate()
    }

    @GetMapping("/student/{studentId}")
    fun getTemplatesByStudentId(@PathVariable studentId: UUID): List<WorkoutTemplateResponse> {
        return useCase.findByStudentId(studentId)
    }

    @GetMapping("/{id}")
    fun getById(@PathVariable id: UUID): WorkoutTemplateResponse {
        return useCase.findById(id)
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: UUID, @RequestBody request: WorkoutTemplateRequest): WorkoutTemplateResponse {
        return useCase.update(id, request)
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable id: UUID) {
        useCase.delete(id)
    }
}
