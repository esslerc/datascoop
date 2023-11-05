package com.github.esslerc.datascoop.services

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.esslerc.datascoop.domain.DBInfo
import com.github.esslerc.datascoop.domain.Datasource
import com.github.esslerc.datascoop.domain.ImportPreset
import com.github.esslerc.datascoop.extensions.asChar
import com.github.esslerc.datascoop.extensions.asCharset
import com.github.esslerc.datascoop.extensions.asMapping
import com.github.esslerc.datascoop.extensions.asMutableFileList
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.nio.charset.Charset
import java.nio.file.Files

class JsonMappingService {

    fun loadJSON(jsonFile: File): ImportPreset {
        val objectMapper = ObjectMapper()

        val jsonNode = objectMapper.readTree(jsonFile)

        val dbInfo = dbInfo(jsonNode["database"])
        val importPreset = ImportPreset(dbInfo = dbInfo)

        // Mandatory fields
        val datasources = jsonNode["datasources"]

        datasources.fields().forEachRemaining { datasourceField ->
            if (datasourceField.key.startsWith("datasource-")) {
                val datasource = Datasource()
                datasourceField.value.fields().forEachRemaining { entry ->
                    when (entry.key) {
                        "csv-files" -> datasource.csvFiles.addAll(entry.value.asMutableFileList())
                        "csv-separator" -> datasource.csvSeparator = entry.value.asChar()
                        "csv-header" -> datasource.csvHeader = entry.value.asBoolean()
                        "csv-encoding" -> datasource.csvEncoding = entry.value.asCharset()
                        "csv-mapping" -> datasource.csvMapping.putAll(entry.value.asMapping())
                        else -> throw IllegalArgumentException("unknown key ${entry.key} in object ${datasourceField.key} found")
                    }
                }
                importPreset.datasources.add(datasource)
            }
        }

        return importPreset
    }

    private fun dbInfo(database: JsonNode) =
        DBInfo(
            database["url"].asText(),
            database["username"].asText(),
            database["password"].asText(),
            database["table-name"].asText(),
        )

    companion object {

        private val logger: Logger = LoggerFactory.getLogger(JsonMappingService::class.java)

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
