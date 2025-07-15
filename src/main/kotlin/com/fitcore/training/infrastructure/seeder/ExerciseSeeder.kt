package com.fitcore.training.infrastructure.seeder

import com.fasterxml.jackson.databind.ObjectMapper
import com.fitcore.training.application.exercise.dto.ExerciseRequest
import com.fitcore.training.application.exercise.port.`in`.ExerciseUseCase
import com.fitcore.training.domain.exercise.port.out.ExerciseRepositoryPort
import com.fitcore.training.infrastructure.minio.adapter.MinioAdapter
import com.fitcore.training.infrastructure.seeder.dto.ExerciseSourceDTO
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.core.annotation.Order
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Component
import java.net.URL

@Component
@Order(1) // Executa primeiro, antes do WorkoutTemplateSeeder
class ExerciseSeeder(
    private val exerciseUseCase: ExerciseUseCase,
    private val repositoryPort: ExerciseRepositoryPort,
    private val minioAdapter: MinioAdapter,
    private val objectMapper: ObjectMapper
) : CommandLineRunner {
    private val logger = LoggerFactory.getLogger(javaClass)

    // --- SUA LISTA DE EXERCÍCIOS DESEJADOS ---
    // Adicione ou remova os IDs dos exercícios que você quer aqui.
    private val desiredExerciseIds = setOf(
        "Barbell_Bench_Press_-_Medium_Grip",
        "Barbell_Deadlift", 
        "Barbell_Full_Squat",
        "Bodyweight_Squat",
        "Decline_Push-Up",
        "Incline_Push-Up",
        "Plyo_Push-up",
        "3_4_Sit-Up",
        "90_90_Hamstring",
        "Handstand_Push-Ups"
        // ... adicione quantos mais quiser
    )

    companion object {
        private const val GITHUB_IMAGE_BASE_URL = "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/"
    }

    override fun run(vararg args: String?) {
        if (repositoryPort.findAll().isNotEmpty()) {
            logger.info("Banco de dados já populado. Seeder não será executado.")
            return
        }

        logger.info("--- Iniciando Seeder Seletivo de Exercícios ---")

        try {
            val jsonResource = ClassPathResource("seed/exercises_source.json")
            if (!jsonResource.exists()) {
                logger.warn("Arquivo exercises_source.json não encontrado. Executando seeder básico...")
                runBasicSeeder()
                return
            }

            val allExercises: List<ExerciseSourceDTO> = objectMapper.readValue(jsonResource.inputStream, objectMapper.typeFactory.constructCollectionType(List::class.java, ExerciseSourceDTO::class.java))

            // --- A MÁGICA ACONTECE AQUI ---
            // Filtramos a lista imensa para conter apenas os exercícios que queremos.
            val exercisesToSeed = allExercises.filter { it.id in desiredExerciseIds }

            logger.info("${exercisesToSeed.size} de ${desiredExerciseIds.size} exercícios desejados foram encontrados na fonte. Iniciando ingestão...")

            exercisesToSeed.forEach { source ->
                val exerciseName = source.name
                try {
                    val uploadedUrls = mutableListOf<String>()
                    source.images.forEach { imagePath ->
                        val fullImageUrl = GITHUB_IMAGE_BASE_URL + imagePath
                        val imageInputStream = URL(fullImageUrl).openStream()
                        val fileName = imagePath.replace("/", "_")
                        val minioUrl = minioAdapter.upload(fileName, imageInputStream)
                        uploadedUrls.add(minioUrl)
                    }

                    val request = ExerciseRequest(
                        name = exerciseName,
                        description = source.instructions.joinToString("\n"),
                        muscleGroup = source.primaryMuscles.joinToString(", "),
                        equipment = source.equipment ?: "Não especificado",
                        mediaUrl = uploadedUrls.firstOrNull() // Pega a primeira imagem como URL principal
                    )
                    exerciseUseCase.create(request)
                    logger.info("Exercício '$exerciseName' salvo com ${uploadedUrls.size} imagens.")

                } catch (e: Exception) {
                    logger.error("ERRO ao processar o exercício '$exerciseName': ${e.message}")
                }
            }
            logger.info("--- Seeder Seletivo Finalizado ---")
        } catch (e: Exception) {
            logger.error("Erro no seeder seletivo: ${e.message}. Executando seeder básico como fallback...")
            runBasicSeeder()
        }
    }

    private fun runBasicSeeder() {
        logger.info("--- Iniciando Seeder Básico de Exercícios ---")
        seedExercise("Supino Reto", "Peitoral", "Barra", "supino-reto.jpg")
        seedExercise("Agachamento", "Quadríceps", "Peso Corporal", "agachamento.jpg")
        seedExercise("Flexão de Braço", "Peitoral", "Peso Corporal", "flexao.jpg")
        seedExercise("Remada Curvada", "Dorsal", "Barra", "remada-curvada.jpg")
        seedExercise("Desenvolvimento", "Ombros", "Halteres", "desenvolvimento.jpg")
        logger.info("--- Seeder Básico Finalizado ---")
    }

    private fun seedExercise(name: String, muscleGroup: String, equipment: String, jpgFileName: String) {
        if (repositoryPort.findByName(name) == null) {
            logger.info("Processando exercício: $name")
            try {
                val resource = ClassPathResource("seed/jpgs/$jpgFileName")
                
                // Verifica se o arquivo existe antes de tentar fazer upload
                if (resource.exists()) {
                    val mediaUrl = minioAdapter.upload(jpgFileName, resource.inputStream)
                    val request = ExerciseRequest(
                        name = name, 
                        description = "Descrição detalhada do exercício $name",
                        muscleGroup = muscleGroup, 
                        equipment = equipment, 
                        mediaUrl = mediaUrl
                    )
                    exerciseUseCase.create(request)
                    logger.info("'$name' salvo com sucesso. URL: $mediaUrl")
                } else {
                    logger.warn("Arquivo JPG não encontrado: $jpgFileName. Criando exercício sem mídia.")
                    val request = ExerciseRequest(
                        name = name, 
                        description = "Descrição detalhada do exercício $name",
                        muscleGroup = muscleGroup, 
                        equipment = equipment, 
                        mediaUrl = null
                    )
                    exerciseUseCase.create(request)
                    logger.info("'$name' salvo com sucesso sem mídia.")
                }
            } catch (e: Exception) {
                logger.error("ERRO ao processar '$name': ${e.message}", e)
            }
        } else {
            logger.info("Exercício '$name' já existe no banco de dados. Pulando...")
        }
    }
}
