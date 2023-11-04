package com.github.esslerc.datascoop

import com.github.esslerc.datascoop.services.CsvImportService
import com.github.esslerc.datascoop.services.JSONMappingService
import com.github.esslerc.datascoop.services.PGDatabaseService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import picocli.CommandLine
import picocli.CommandLine.*
import java.util.concurrent.Callable
import kotlin.system.exitProcess
import java.io.File

@Command(name = "datascoop", mixinStandardHelpOptions = true, version = ["DataScoop 1.0"],
    description = ["Imports data from (multiple) csv files into databases based on a json configuration file."])
class DataScoopApplication: Callable<Int> {

    @Parameters(index = "0", description = ["The json configuration which contains all necessary import info"])
    lateinit var file: File

    override fun call(): Int {
        val isValid = CsvImportService.validateFile(file, "json")

        val jsonMappingService = JSONMappingService()
        val pgDatabaseService = PGDatabaseService()

        val csvImportService = CsvImportService(jsonMappingService, pgDatabaseService)

        if (isValid) {
            csvImportService.import(file)
            logger.info("File were successfully loaded")
        } else {
            logger.info("Something happened. It seems like the import or mapping file is not valid")
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
