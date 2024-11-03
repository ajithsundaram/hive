package com.apis.hive.configuration

import com.apis.hive.util.AppConstant
import com.apis.hive.util.TenantContext
import com.zaxxer.hikari.HikariDataSource
import java.sql.Connection
import javax.sql.DataSource
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.jdbc.datasource.AbstractDataSource

@Configuration
class DataSourceConfig {
    @Value("\${app.datasource.hive.url}")
    private val url: String? = null
    @Value("\${app.datasource.hive.user}")
    private val username: String? = null
    @Value("\${app.datasource.hive.password}")
    private val password: String? = null
    @Bean
    @Primary
    fun dataSource(): DataSource {
        val dataSource = HikariDataSource()
        dataSource.jdbcUrl = url
        dataSource.username = username
        dataSource.password = password
        return SchemaSwitchingDataSource(dataSource)
    }
}

class SchemaSwitchingDataSource(private val dataSource: DataSource) : AbstractDataSource() {
    override fun getConnection(): Connection {
        println("GET CONNECTION CALLED ***")
        return switchSchema(dataSource.connection)
    }

    override fun getConnection(username: String, password: String): Connection {
        println("GET CONNECTION CALLED with username and pass***")
        return switchSchema(dataSource.getConnection(username, password))
    }

    private fun switchSchema(connection: Connection): Connection {
        val tenantId = TenantContext.getTenantId()
        val schema = TenantContext.getSchema() ?: AppConstant.HIVE_SCHEMA_NAME
        println("using $tenantId and schema $schema")
        connection.createStatement().execute("use $schema")
        return connection
    }
}
