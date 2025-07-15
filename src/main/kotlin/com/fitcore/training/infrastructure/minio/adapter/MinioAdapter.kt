package com.fitcore.training.infrastructure.minio.adapter

import com.fitcore.training.domain.exercise.port.out.FileStoragePort
import com.fitcore.training.infrastructure.minio.service.StorageService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.io.InputStream

@Component
class MinioAdapter(
    private val storageService: StorageService
) : FileStoragePort {

    private val logger = LoggerFactory.getLogger(javaClass)

    override fun upload(fileName: String, inputStream: InputStream): String {
        try {
            logger.info("Iniciando upload do arquivo: $fileName")
            val objectKey = storageService.uploadExerciseGif(fileName, inputStream)
            val publicUrl = storageService.getPublicUrl(objectKey)
            logger.info("Upload concluído com sucesso. URL: $publicUrl")
            return publicUrl
        } catch (e: Exception) {
            logger.error("Erro ao fazer upload do arquivo '$fileName': ${e.message}", e)
            throw RuntimeException("Falha no upload do arquivo", e)
        }
    }

    override fun delete(fileName: String): Boolean {
        try {
            storageService.deleteExerciseGif(fileName)
            logger.info("Arquivo '$fileName' deletado com sucesso do MinIO")
            return true
        } catch (e: Exception) {
            logger.error("Erro ao deletar arquivo '$fileName': ${e.message}", e)
            return false
        }
    }

    override fun getUrl(fileName: String): String {
        return storageService.getPublicUrl(fileName)
    }
}
