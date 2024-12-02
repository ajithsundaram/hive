package com.apis.hive.configuration

import net.mguenther.idem.flake.Flake64L
import net.mguenther.idem.provider.LinearTimeProvider
import net.mguenther.idem.provider.StaticWorkerIdProvider
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import java.net.Inet4Address
import java.net.InetAddress
import java.net.NetworkInterface
import java.util.*

@Component
class ApplicationConfig {
    @Bean
    fun idGenerator(): Flake64L {
        return Flake64L(LinearTimeProvider(), StaticWorkerIdProvider(getLocalIpAddress()))
    }

    private fun getLocalIpAddress(): String {
        var ipAddress = "0.0.0.0"
        try {
            val networkInterfaceList: Enumeration<NetworkInterface> = NetworkInterface.getNetworkInterfaces()
            parentLoop@ while (networkInterfaceList.hasMoreElements()) {
                val networkInterface = networkInterfaceList.nextElement()
                val addressList: Enumeration<InetAddress> = networkInterface.inetAddresses
                while (addressList.hasMoreElements()) {
                    val address: InetAddress = addressList.nextElement()
                    if (address is Inet4Address && !address.isLoopbackAddress) {
                        ipAddress = address.hostAddress.trim()
                        break@parentLoop
                    }
                }
            }
        } catch (ex: Exception) {
        }
        return ipAddress
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
