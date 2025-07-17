# 🌍 Sistema de Tradução de Exercícios - FitCore Training Service

## Visão Geral

O `TranslationService` foi implementado para traduzir automaticamente os dados de exercícios do repositório [free-exercise-db](https://github.com/yuhonas/free-exercise-db) do inglês para português brasileiro usando a API DeepL.

## 🚀 Configuração

### 1. API Key DeepL

1. Crie uma conta no [DeepL API](https://www.deepl.com/pro-api)
2. Obtenha sua API key
3. Configure no `application.yml`:

```yaml
deepl:
  api:
    key: SUA_CHAVE_DEEPL_AQUI
```

### 2. Dependências

A dependência `com.deepl.api:deepl-java:1.5.0` já foi adicionada ao `build.gradle.kts`.

## 🔧 Como Usar

### Limpeza e Re-população com Tradução

Para limpar o banco e repopular com dados traduzidos:

```bash
# Opção 1: Via parâmetro do seeder
./gradlew bootRun --args="--clear-db"

# Opção 2: Via migration + restart
./gradlew flywayMigrate
./gradlew bootRun
```

### Execução Normal

Se o banco estiver vazio, o seeder executará automaticamente com tradução:

```bash
./gradlew bootRun
```

## 🧠 Funcionalidades Inteligentes

### 1. **Tradução Seletiva**
- ✅ **Traduz**: nomes, descrições, grupos musculares, equipamentos
- ❌ **NÃO traduz**: IDs, nomes de arquivos, URLs de imagens

### 2. **Cache de Tradução**
- Evita traduções duplicadas
- Melhora performance
- Reduz custos da API

### 3. **Mapeamentos Específicos**
- Grupos musculares: `quadriceps` → `quadríceps`
- Equipamentos: `barbell` → `barra`
- Termos anatômicos padronizados

### 4. **Tratamento de Erros Robusto**
- Em caso de falha na API, retorna texto original
- Não quebra o processo de seeding
- Logs detalhados para debug

## 📝 Exemplos de Tradução

### Antes (Inglês)
```
Name: "Barbell_Bench_Press_-_Medium_Grip"
Instructions: ["Lie back on a flat bench...", "Using a medium width grip..."]
Primary Muscles: ["chest", "shoulders", "triceps"]
Equipment: "barbell"
```

### Depois (Português)
```
Name: "Supino com Barra - Pegada Média"
Instructions: "1. Deite-se em um banco reto...\n2. Usando uma pegada de largura média..."
Primary Muscles: "peitoral, ombros, tríceps"
Equipment: "barra"
```

## 🔍 Logs e Monitoramento

O sistema fornece logs detalhados:

```
🌍 Processando e traduzindo exercício: Barbell_Bench_Press
✅ Traduzido: 'Supino com Barra' - Músculos: 'peitoral, ombros, tríceps'
💾 Exercício 'Supino com Barra' salvo com 2 imagens.
📊 Estatísticas de Tradução: 45 traduções realizadas
```

## ⚠️ Considerações Importantes

### 1. **Custos da API**
- DeepL cobra por caractere traduzido
- Cache reduz custos evitando traduções duplicadas
- Plano gratuito: 500.000 caracteres/mês

### 2. **Rate Limits**
- API tem limites de requisições por segundo
- Sistema implementa retry automático em caso de erro

### 3. **Limpeza de Dados**
- Use `--clear-db` apenas quando necessário
- Backup seus dados antes de limpar
- Migration V5 está disponível para limpeza via Flyway

## 🛠️ Desenvolvimento

### Testando Traduções

```kotlin
// Via service direto (para testes)
val translationService = TranslationService("sua-api-key")
val translated = translationService.translateExerciseName("Barbell_Bench_Press")
println(translated) // "Supino com Barra"
```

### Estatísticas do Cache

```kotlin
val stats = translationService.getCacheStats()
println("Traduções em cache: ${stats["cacheSize"]}")
```

### Limpeza do Cache

```kotlin
translationService.clearCache()
```

## 📁 Estrutura de Arquivos

```
src/main/kotlin/com/fitcore/training/infrastructure/seeder/
├── TranslationService.kt          # Serviço principal de tradução
├── ExerciseSeeder.kt             # Seeder modificado com tradução
└── dto/ExerciseSourceDTO.kt      # DTO dos dados originais

src/main/resources/
├── application.yml               # Configuração da API key
└── db/migration/
    └── V5__clean_exercises_and_workouts_for_translation.sql
```

## 🚨 Troubleshooting

### Erro de API Key
```
Erro na tradução da API DeepL: Authentication failed. Retornando texto original.
```
**Solução**: Verifique se a API key está correta no `application.yml`

### Limite de Cota Excedido
```
Erro na tradução da API DeepL: Quota exceeded. Retornando texto original.
```
**Solução**: Verifique sua cota no painel DeepL ou aguarde o reset mensal

### Dados Já Existem
```
Banco de dados já populado. Verificando se precisamos atualizar imagens...
```
**Solução**: Use `--clear-db` para limpar e repopular com tradução

---

## 🎯 Próximos Passos

1. **Implementar tradução incremental** para novos exercícios
2. **Adicionar suporte a outros idiomas** (espanhol, francês)
3. **Cache persistente** (Redis/Database) para múltiplas execuções
4. **Interface admin** para gerenciar traduções manualmente
