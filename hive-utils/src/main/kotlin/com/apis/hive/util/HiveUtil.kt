package com.apis.hive.util

import java.net.Inet4Address
import java.net.InetAddress
import java.net.NetworkInterface
import java.util.Enumeration
import org.springframework.stereotype.Service

@Service
class HiveUtil {

    fun getLocalIpAddress(): String {
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

    fun executeInHiveSchema(function: () -> Any?): Any? {
        val currentTenantId = TenantContext.getTenantId()
        val currentTenantSchema = TenantContext.getSchema()
        return try {
            TenantContext.setTenantScopeInfo(TenantInfo(AppConstant.HIVE_SCHEMA_NAME, currentTenantId))
            function.invoke()
        } catch (ex: Throwable) {
            println("exception while executeInHiveSchema ${ex.message}")
            null
        } finally {
            TenantContext.setTenantScopeInfo(TenantInfo(currentTenantSchema, currentTenantId))
        }
    }
}
