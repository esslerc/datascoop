package com.github.esslerc.datascoop

import com.github.esslerc.datascoop.services.CsvImportService
import com.github.esslerc.datascoop.services.JsonMappingService
import com.github.esslerc.datascoop.services.PGDatabaseService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import picocli.CommandLine
import picocli.CommandLine.*
import java.util.concurrent.Callable
import kotlin.system.exitProcess
import java.io.File

@Command(name = "datascoop", mixinStandardHelpOptions = true, version = ["DataScoop 1.0"],
    description = ["Imports data from (multiple) csv files into database(s) based on a json configuration file."])
class DataScoopApplication: Callable<Int> {

    @Parameters(index = "0", description = ["The json configuration which contains all necessary import info"])
    lateinit var file: File

    override fun call(): Int {
        val isValid = JsonMappingService.validateFile(file, "json")

        if (isValid) {
            val importPreset = JsonMappingService().loadJSON(file)
            val csvImportService = CsvImportService(PGDatabaseService())
            csvImportService.import(importPreset)
        } else {
            logger.error("Something happened. It seems like the import preset file is not valid.")
        }
        return 0
    }

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(DataScoopApplication::class.java)
    }
}

fun main(args: Array<String>) {
    exitProcess(CommandLine(DataScoopApplication()).execute(*args))
}
