package com.github.esslerc.datascoop.services

import com.github.esslerc.datascoop.domain.DBInfo
import com.github.esslerc.datascoop.domain.Datasource
import com.github.esslerc.datascoop.domain.ImportPreset
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter


class CsvImportServiceTest {

    private lateinit var csvImportService: CsvImportService
    private lateinit var databaseService: PGDatabaseService
    private lateinit var tempFile: File

    @BeforeEach
    fun setUp() {
        databaseService = mock<PGDatabaseService> {}
        csvImportService = CsvImportService(databaseService)

        tempFile = File.createTempFile("test", "csv")
    }

    @AfterEach
    fun tearDown() {
        tempFile.delete()
    }

    @Test
    fun testImportMethod() {
        val importPreset = createMockImportPreset()
        csvImportService.import(importPreset)

        verify(databaseService, times(1)).dropTable(any())
        verify(databaseService, times(1)).createTable(any(), any())
        verify(databaseService, times(importPreset.datasources.size)).insert(any(), any(), any()
        )
    }

    private fun createMockImportPreset(): ImportPreset {

        val content = """
            Name;Age;City;Email
            Homer;38;Springfield;homer@localhost
            Marge;34;Springfield;marge@localhost
            Bart;10;Springfield;bart@localhost
            Lisa;8;Springfield;lisa@localhost
        """.trimIndent()


        BufferedWriter(FileWriter(tempFile)).use {
            it.write(content)
        }

        return ImportPreset(
            mutableListOf(
                Datasource(
                    csvFiles = mutableListOf(tempFile),
                    csvMapping = mutableMapOf(
                        "Name" to "text",
                        "Age" to "integer",
                        "City" to "text",
                        "Email" to "text",
                    ),
                )
            ),
            DBInfo(
                jdbcUrl = "",
                username = "",
                password = "",
                tableName = "",
            )
        )
    }


}