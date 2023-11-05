package com.github.esslerc.datascoop.services

import com.fasterxml.jackson.databind.MappingIterator
import com.github.esslerc.datascoop.domain.DBInfo
import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.util.*


class PGDatabaseService {

    fun dropTable(dbInfo: DBInfo) {
        getConnection(dbInfo).use { connection ->
            val sql = "DROP TABLE IF EXISTS ${dbInfo.tableName}"
            connection.createStatement().use { statement ->
                statement.execute(sql)
                connection.commit()
            }
        }
    }

    fun createTable(dbInfo: DBInfo, mapping: Map<String, String>) {
        getConnection(dbInfo).use { connection ->
            val columnDefinitions = mapping.entries.joinToString(", ") { (columnName, columnType) ->
                "$columnName ${getPGDatatype(columnType)}"
            }
            val sql = "CREATE TABLE ${dbInfo.tableName} (id SERIAL PRIMARY KEY, $columnDefinitions)"
            connection.createStatement().use { statement ->
                statement.execute(sql)
                connection.commit()
            }
        }
    }

    fun insert(
        dbInfo: DBInfo,
        mapping: Map<String, String>,
        iterator: MappingIterator<Map<String, String>>
    ) {
        getConnection(dbInfo).use { connection ->
            val columnNames = mapping.keys.joinToString(", ")
            val placeholders = mapping.keys.joinToString(", ") { "?" }
            val sql = "INSERT INTO ${dbInfo.tableName} ($columnNames) VALUES ($placeholders)"
            connection.prepareStatement(sql).use { preparedStatement ->
                while (iterator.hasNext()) {
                    iterator.asSequence().chunked(1000).forEach { values ->
                        for (map in values) {
                            for ((index, columnName) in mapping.keys.withIndex()) {
                                val columnType = mapping[columnName]
                                    ?: throw IllegalArgumentException("Unknown column type for column $columnName")
                                val columnValue = map[columnName]
                                    ?: throw IllegalArgumentException("Value for column $columnName is missing")
                                setPreparedStatementParameter(preparedStatement, index + 1, columnType, columnValue)
                            }
                            preparedStatement.addBatch()
                        }
                    }
                    preparedStatement.executeBatch()
                }
                connection.commit()
            }
        }
    }


    private fun setPreparedStatementParameter(
        preparedStatement: PreparedStatement,
        index: Int,
        columnType: String,
        columnValue: String
    ) {
        when (columnType.lowercase()) {
            "string" -> preparedStatement.setString(index, columnValue)
            "int" -> preparedStatement.setInt(index, columnValue.toInt())
            "boolean" -> preparedStatement.setBoolean(index, columnValue.toBoolean())
            "double" -> preparedStatement.setBigDecimal(index, columnValue.toBigDecimal())
            else -> throw IllegalArgumentException("Unknown PGColumnType $columnType")
        }
    }


    private fun getPGDatatype(pgColumnType: String) = when (pgColumnType.lowercase()) {
        "string" -> "VARCHAR"
        "int" -> "BIGINT"
        "boolean" -> "BOOLEAN"
        "double" -> "NUMERIC(16,4)"
        else -> throw IllegalArgumentException("Unknown PGColumnType $pgColumnType")
    }

    private fun getConnection(dbInfo: DBInfo): Connection {
        val props = Properties()
        props.setProperty("user", dbInfo.username)
        props.setProperty("password", dbInfo.password)
        return DriverManager.getConnection(dbInfo.jdbcUrl, props).apply {
            autoCommit = false
        }
    }
}