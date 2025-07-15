package com.fitcore.training.infrastructure.web.controller

import com.fitcore.training.application.exercise.dto.*
import com.fitcore.training.application.exercise.port.`in`.ExerciseUseCase
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/v1/exercises")
class ExerciseController(
    private val useCase: ExerciseUseCase 
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@RequestBody request: ExerciseRequest) = useCase.create(request)

    @GetMapping
    fun getAll() = useCase.findAll()

    @GetMapping("/{id}")
    fun getById(@PathVariable id: UUID) = useCase.findById(id)
}
