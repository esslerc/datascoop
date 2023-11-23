package com.github.esslerc.datascoop.services

import com.fasterxml.jackson.databind.MappingIterator
import com.fasterxml.jackson.dataformat.csv.CsvMapper
import com.fasterxml.jackson.dataformat.csv.CsvSchema
import com.github.esslerc.datascoop.domain.DBInfo
import com.github.esslerc.datascoop.domain.Datasource
import com.github.esslerc.datascoop.domain.ImportPreset
import com.github.esslerc.datascoop.domain.ScoopColumnType
import org.apache.commons.io.input.BOMInputStream
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.FileInputStream
import java.io.InputStreamReader

class CsvImportService(
    private val databaseService: PGDatabaseService,
) {

    fun import(importPreset: ImportPreset) {
        logger.info("Start import for ${importPreset.datasources.size} datasource")

        val dbInfo = importPreset.dbInfo

        importPreset.datasources.forEach { datasource ->
            importFromDatasource(datasource, dbInfo)
        }

        logger.info("Import for ${importPreset.datasources.size} datasource finished")
    }

    private fun importFromDatasource(datasource: Datasource, dbInfo: DBInfo) {
        val csvMapping = datasource.csvMapping
        val csvEncoding = datasource.csvEncoding

        databaseService.dropTable(dbInfo)
        databaseService.createTable(dbInfo, csvMapping)

        val filesToImport = datasource.csvFiles

        filesToImport.forEach { csvFile ->
            val csvMapper = CsvMapper()

            val csvSchemaBuilder = CsvSchema.builder()

            csvMapping.forEach { (columnName, columnType) ->
                csvSchemaBuilder.addColumn(columnName, ScoopColumnType.forValue(columnType).jacksonCsvType)
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

                databaseService.insert(dbInfo, csvMapping, mappingIterator)
            }
        }
    }

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(CsvImportService::class.java)
    }
}
