package com.github.esslerc.datascoop.commands

import com.github.esslerc.datascoop.services.CsvImportService
import java.nio.file.Paths
/*
class ImportCSVFileCommand(
    private val csvImportService: CsvImportService
) {

    fun importCsv(
        mappingFile: String
    ): String {
        val isValid = CsvImportService.validateFile(mappingFile, "json")

        return if (isValid) {
            csvImportService.import(Paths.get(mappingFile).toFile())
            "File were successfully loaded"
        } else {
            "Something happened. It seems like the import or mapping file is not valid"
        }
    }
}*/