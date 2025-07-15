package com.fitcore.training.domain.exercise.port.out

import java.io.InputStream

// Contrato que o domínio exige para armazenamento de arquivos. "Preciso que alguém implemente isso."
interface FileStoragePort {
    fun upload(fileName: String, inputStream: InputStream): String
    fun delete(fileName: String): Boolean
    fun getUrl(fileName: String): String
}
