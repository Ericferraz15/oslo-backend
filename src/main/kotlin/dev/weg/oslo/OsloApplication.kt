package dev.weg.oslo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class OsloApplication

fun main(args: Array<String>) {
	runApplication<OsloApplication>(*args)
}
