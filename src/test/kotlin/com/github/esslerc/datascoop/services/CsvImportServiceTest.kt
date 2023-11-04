package com.github.esslerc.datascoop.services

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import java.io.File
import java.nio.file.Paths

class CsvImportServiceTest {

    @Test
    fun validateFile() {
        val tempFile = File.createTempFile("temp", ".csv")

        val actual = CsvImportService.validateFile(tempFile)

        assertEquals(true, actual)

        tempFile.delete()
    }

    @Test
    fun validateFile_wrongSuffix() {
        val tempFile = File.createTempFile("temp", ".txt")

        val actual = CsvImportService.validateFile(tempFile)

        assertEquals(false, actual)

        tempFile.delete()
    }



    @Test
    fun validateFile_noFile() {
        val actual = CsvImportService.validateFile(Paths.get("unkown").toFile())

        assertEquals(false, actual)
    }


}