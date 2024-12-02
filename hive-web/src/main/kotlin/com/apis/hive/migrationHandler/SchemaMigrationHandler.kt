package com.apis.hive.migrationHandler

import com.apis.hive.util.AppConstant
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service

@Service
class SchemaMigrationHandler {
    @Autowired
    private lateinit var liquibaseMigrationHandler: LiquibaseMigrationHandler
    @Value("\${app.liquibase.hive.ddl.change-log}")
    private val hiveDDLChangelog: String? = null

    @EventListener(ApplicationReadyEvent::class)
    fun migrateSchema() {
        liquibaseMigrationHandler.migrate(AppConstant.HIVE_SCHEMA_NAME, hiveDDLChangelog!!)
    }
}
