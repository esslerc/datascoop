package com.github.esslerc.datascoop.services

import com.fasterxml.jackson.databind.MappingIterator
import com.fasterxml.jackson.dataformat.csv.CsvMapper
import com.fasterxml.jackson.dataformat.csv.CsvSchema
import com.fasterxml.jackson.dataformat.csv.CsvSchema.ColumnType
import org.apache.commons.io.input.BOMInputStream
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import java.nio.file.Files

class CsvImportService(
    private val jsonMappingService: JSONMappingService,
    private val databaseService: PGDatabaseService
) {

    fun import(mappingFile: File) {
        val importPreset = jsonMappingService.loadJSON(mappingFile)

        val databaseClient = databaseService.getClient(importPreset.databaseConnectionInfo)

        importPreset.datasources.forEach { datasource ->
            importDataFromDatasource(datasource, databaseClient)
        }
    }

    private fun importDataFromDatasource(datasource: Datasource, databaseClient: Unit) {
        val filesToImport = datasource.csvFiles

        filesToImport.forEach { csvFile ->
            val csvMapper = CsvMapper()

            val csvMapping = datasource.csvMapping
            val csvEncoding = datasource.csvEncoding

            val csvSchemaBuilder = CsvSchema.builder()

            csvMapping.forEach { (columnName, columnType) ->
                csvSchemaBuilder.addColumn(columnName, getJacksonColumnType(columnType))
            }
            csvSchemaBuilder.setColumnSeparator(datasource.csvSeparator)
            csvSchemaBuilder.setSkipFirstDataRow(datasource.csvHeader)
            csvSchemaBuilder.setUseHeader(false)

            val csvSchema = csvSchemaBuilder.build()

            InputStreamReader(
                BOMInputStream
                    .builder()
                    .setCharset(csvEncoding)
                    .setInputStream(FileInputStream(csvFile))
                    .get()
            ).use { inputStreamReader ->
                val mappingIterator: MappingIterator<Map<String, String>> =
                    csvMapper.readerFor(Map::class.java)
                        .with(csvSchema)
                        .readValues(inputStreamReader)

                while (mappingIterator.hasNext()) {
                    val row = mappingIterator.next()
                    logger.info(row.toString())
                    // hier muss was mit databaseClient passieren
                }
            }

        }
    }

    private fun getJacksonColumnType(csvColumnType: String) =
        when(csvColumnType.lowercase()) {
            "string" -> ColumnType.STRING
            "int" -> ColumnType.NUMBER
            "boolean" -> ColumnType.BOOLEAN
            "double" -> ColumnType.NUMBER
            else -> throw IllegalArgumentException("Unkown csvColumnType $csvColumnType")
        }


    companion object {
        private val logger: Logger = LoggerFactory.getLogger(CsvImportService::class.java)

        fun validateFile(file: File, suffix: String = "csv") : Boolean {
            val fileExists = file.exists()
            logger.info("Check file exists: $fileExists")

            val isRegularFile = Files.isRegularFile(file.toPath())
            logger.info("Check is regular file: $isRegularFile")

            val isValidSuffix = file.extension.lowercase() == suffix.lowercase()
            logger.info("Check is valid suffix: $isRegularFile")

            return fileExists && isRegularFile && isValidSuffix
        }
    }
}
