package com.fitcore.training.infrastructure.seeder

import com.fasterxml.jackson.databind.ObjectMapper
import com.fitcore.training.application.exercise.dto.ExerciseRequest
import com.fitcore.training.application.exercise.port.`in`.ExerciseUseCase
import com.fitcore.training.domain.exercise.port.out.ExerciseRepositoryPort
import com.fitcore.training.domain.workouttemplate.port.out.WorkoutTemplateRepositoryPort
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
    private val workoutRepositoryPort: WorkoutTemplateRepositoryPort,
    private val minioAdapter: MinioAdapter,
    private val objectMapper: ObjectMapper,
    private val translationService: TranslationService
) : CommandLineRunner {
    private val logger = LoggerFactory.getLogger(javaClass)

    // --- CONTROLE DE TRADUÇÃO ---
    // Alterar para 'false' para desativar tradução e economizar API calls
    private val enableTranslation = false

    // --- LISTA DE EXERCÍCIOS DESEJADOS ---
    // Adicionar ou remover os IDs dos exercícios que você quer aqui.
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
        // Verifica se deve limpar o banco antes de popular (parâmetro --clear-db)
        val shouldClearDatabase = args.contains("--clear-db")
        
        if (shouldClearDatabase) {
            logger.info("🗑️ Limpando banco de dados de exercícios e bucket MinIO...")
            clearDatabaseAndBucket()
        }

        if (!shouldClearDatabase && repositoryPort.findAll().isNotEmpty()) {
            logger.info("Banco de dados já populado. Verificando se precisamos atualizar imagens...")
            checkAndUpdateExistingExercises()
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

            // Filtramos a lista imensa para conter apenas os exercícios que queremos.
            val exercisesToSeed = allExercises.filter { it.id in desiredExerciseIds }

            logger.info("${exercisesToSeed.size} de ${desiredExerciseIds.size} exercícios desejados foram encontrados na fonte. Iniciando ingestão...")

            exercisesToSeed.forEach { source ->
                try {
                    logger.info("🌍 Processando exercício: ${source.name}")
                    
                    // Traduz apenas se a flag estiver habilitada
                    val translatedName = if (enableTranslation) {
                        translationService.translateExerciseName(source.name)
                    } else {
                        source.name // Usa o nome original em inglês
                    }
                    
                    val translatedDescription = if (enableTranslation) {
                        translationService.translateInstructions(source.instructions)
                    } else {
                        source.instructions.joinToString(". ") // Junta as instruções em inglês
                    }
                    
                    val translatedMuscleGroup = if (enableTranslation) {
                        translationService.translateMuscleGroups(source.primaryMuscles)
                    } else {
                        source.primaryMuscles.joinToString(", ") // Junta os músculos em inglês
                    }
                    
                    val translatedEquipment = if (enableTranslation) {
                        translationService.translateEquipment(source.equipment)
                    } else {
                        source.equipment ?: "Unknown" // Usa o equipamento em inglês
                    }

                    if (enableTranslation) {
                        logger.info("✅ Traduzido: '$translatedName' - Músculos: '$translatedMuscleGroup'")
                    } else {
                        logger.info("✅ Processado (sem tradução): '$translatedName' - Músculos: '$translatedMuscleGroup'")
                    }

                    val uploadedUrls = mutableListOf<String>()
                    source.images.forEach { imagePath ->
                        val fullImageUrl = GITHUB_IMAGE_BASE_URL + imagePath
                        val imageInputStream = URL(fullImageUrl).openStream()
                        val fileName = imagePath.replace("/", "_")
                        val minioUrl = minioAdapter.upload(fileName, imageInputStream)
                        uploadedUrls.add(minioUrl)
                    }

                    val request = ExerciseRequest(
                        name = translatedName,
                        description = translatedDescription,
                        muscleGroup = translatedMuscleGroup,
                        equipment = translatedEquipment,
                        mediaUrl = uploadedUrls.getOrNull(0), // Primeira imagem
                        mediaUrl2 = uploadedUrls.getOrNull(1) // Segunda imagem
                    )
                    exerciseUseCase.create(request)
                    logger.info("💾 Exercício '$translatedName' salvo com ${uploadedUrls.size} imagens.")

                } catch (e: Exception) {
                    logger.error("❌ ERRO ao processar o exercício '${source.name}': ${e.message}")
                }
            }
            logger.info("--- Seeder Seletivo Finalizado ---")
            
            // Exibe estatísticas de tradução apenas se a tradução estiver habilitada
            if (enableTranslation) {
                val cacheStats = translationService.getCacheStats()
                logger.info("📊 Estatísticas de Tradução: ${cacheStats["cacheSize"]} traduções realizadas")
            } else {
                logger.info("📊 Tradução desabilitada - nenhuma API call foi feita")
            }
            
        } catch (e: Exception) {
            logger.error("Erro no seeder seletivo: ${e.message}. Executando seeder básico como fallback...")
            runBasicSeeder()
        }
    }

    private fun runBasicSeeder() {
        logger.info("--- Iniciando Seeder Básico de Exercícios (já em português) ---")
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
                        mediaUrl = mediaUrl,
                        mediaUrl2 = null // Seeder básico só tem uma imagem
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
                        mediaUrl = null,
                        mediaUrl2 = null
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

    /**
     * Limpa o banco de dados de exercícios respeitando foreign keys
     */
    private fun clearDatabaseAndBucket() {
        try {
            logger.info("🗑️ Iniciando limpeza do banco de dados...")
            
            // 1. Primeiro remove os workouts (que referenciam exercícios)
            logger.info("🗑️ Removendo todos os workout templates...")
            workoutRepositoryPort.deleteAll()
            logger.info("✅ Workout templates removidos.")
            
            // 2. Depois remove os exercícios
            logger.info("🗑️ Removendo todos os exercícios...")
            repositoryPort.deleteAll()
            logger.info("✅ Exercícios removidos.")

            logger.info("⚠️ Lembre-se de limpar o bucket MinIO manualmente se necessário.")
            logger.info("🧹 Limpeza do banco de dados concluída. Pronto para nova população.")
        } catch (e: Exception) {
            logger.error("❌ Erro durante limpeza: ${e.message}")
            throw e
        }
    }

    /**
     * Verifica e atualiza exercícios existentes que não têm mediaUrl2 mas deveriam ter
     */
    private fun checkAndUpdateExistingExercises() {
        try {
            logger.info("--- Verificando Exercícios Existentes para Atualização ---")
            
            val existingExercises = repositoryPort.findAll()
            val exercisesNeedingUpdate = existingExercises.filter { exercise ->
                // Exercícios que não têm mediaUrl2 mas vieram do GitHub (padrão conhecido)
                exercise.mediaUrl2 == null && 
                exercise.mediaUrl != null && 
                (exercise.mediaUrl!!.contains("0.jpg") || exercise.mediaUrl!!.contains("_0.jpg"))
            }

            if (exercisesNeedingUpdate.isEmpty()) {
                logger.info("Todos os exercícios já estão atualizados com as duas imagens.")
                return
            }

            logger.info("Encontrados ${exercisesNeedingUpdate.size} exercícios que precisam de atualização.")

            // Carregar os dados originais do JSON para reprocessar
            val jsonResource = ClassPathResource("seed/exercises_source.json")
            if (!jsonResource.exists()) {
                logger.warn("Arquivo exercises_source.json não encontrado. Não é possível atualizar exercícios existentes.")
                return
            }

            val allExercises: List<ExerciseSourceDTO> = objectMapper.readValue(
                jsonResource.inputStream, 
                objectMapper.typeFactory.constructCollectionType(List::class.java, ExerciseSourceDTO::class.java)
            )

            exercisesNeedingUpdate.forEach { exercise ->
                try {
                    // Encontrar o exercício original no JSON
                    val sourceExercise = allExercises.find { it.name == exercise.name }
                    
                    if (sourceExercise != null && sourceExercise.images.size >= 2) {
                        logger.info("Atualizando exercício '${exercise.name}' com segunda imagem...")
                        
                        // Upload da segunda imagem
                        val secondImagePath = sourceExercise.images[1]
                        val fullImageUrl = GITHUB_IMAGE_BASE_URL + secondImagePath
                        val imageInputStream = URL(fullImageUrl).openStream()
                        val fileName = secondImagePath.replace("/", "_")
                        val secondImageUrl = minioAdapter.upload(fileName, imageInputStream)
                        
                        // Atualizar o exercício
                        exercise.mediaUrl2 = secondImageUrl
                        repositoryPort.save(exercise)
                        
                        logger.info("✅ Exercício '${exercise.name}' atualizado com segunda imagem: $secondImageUrl")
                    } else {
                        logger.info("⚠️ Exercício '${exercise.name}' não encontrado no source ou só tem uma imagem.")
                    }
                } catch (e: Exception) {
                    logger.error("❌ Erro ao atualizar exercício '${exercise.name}': ${e.message}")
                }
            }
            
            logger.info("--- Atualização de Exercícios Finalizada ---")
            
        } catch (e: Exception) {
            logger.error("Erro durante verificação/atualização de exercícios existentes: ${e.message}")
        }
    }
}
