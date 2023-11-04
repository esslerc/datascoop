package com.github.esslerc.datascoop.services

import com.github.esslerc.datascoop.database.DatabaseConnectionInfo
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.nio.file.Paths

class JSONMappingServiceTest {

    @Test
    fun loadJSON() {
        val jsonMappingService = JSONMappingService()

        val content =
            """
            {
              "datasources": {
                "datasource-1": {
                  "csv-files": [
                    "/tmp/example1.csv",
                    "/tmp/example2.csv"
                  ],
                  "csv-separator": ",",
                  "csv-header": true,
                  "csv-encoding": "UTF-8",
                  "csv-mapping": {
                    "Name": "String",
                    "Age": "Int",
                    "City": "String",
                    "Email": "String"
                  }
                }
              },
              "database": {
                "url": "",
                "username": "root",
                "password": "password",
                "driver-class-name": ""
              }
            }
            """.trimIndent()

        val file = writeJSONContent(content)

        val importPreset = jsonMappingService.loadJSON(file)

        val expectedImportPreset = ImportPreset(
            databaseConnectionInfo = DatabaseConnectionInfo(
                url = "",
                username = "root",
                password = "password",
                driverClassName = ""
            )
        ).also {
            val datasource1 = Datasource()
            datasource1.csvFiles.add(Paths.get("/tmp/example1.csv").toFile())
            datasource1.csvFiles.add(Paths.get("/tmp/example2.csv").toFile())
            datasource1.csvSeparator = ','
            datasource1.csvHeader = true
            datasource1.csvEncoding = Charsets.UTF_8
            datasource1.csvMapping["Name"] = "String"
            datasource1.csvMapping["Age"] = "Int"
            datasource1.csvMapping["City"] = "String"
            datasource1.csvMapping["Email"] = "String"

            it.datasources.add(datasource1)
        }

        assertEquals(expectedImportPreset, importPreset)
    }

    private fun writeJSONContent(content: String): File {
        val tempFile = File.createTempFile("test", ".json")
        BufferedWriter(FileWriter(tempFile)).use {
            it.write(content)
        }
        return tempFile
    }
}
