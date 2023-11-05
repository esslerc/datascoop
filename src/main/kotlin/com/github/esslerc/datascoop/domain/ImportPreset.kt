package com.github.esslerc.datascoop.domain

data class ImportPreset(
    val datasources: MutableList<Datasource> = mutableListOf(),
    val dbInfo: DBInfo,
)