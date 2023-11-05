package com.github.esslerc.datascoop.extensions

import com.fasterxml.jackson.databind.JsonNode
import java.io.File
import java.nio.charset.Charset
import java.nio.file.Paths

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