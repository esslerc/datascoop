package com.github.esslerc.datascoop.domain

import java.io.File
import java.nio.charset.Charset

data class Datasource(
    val csvFiles: MutableList<File> = mutableListOf(),
    var csvSeparator: Char = ';',
    var csvHeader: Boolean = true,
    var csvEncoding: Charset = Charsets.UTF_8,
    val csvMapping: MutableMap<String, String> = mutableMapOf()
)