package com.fitcore.training.infrastructure.minio.service

import io.minio.MinioClient
import io.minio.PutObjectArgs
import io.minio.RemoveObjectArgs  
import org.springframework.stereotype.Service
import org.springframework.beans.factory.annotation.Value
import com.fitcore.training.infrastructure.minio.config.MinioConfig
import org.slf4j.LoggerFactory
import java.io.InputStream
import java.util.UUID

@Service
class StorageService(
    private val client: MinioClient,
    private val minioConfig: MinioConfig,
    @Value("\${minio.bucket}") private val bucket: String
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun uploadExerciseGif(fileName: String, inputStream: InputStream): String {
        val objectName = "exercises/${UUID.randomUUID()}-$fileName"
        logger.info("Uploading file to MinIO, bucket: $bucket, object: $objectName")
        
        client.putObject(
            PutObjectArgs.builder()
                .bucket(bucket)
                .`object`(objectName)
                .stream(inputStream, -1, 10485760) // 10MB max size
                .contentType("image/gif")
                .build()
        )
        logger.info("Upload successful, returning key: $objectName")
        return objectName
    }

    fun getPublicUrl(objectName: String): String {
        // Construir URL pública direta em vez de URL assinada
        val baseUrl = minioConfig.externalUrl ?: minioConfig.url
        val publicUrl = "$baseUrl/$bucket/$objectName"
        
        logger.info("Generated public URL: $publicUrl")
        return publicUrl
    }
    
    fun deleteExerciseGif(objectKey: String) {
        logger.info("Deleting object from MinIO: $objectKey")
        client.removeObject(
            RemoveObjectArgs.builder()
                .bucket(bucket)
                .`object`(objectKey)
                .build()
        )
        logger.info("Successfully deleted object: $objectKey")
    }
}
