package com.github.esslerc.datascoop.domain

data class DBInfo (
    val jdbcUrl: String,
    val username: String,
    val password: String,
    val tableName: String,
)