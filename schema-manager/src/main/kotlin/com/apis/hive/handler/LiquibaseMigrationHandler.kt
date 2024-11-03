package com.apis.hive.handler
import javax.sql.DataSource
import liquibase.integration.spring.SpringLiquibase
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties
import org.springframework.context.ResourceLoaderAware
import org.springframework.core.io.ResourceLoader
import org.springframework.stereotype.Service

@Service
class LiquibaseMigrationHandler(
    @Autowired
    private val dataSource: DataSource,
    private var liquibaseProperties: LiquibaseProperties
) : ResourceLoaderAware {
    private lateinit var resourceLoader: ResourceLoader

    fun migrate(schemaName: String, changeLog: String) {
        val mainSchemaSpringLiquibaseConfig = constructSpringLiquibase(schemaName, changeLog)
        mainSchemaSpringLiquibaseConfig.afterPropertiesSet()
    }

    private fun constructSpringLiquibase(schemaName: String, changeLog: String): SpringLiquibase {
        val springLiquibase = SpringLiquibase()
        springLiquibase.dataSource = dataSource
        springLiquibase.contexts = liquibaseProperties.contexts
        springLiquibase.defaultSchema = schemaName
        springLiquibase.resourceLoader = resourceLoader
        springLiquibase.changeLog = changeLog
        return springLiquibase
    }

    override fun setResourceLoader(resourceLoader: ResourceLoader) {
        this.resourceLoader = resourceLoader
    }
}
