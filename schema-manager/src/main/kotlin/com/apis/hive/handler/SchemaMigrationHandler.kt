package com.apis.hive.handler

import com.apis.hive.repo.HiveDataRepository
import com.apis.hive.util.AppConstant
import net.mguenther.idem.flake.Flake64L
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import org.springframework.stereotype.Service

@Service
class SchemaMigrationHandler {
    @Autowired
    private lateinit var liquibaseMigrationHandler: LiquibaseMigrationHandler
    @Autowired
    private lateinit var hiveDataRepository: HiveDataRepository
    @Value("\${app.liquibase.tenant.ddl.change-log}")
    private val tenantDDLChangelog: String? = null
    @Value("\${app.liquibase.hive.ddl.change-log}")
    private val hiveDDLChangelog: String? = null
    @Value("\${app.liquibase.hive.dml.change-log}")
    private val hiveDMLChangelog: String? = null
    @Value("\${app.multitenant.freeSchema.count}")
    private val freeSchemaCount: String? = null
    @Autowired
    private lateinit var idGenerator: Flake64L
    private val threadPoolExecutor: ThreadPoolTaskExecutor = ThreadPoolTaskExecutor()

    init {
        threadPoolExecutor.corePoolSize = 4
        threadPoolExecutor.initialize()
    }

    @EventListener(ApplicationReadyEvent::class)
    fun migrateSchema() {
        migrateHiveSchema()
        migrateExistingTenants()
        checkAndInitiateFreeSchemaCreation()
    }

    fun migrateHiveSchema() {
        liquibaseMigrationHandler.migrate(AppConstant.HIVE_SCHEMA_NAME, hiveDDLChangelog!!)
        liquibaseMigrationHandler.migrate(AppConstant.HIVE_SCHEMA_NAME, hiveDMLChangelog!!)
    }

    fun migrateExistingTenants() {
        val allTenantSchemas: List<String> = hiveDataRepository.getAllTenantSchema()
        allTenantSchemas.forEach { schemaName ->
            println("Migration Started for $schemaName")
            migrateTenantSchema(schemaName)
            println("Migration Completed for $schemaName")
        }
    }

    fun migrateTenantSchema(schemaName: String) {
        liquibaseMigrationHandler.migrate(schemaName, tenantDDLChangelog!!)
    }

    fun checkAndInitiateFreeSchemaCreation() {
        threadPoolExecutor.execute { val availableFreeSchemas = hiveDataRepository.getUnassignedSchemaCount()
            if (availableFreeSchemas < freeSchemaCount!!.toInt()) {
                val schemasNeedsToBeCreated = freeSchemaCount.toInt().minus(availableFreeSchemas)
                for (i in 1..schemasNeedsToBeCreated) {
                    val schemaName = createSchemaForTenant()
                    if (schemaName != null) {
                        try {
                            migrateTenantSchema(schemaName)
                            hiveDataRepository.saveInSchemaDetails(schemaName)
                        } catch (ex: Exception) {
                            println("exception while running migration in free schema $schemaName" + ex.printStackTrace())
                            hiveDataRepository.dropSchemaIfExist(schemaName)
                            println("dropped free schema $schemaName")
                        }
                    } else {
                        println("Exception while creating Schema $schemaName")
                    }
                }
            } else {
                println("Available Free Schema for Tenants $availableFreeSchemas")
            }
        }
    }

    fun createSchemaForTenant(): String? {
        val schemaName = AppConstant.TENANT_DB_PREFIX + "_" + idGenerator.nextId()
        try {
            hiveDataRepository.createSchemaIfNotExist(schemaName)
        } catch (ex: Exception) {
            println("exception while creating schema")
            return null
        }
        return schemaName
    }
}
