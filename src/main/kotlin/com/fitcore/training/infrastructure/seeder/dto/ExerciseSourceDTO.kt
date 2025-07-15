package com.fitcore.training.infrastructure.seeder.dto

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * DTO que representa a estrutura dos dados de exercícios vindos do ExerciseDB
 * Baseado na estrutura JSON do repositório: https://github.com/yuhonas/free-exercise-db
 */
data class ExerciseSourceDTO(
    @JsonProperty("id")
    val id: String,
    
    @JsonProperty("name")
    val name: String,
    
    @JsonProperty("force")
    val force: String?,
    
    @JsonProperty("level")
    val level: String,
    
    @JsonProperty("mechanic")
    val mechanic: String?,
    
    @JsonProperty("equipment")
    val equipment: String?,
    
    @JsonProperty("primaryMuscles")
    val primaryMuscles: List<String>,
    
    @JsonProperty("secondaryMuscles")
    val secondaryMuscles: List<String>,
    
    @JsonProperty("instructions")
    val instructions: List<String>,
    
    @JsonProperty("category")
    val category: String,
    
    @JsonProperty("images")
    val images: List<String>
)
