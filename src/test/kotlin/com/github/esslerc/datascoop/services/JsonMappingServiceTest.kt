package com.github.esslerc.datascoop.services

import com.github.esslerc.datascoop.domain.DBInfo
import com.github.esslerc.datascoop.domain.Datasource
import com.github.esslerc.datascoop.domain.ImportPreset
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.nio.file.Paths

class JsonMappingServiceTest {


    @Test
    fun validateFile() {
        val tempFile = File.createTempFile("temp", ".csv")

        val actual = JsonMappingService.validateFile(tempFile)

        assertEquals(true, actual)

        tempFile.delete()
    }

    @Test
    fun validateFile_wrongSuffix() {
        val tempFile = File.createTempFile("temp", ".txt")

        val actual = JsonMappingService.validateFile(tempFile)

        assertEquals(false, actual)

        tempFile.delete()
    }



    @Test
    fun validateFile_noFile() {
        val actual = JsonMappingService.validateFile(Paths.get("unkown").toFile())

        assertEquals(false, actual)
    }

    @Test
    fun loadJSON() {
        val jsonMappingService = JsonMappingService()

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
                    "Name": "text",
                    "Age": "integer",
                    "City": "text",
                    "Email": "text"
                  }
                }
              },
              "database": {
                "url": "",
                "username": "root",
                "password": "password",
                "table-name": "table1"
              }
            }
            """.trimIndent()

        val file = writeJsonContent(content)

        val importPreset = jsonMappingService.loadJSON(file)

        val expectedImportPreset = ImportPreset(
            dbInfo = DBInfo(
                jdbcUrl = "",
                username = "root",
                password = "password",
                tableName = "table1",
            )
        ).also {
            val datasource1 = Datasource()
            datasource1.csvFiles.add(Paths.get("/tmp/example1.csv").toFile())
            datasource1.csvFiles.add(Paths.get("/tmp/example2.csv").toFile())
            datasource1.csvSeparator = ','
            datasource1.csvHeader = true
            datasource1.csvEncoding = Charsets.UTF_8
            datasource1.csvMapping["Name"] = "text"
            datasource1.csvMapping["Age"] = "integer"
            datasource1.csvMapping["City"] = "text"
            datasource1.csvMapping["Email"] = "text"

            it.datasources.add(datasource1)
        }

        assertEquals(expectedImportPreset, importPreset)
    }

    private fun writeJsonContent(content: String): File {
        val tempFile = File.createTempFile("test", ".json")
        BufferedWriter(FileWriter(tempFile)).use {
            it.write(content)
        }
        return tempFile
    }
}
