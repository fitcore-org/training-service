# API para Criação de Treinos Privados

## Endpoint
`POST /api/v1/workouts/private`

## Exemplo de Request Body

```json
{
  "name": "Treino Personalizado - João",
  "description": "Treino específico para o aluno João focado em força e resistência",
  "studentIds": [
    "123e4567-e89b-12d3-a456-426614174001",
    "123e4567-e89b-12d3-a456-426614174002"
  ],
  "items": [
    {
      "exerciseId": "d1a2b3c4-e5f6-7890-abcd-ef1234567890",
      "sets": "3",
      "reps": "8-10",
      "restSeconds": 120,
      "observation": "Foque na técnica e controle do movimento",
      "order": 1
    },
    {
      "exerciseId": "f2b3c4d5-e6f7-8901-bcde-f23456789012",
      "sets": "3",
      "reps": "12-15",
      "restSeconds": 90,
      "observation": "Mantenha a tensão no músculo durante toda a execução",
      "order": 2
    }
  ]
}
```

## Response
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "name": "Treino Personalizado - João",
  "description": "Treino específico para o aluno João focado em força e resistência",
  "isPublic": false,
  "studentIds": [
    "123e4567-e89b-12d3-a456-426614174001",
    "123e4567-e89b-12d3-a456-426614174002"
  ],
  "items": [
    {
      "id": "a1b2c3d4-e5f6-7890-1234-567890abcdef",
      "exerciseId": "d1a2b3c4-e5f6-7890-abcd-ef1234567890",
      "sets": "3",
      "reps": "8-10",
      "restSeconds": 120,
      "observation": "Foque na técnica e controle do movimento",
      "order": 1
    },
    {
      "id": "b2c3d4e5-f6g7-8901-2345-678901bcdefg",
      "exerciseId": "f2b3c4d5-e6f7-8901-bcde-f23456789012",
      "sets": "3",
      "reps": "12-15",
      "restSeconds": 90,
      "observation": "Mantenha a tensão no músculo durante toda a execução",
      "order": 2
    }
  ]
}
```

## Validações Implementadas

1. **Exercícios devem existir**: Todos os exercícios referenciados nos `exerciseId` devem existir no banco
2. **Treinos privados devem ter estudantes**: Treinos com `isPublic: false` devem ter pelo menos um `studentId`
3. **Nome obrigatório**: O nome do treino não pode ser vazio
4. **Pelo menos um exercício**: Um treino deve ter pelo menos um item

## Status Codes

- `201 Created`: Treino criado com sucesso
- `400 Bad Request`: Dados inválidos ou exercício não encontrado
- `422 Unprocessable Entity`: Violação de regras de negócio
