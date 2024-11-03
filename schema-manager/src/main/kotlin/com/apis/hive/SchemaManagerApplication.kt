package com.apis.hive

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

@ComponentScan
@Configuration
@SpringBootApplication
class SchemaManagerApplication

fun main(args: Array<String>) {
    runApplication<SchemaManagerApplication>(*args)
}
