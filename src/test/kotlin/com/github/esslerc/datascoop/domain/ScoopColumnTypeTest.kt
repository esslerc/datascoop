package com.github.esslerc.datascoop.domain

import com.fasterxml.jackson.dataformat.csv.CsvSchema
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class ScoopColumnTypeTest {
    @Test
    fun testGetJacksonColumnType() {
        val stringType = ScoopColumnType.forValue("string")
        val intType = ScoopColumnType.forValue("int")
        val booleanType = ScoopColumnType.forValue("boolean")
        val doubleType = ScoopColumnType.forValue("double")

        assertEquals(CsvSchema.ColumnType.STRING, stringType.jacksonCsvType)
        assertEquals(CsvSchema.ColumnType.NUMBER, intType.jacksonCsvType)
        assertEquals(CsvSchema.ColumnType.BOOLEAN, booleanType.jacksonCsvType)
        assertEquals(CsvSchema.ColumnType.NUMBER, doubleType.jacksonCsvType)
    }

    @Test
    fun testGetPGColumnType() {
        val stringType = ScoopColumnType.forValue("string")
        val intType = ScoopColumnType.forValue("int")
        val booleanType = ScoopColumnType.forValue("boolean")
        val doubleType = ScoopColumnType.forValue("double")

        assertEquals("VARCHAR", stringType.pgType)
        assertEquals("INTEGER", intType.pgType)
        assertEquals("BOOLEAN", booleanType.pgType)
        assertEquals("NUMERIC(16,4)", doubleType.pgType)
    }

    @Test
    fun testGetSQLColumnType() {
        val stringType = ScoopColumnType.forValue("string")
        val intType = ScoopColumnType.forValue("int")
        val booleanType = ScoopColumnType.forValue("boolean")
        val doubleType = ScoopColumnType.forValue("double")

        assertEquals(java.sql.Types.VARCHAR, stringType.sqlType)
        assertEquals(java.sql.Types.INTEGER, intType.sqlType)
        assertEquals(java.sql.Types.BOOLEAN, booleanType.sqlType)
        assertEquals(java.sql.Types.NUMERIC, doubleType.sqlType)
    }
}