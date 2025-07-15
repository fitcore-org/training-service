package com.fitcore.training.application.exception

/**
 * Exception thrown when an exercise is not found in the system
 */
class ExerciseNotFoundException(message: String) : RuntimeException(message)

/**
 * Exception thrown when a workout template is not found in the system
 */
class WorkoutTemplateNotFoundException(message: String) : RuntimeException(message)

/**
 * Exception thrown when an invalid exercise is referenced in a workout
 */
class InvalidExerciseReferenceException(message: String) : RuntimeException(message)

/**
 * Exception thrown when workout validation fails
 */
class WorkoutValidationException(message: String) : RuntimeException(message)
