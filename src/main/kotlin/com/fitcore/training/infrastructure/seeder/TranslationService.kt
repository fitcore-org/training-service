package com.fitcore.training.infrastructure.seeder

import com.deepl.api.Translator
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

/**
 * Serviço de tradução usando a API DeepL para traduzir conteúdo de exercícios
 * do inglês para português brasileiro de forma inteligente e modular.
 */
@Service
class TranslationService(
    @Value("\${deepl.api.key}") private val apiKey: String
) {
    private val logger = LoggerFactory.getLogger(javaClass)
    private val translator = Translator(apiKey)

    companion object {
        // Cache simples para evitar traduções desnecessárias
        private val translationCache = mutableMapOf<String, String>()
    }

    /**
     * Traduz texto do inglês para português brasileiro.
     * Implementa cache simples e tratamento de erros robusto.
     */
    fun translateToBrazilianPortuguese(text: String): String {
        // Retorna imediatamente se o texto está vazio ou é muito curto
        if (text.isBlank() || text.length < 3) return text

        // Verifica se já foi traduzido antes (cache)
        translationCache[text]?.let { cachedTranslation ->
            logger.debug("Usando tradução em cache para: ${text.take(50)}...")
            return cachedTranslation
        }

        return try {
            logger.debug("Traduzindo texto: ${text.take(100)}...")
            val result = translator.translateText(text, "en", "pt-BR")
            val translation = result.text
            
            // Armazena no cache para uso futuro
            translationCache[text] = translation
            
            logger.debug("Tradução concluída: ${translation.take(100)}...")
            translation
        } catch (e: Exception) {
            logger.warn("Erro na tradução da API DeepL: ${e.message}. Retornando texto original.")
            // Em caso de erro, retorna o texto original para não quebrar o processo
            text
        }
    }

    /**
     * Traduz nome do exercício de forma inteligente.
     * Remove underscores e capitaliza adequadamente antes da tradução.
     */
    fun translateExerciseName(name: String): String {
        if (name.isBlank()) return name

        // Converte formato GitHub (Barbell_Bench_Press_-_Medium_Grip) para texto legível
        val readableName = name
            .replace("_", " ")
            .replace("-", "")
            .trim()
            .split(" ")
            .joinToString(" ") { word -> 
                word.lowercase().replaceFirstChar { it.uppercase() }
            }

        return translateToBrazilianPortuguese(readableName)
    }

    /**
     * Traduz lista de instruções de exercício.
     * Cada instrução é traduzida individualmente para melhor contexto.
     */
    fun translateInstructions(instructions: List<String>): String {
        if (instructions.isEmpty()) return ""

        val translatedInstructions = instructions.mapIndexed { index, instruction ->
            if (instruction.isBlank()) return@mapIndexed instruction
            
            logger.debug("Traduzindo instrução ${index + 1}/${instructions.size}")
            val translated = translateToBrazilianPortuguese(instruction)
            
            // Adiciona numeração para instruções múltiplas
            if (instructions.size > 1) {
                "${index + 1}. $translated"
            } else {
                translated
            }
        }

        return translatedInstructions.joinToString("\n")
    }

    /**
     * Traduz grupos musculares de forma inteligente.
     * Mantém consistência na tradução de termos anatômicos.
     */
    fun translateMuscleGroups(muscleGroups: List<String>): String {
        if (muscleGroups.isEmpty()) return ""

        // Mapeamento específico para grupos musculares comuns
        val muscleGroupMappings = mapOf(
            "quadriceps" to "quadríceps",
            "hamstrings" to "posteriores de coxa",
            "glutes" to "glúteos",
            "calves" to "panturrilhas",
            "chest" to "peitoral",
            "shoulders" to "ombros",
            "triceps" to "tríceps",
            "biceps" to "bíceps",
            "lats" to "dorsais",
            "abs" to "abdominais",
            "core" to "core",
            "back" to "costas"
        )

        val translatedGroups = muscleGroups.map { group ->
            val lowerGroup = group.lowercase()
            muscleGroupMappings[lowerGroup] ?: translateToBrazilianPortuguese(group)
        }

        return translatedGroups.joinToString(", ")
    }

    /**
     * Traduz equipamento de forma padronizada.
     */
    fun translateEquipment(equipment: String?): String {
        if (equipment.isNullOrBlank()) return "Não especificado"

        // Mapeamento específico para equipamentos comuns
        val equipmentMappings = mapOf(
            "barbell" to "barra",
            "dumbbell" to "halter",
            "body weight" to "peso corporal",
            "bodyweight" to "peso corporal",
            "none" to "nenhum",
            "cable" to "cabo",
            "machine" to "máquina",
            "kettlebell" to "kettlebell",
            "resistance band" to "faixa elástica",
            "pull-up bar" to "barra de pull-up"
        )

        val lowerEquipment = equipment.lowercase()
        return equipmentMappings[lowerEquipment] ?: translateToBrazilianPortuguese(equipment)
    }

    /**
     * Limpa o cache de traduções (útil para testes ou para liberar memória)
     */
    fun clearCache() {
        translationCache.clear()
        logger.info("Cache de traduções limpo")
    }

    /**
     * Retorna estatísticas do cache para monitoramento
     */
    fun getCacheStats(): Map<String, Any> {
        return mapOf(
            "cacheSize" to translationCache.size,
            "cachedItems" to translationCache.keys.take(5) // Primeiros 5 para debug
        )
    }
}
