package com.apis.hive.configuration

import com.apis.hive.util.HiveUtil
import net.mguenther.idem.flake.Flake64L
import net.mguenther.idem.provider.LinearTimeProvider
import net.mguenther.idem.provider.StaticWorkerIdProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

@Component
class ApplicationConfig {

    @Autowired
    private lateinit var hiveUtil: HiveUtil

    @Bean
    fun idGenerator(): Flake64L {
        return Flake64L(LinearTimeProvider(), StaticWorkerIdProvider(hiveUtil.getLocalIpAddress()))
    }

    @Bean
    @Primary
    @ConfigurationProperties(prefix = "app.liquibase")
    fun primaryLiquibaseProperties(): LiquibaseProperties {
        return LiquibaseProperties()
    }

    @Bean
    fun restTemplate(): RestTemplate? {
        return RestTemplate()
    }
}
