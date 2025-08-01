package com.fitcore.training.domain.workouttemplate.model

import java.util.UUID

// O Aggregate Root
class WorkoutTemplate(
    val id: UUID = UUID.randomUUID(),
    var name: String,
    var description: String?,
    val isPublic: Boolean = true,
    items: List<WorkoutItem>, // Recebe os itens na construção
    studentIds: List<UUID> = emptyList() // IDs dos estudantes para treinos privados
) {
    // Garante que a lista de itens seja privada e imutável por fora
    private val _items: MutableList<WorkoutItem> = items.toMutableList()
    val items: List<WorkoutItem>
        get() = _items.toList()
        
    // Garante que a lista de estudantes seja privada e imutável por fora
    private val _studentIds: MutableList<UUID> = studentIds.toMutableList()
    val studentIds: List<UUID>
        get() = _studentIds.toList()

    init {
        require(name.isNotBlank()) { "O nome do treino não pode ser vazio." }
        require(items.isNotEmpty()) { "Um treino deve ter pelo menos um exercício." }
        
        // Validação de regra de negócio: treinos privados devem ter pelo menos um estudante
        if (!isPublic) {
            require(studentIds.isNotEmpty()) { "Treinos privados devem ter pelo menos um estudante associado." }
        }
    }
}
