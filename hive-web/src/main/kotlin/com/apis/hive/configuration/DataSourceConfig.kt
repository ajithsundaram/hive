package com.apis.hive.configuration

import com.zaxxer.hikari.HikariDataSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.data.repository.config.BootstrapMode
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.JpaVendorAdapter
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.orm.jpa.persistenceunit.PersistenceManagedTypes
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter
import org.springframework.transaction.PlatformTransactionManager
import javax.sql.DataSource

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
        return dataSource
    }

}


