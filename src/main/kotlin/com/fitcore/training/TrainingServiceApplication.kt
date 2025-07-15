package com.fitcore.training

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class TrainingServiceApplication

fun main(args: Array<String>) {
	runApplication<TrainingServiceApplication>(*args)
}
