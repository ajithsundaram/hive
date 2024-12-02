package com.apis.hive

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.orm.jpa.support.OpenEntityManagerInViewFilter

@ComponentScan
@Configuration
@SpringBootApplication(exclude = [UserDetailsServiceAutoConfiguration::class])
class HiveWebApplication {

    @Bean
    fun OpenFilter(): OpenEntityManagerInViewFilter {
        return OpenEntityManagerInViewFilter()
    }
}

fun main(args: Array<String>) {
    val test = runApplication<HiveWebApplication>(*args)
}