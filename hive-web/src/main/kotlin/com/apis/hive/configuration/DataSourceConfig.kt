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
//@EnableJpaRepositories(
//    basePackages = ["com.apis.hive"],
//    entityManagerFactoryRef = "hiveEntityManagerFactory",
//    transactionManagerRef = "hiveTransaction",
//    bootstrapMode = BootstrapMode.DEFAULT
//)
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

//    @Bean
//    @Primary
//    fun jpaVendorAdapter(): JpaVendorAdapter {
//        val adapter = HibernateJpaVendorAdapter()
//        adapter.setGenerateDdl(true)
//        adapter.setDatabasePlatform("org.hibernate.dialect.MySQLDialect")
//        return adapter
//    }
//
//    @Bean
//    @Primary
//    @Qualifier("hiveEntityManagerFactory")
//    fun hiveEntityManagerFactory(@Autowired dataSource: DataSource, jpaVendorAdapter: JpaVendorAdapter): LocalContainerEntityManagerFactoryBean {
//        val emf = LocalContainerEntityManagerFactoryBean()
//        emf.setJtaDataSource(dataSource)
//        emf.dataSource = dataSource
//        emf.jpaVendorAdapter = jpaVendorAdapter
//        emf.setPackagesToScan("com.apis.hive")
//        return emf
//    }
//
//
//    @Bean
//    @Qualifier("hiveTransaction")
//    @Primary
//    fun hiveTransaction(
//         @Qualifier("hiveEntityManagerFactory")
//         entityManagerFactory: LocalContainerEntityManagerFactoryBean
//    ): PlatformTransactionManager {
//        return JpaTransactionManager(entityManagerFactory.getObject()!!)
//    }

}


