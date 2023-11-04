package com.github.esslerc.datascoop.services

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.esslerc.datascoop.database.DatabaseConnectionInfo
import java.io.File
import java.nio.charset.Charset
import java.nio.file.Paths

class JSONMappingService {

    fun loadJSON(jsonFile: File): ImportPreset {
        val objectMapper = ObjectMapper()

        val jsonNode = objectMapper.readTree(jsonFile)

        val databaseConnectionInfo = databaseConnectionInfo(jsonNode["database"])
        val importPreset = ImportPreset(databaseConnectionInfo = databaseConnectionInfo)

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
                        else -> throw IllegalArgumentException("unkown key ${entry.key} in object ${datasourceField.key} found")
                    }
                }
                importPreset.datasources.add(datasource)
            }
        }

        return importPreset
    }

    private fun databaseConnectionInfo(database: JsonNode) =
        DatabaseConnectionInfo(
                database["url"].asText(),
                database["username"].asText(),
                database["password"].asText(),
                database["driver-class-name"].asText(),
            )

}


data class ImportPreset(
    val datasources: MutableList<Datasource> = mutableListOf(),
    val databaseConnectionInfo: DatabaseConnectionInfo,
)

data class Datasource(
    val csvFiles: MutableList<File> = mutableListOf(),
    var csvSeparator: Char = ';',
    var csvHeader: Boolean = true,
    var csvEncoding: Charset = Charsets.UTF_8,
    val csvMapping: MutableMap<String, String> = mutableMapOf()
)

fun JsonNode.asChar(): Char = this.asText()[0]

fun JsonNode.asCharset(): Charset = Charset.forName(this.asText())

fun JsonNode.asMapping(): Map<String, String> {
    val mappingMap = mutableMapOf<String, String>()
    this.fields().forEachRemaining { entry ->
        val columnName: String = entry.key
        val columnType: JsonNode = entry.value

        mappingMap[columnName] = columnType.asText()
    }
    return mappingMap
}


fun JsonNode.asMutableFileList(): MutableList<File> {
    val files = mutableListOf<File>()

    this.elements().forEachRemaining {
        files.add(Paths.get(it.asText()).toFile())
    }

    return files
}
