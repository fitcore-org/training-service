package com.fitcore.training.infrastructure.seeder

import com.fitcore.training.application.workouttemplate.dto.WorkoutTemplateRequest
import com.fitcore.training.application.workouttemplate.dto.WorkoutItemRequest
import com.fitcore.training.application.workouttemplate.port.`in`.WorkoutTemplateUseCase
import com.fitcore.training.infrastructure.persistence.repository.ExerciseJpaRepository
import com.fitcore.training.infrastructure.persistence.repository.WorkoutTemplateJpaRepository
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import java.util.UUID

/**
 * Seeder responsável por criar treinos padrão do sistema.
 * Executa após o ExerciseSeeder para garantir que os exercícios já existam.
 */
@Component
@Order(2) // Executa após o ExerciseSeeder (que tem Order(1))
class WorkoutTemplateSeeder(
    private val workoutTemplateUseCase: WorkoutTemplateUseCase,
    private val exerciseJpaRepository: ExerciseJpaRepository,
    private val workoutTemplateJpaRepository: WorkoutTemplateJpaRepository
) : CommandLineRunner {
    
    private val logger = LoggerFactory.getLogger(javaClass)

    override fun run(vararg args: String?) {
        if (workoutTemplateJpaRepository.findAll().isNotEmpty()) {
            logger.info("Treinos já existem no banco de dados. Seeder de treinos não será executado.")
            return
        }

        logger.info("--- Iniciando Seeder de Treinos Padrão ---")
        
        // Aguarda um pouco para garantir que os exercícios foram carregados
        Thread.sleep(2000)
        
        createChestWorkout()
        createBackWorkout()
        createLegsWorkout()
        createPushWorkout()
        
        logger.info("--- Seeder de Treinos Finalizado ---")
    }

    private fun getExerciseIdByName(name: String): UUID? {
        return try {
            val exercise = exerciseJpaRepository.findByName(name)
            exercise?.id
        } catch (e: Exception) {
            logger.warn("Exercício '$name' não encontrado: ${e.message}")
            null
        }
    }

    private fun createChestWorkout() {
        try {
            logger.info("Criando treino de peito...")
            
            val exercises = listOf(
                Triple("Supino reto com barra de apoio com empunhadura média", "4", "8-12"),
                Triple("Flexão inclinada", "3", "12-15"),
                Triple("Flexão em declive", "3", "10-12"),
                Triple("3/4 Situp", "3", "15-20")
            )
            
            val workoutItems = exercises.mapIndexedNotNull { index, (exerciseName, sets, reps) ->
                getExerciseIdByName(exerciseName)?.let { exerciseId ->
                    WorkoutItemRequest(
                        exerciseId = exerciseId,
                        sets = sets,
                        reps = reps,
                        restSeconds = when (index) {
                            0 -> 120  // Bench Press
                            3 -> 60   // Sit-Up
                            else -> 90
                        },
                        observation = when (index) {
                            0 -> "Exercício principal. Foque na técnica e controle do movimento."
                            1 -> "Para trabalhar a porção superior do peitoral. Ajuste a altura conforme sua capacidade."
                            2 -> "Exercício desafiador para a porção inferior do peitoral."
                            3 -> "Para fortalecer o core e estabilização durante os exercícios de peito."
                            else -> null
                        },
                        order = index + 1
                    )
                }
            }
            
            if (workoutItems.size == exercises.size) {
                val workoutRequest = WorkoutTemplateRequest(
                    name = "Treino de Peito - Iniciante",
                    description = "Treino focado no desenvolvimento do peitoral, ideal para iniciantes. Inclui exercícios compostos e isolados para trabalhar todas as porções do músculo peitoral.",
                    items = workoutItems
                )
                
                workoutTemplateUseCase.createPublic(workoutRequest)
                logger.info("✅ Treino de Peito criado com sucesso!")
            } else {
                logger.warn("⚠️ Nem todos os exercícios foram encontrados para o treino de peito.")
            }
            
        } catch (e: Exception) {
            logger.error("❌ Erro ao criar treino de peito: ${e.message}")
        }
    }

    private fun createBackWorkout() {
        try {
            logger.info("Criando treino de costas...")
            
            val exercises = listOf(
                Triple("Levantamento terra com barra", "4", "6-8"),
                Triple("90/90 Hamstring", "3", "12-15"),
                Triple("Flexões de parada de mão", "3", "5-8"),
                Triple("3/4 Situp", "3", "15-20")
            )
            
            val workoutItems = exercises.mapIndexedNotNull { index, (exerciseName, sets, reps) ->
                getExerciseIdByName(exerciseName)?.let { exerciseId ->
                    WorkoutItemRequest(
                        exerciseId = exerciseId,
                        sets = sets,
                        reps = reps,
                        restSeconds = when (index) {
                            0, 1 -> 120  // Exercícios compostos principais
                            3 -> 60      // Exercício de core
                            else -> 90
                        },
                        observation = when (index) {
                            0 -> "Exercício rei das costas. Mantenha a coluna neutra e foque na técnica."
                            1 -> "Alongamento ativo para posteriores de coxa e mobilidade."
                            2 -> "Exercício avançado para ombros. Adapte conforme sua capacidade."
                            3 -> "Para fortalecer o core e estabilização do corpo."
                            else -> null
                        },
                        order = index + 1
                    )
                }
            }
            
            if (workoutItems.size == exercises.size) {
                val workoutRequest = WorkoutTemplateRequest(
                    name = "Treino de Costas - Iniciante",
                    description = "Treino completo para desenvolvimento das costas, focando na largura e espessura do músculo. Inclui exercícios para fortalecer também a postura.",
                    items = workoutItems
                )
                
                workoutTemplateUseCase.createPublic(workoutRequest)
                logger.info("✅ Treino de Costas criado com sucesso!")
            } else {
                logger.warn("⚠️ Nem todos os exercícios foram encontrados para o treino de costas.")
            }
            
        } catch (e: Exception) {
            logger.error("❌ Erro ao criar treino de costas: ${e.message}")
        }
    }

    private fun createLegsWorkout() {
        try {
            logger.info("Criando treino de pernas...")
            
            val exercises = listOf(
                Triple("Agachamento completo com barra", "4", "8-10"),
                Triple("Agachamento com peso corporal", "3", "15-20"),
                Triple("Levantamento terra com barra", "3", "6-8"),
                Triple("90/90 Hamstring", "3", "12-15")
            )
            
            val workoutItems = exercises.mapIndexedNotNull { index, (exerciseName, sets, reps) ->
                getExerciseIdByName(exerciseName)?.let { exerciseId ->
                    WorkoutItemRequest(
                        exerciseId = exerciseId,
                        sets = sets,
                        reps = reps,
                        restSeconds = when (index) {
                            0 -> 150  // Squat com barra
                            1 -> 90   // Bodyweight squat
                            2 -> 75   // Glute bridge
                            3 -> 30   // Ankle circles
                            else -> 90
                        },
                        observation = when (index) {
                            0 -> "Agachamento completo com barra. Desça até quebrar a paralela."
                            1 -> "Para finalizar e trabalhar resistência muscular."
                            2 -> "Deadlift também trabalha intensamente as pernas, especialmente posteriores."
                            3 -> "Alongamento ativo para posteriores de coxa e mobilidade."
                            else -> null
                        },
                        order = index + 1
                    )
                }
            }
            
            if (workoutItems.size == exercises.size) {
                val workoutRequest = WorkoutTemplateRequest(
                    name = "Treino de Pernas - Força",
                    description = "Treino intenso para desenvolvimento de força e massa muscular nas pernas.",
                    items = workoutItems
                )
                
                workoutTemplateUseCase.createPublic(workoutRequest)
                logger.info("✅ Treino de Pernas criado com sucesso!")
            } else {
                logger.warn("⚠️ Nem todos os exercícios foram encontrados para o treino de pernas.")
            }
            
        } catch (e: Exception) {
            logger.error("❌ Erro ao criar treino de pernas: ${e.message}")
        }
    }

    private fun createPushWorkout() {
        try {
            logger.info("Criando treino Push (Empurrar)...")
            
            val exercises = listOf(
                Triple("Supino reto com barra de apoio com empunhadura média", "4", "8-10"),
                Triple("Flexões de parada de mão", "3", "5-8"),
                Triple("Flexão Plyo", "3", "8-10"),
                Triple("Flexão em declive", "3", "12-15")
            )
            
            val workoutItems = exercises.mapIndexedNotNull { index, (exerciseName, sets, reps) ->
                getExerciseIdByName(exerciseName)?.let { exerciseId ->
                    WorkoutItemRequest(
                        exerciseId = exerciseId,
                        sets = sets,
                        reps = reps,
                        restSeconds = when (index) {
                            0 -> 120  // Bench Press
                            1 -> 90   // Push-ups normais
                            2 -> 90   // Decline push-up
                            3 -> 75   // Incline push-up
                            else -> 90
                        },
                        observation = when (index) {
                            0 -> "Movimento principal do treino. Foque na explosão na subida."
                            1 -> "Exercício avançado para ombros. Adapte conforme sua capacidade."
                            2 -> "Flexão pliométrica para explosão e potência."
                            3 -> "Para finalizar e esgotar as fibras musculares."
                            else -> null
                        },
                        order = index + 1
                    )
                }
            }
            
            if (workoutItems.size == exercises.size) {
                val workoutRequest = WorkoutTemplateRequest(
                    name = "Treino Push - Peito, Ombros e Tríceps",
                    description = "Treino completo dos músculos de empurrar: peitoral, ombros e tríceps.",
                    items = workoutItems
                )
                
                workoutTemplateUseCase.createPublic(workoutRequest)
                logger.info("✅ Treino Push criado com sucesso!")
            } else {
                logger.warn("⚠️ Nem todos os exercícios foram encontrados para o treino push.")
            }
            
        } catch (e: Exception) {
            logger.error("❌ Erro ao criar treino push: ${e.message}")
        }
    }
}
