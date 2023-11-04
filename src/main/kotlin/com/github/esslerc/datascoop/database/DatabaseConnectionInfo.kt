package com.github.esslerc.datascoop.database

data class DatabaseConnectionInfo (
    val url: String,
    val username: String,
    val password: String,
    val driverClassName: String,
)