package com.github.esslerc.datascoop.domain

import com.fasterxml.jackson.dataformat.csv.CsvSchema
import java.sql.Types

enum class ScoopColumnType (
    val jacksonCsvType: CsvSchema.ColumnType,
    val sqlType: Int,
    val pgType: String,
    ) {
    STRING_COLUMN_TYPE(CsvSchema.ColumnType.STRING, Types.VARCHAR, "VARCHAR"),
    INT_COLUMN_TYPE(CsvSchema.ColumnType.NUMBER, Types.INTEGER, "INTEGER"),
    BOOLEAN_COLUMN_TYPE(CsvSchema.ColumnType.BOOLEAN, Types.BOOLEAN, "BOOLEAN"),
    NUMERIC_COLUMN_TYPE(CsvSchema.ColumnType.NUMBER, Types.NUMERIC, "NUMERIC");

    companion object {
        fun forValue(value: String) =
            when (value.lowercase()) {
                "text" -> STRING_COLUMN_TYPE
                "integer" -> INT_COLUMN_TYPE
                "boolean" -> BOOLEAN_COLUMN_TYPE
                "double" -> NUMERIC_COLUMN_TYPE
                else -> throw IllegalArgumentException("unknown csvColumnType $value")
            }
    }

}
