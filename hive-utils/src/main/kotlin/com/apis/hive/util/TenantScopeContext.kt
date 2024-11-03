package com.apis.hive.util

/*
TenantContext thread local holds the tenantId and schema of currently processing thread
it should be cleared once request is done with the current tenant. its cleared in filter.
if we want to switch and execute in query in hive db means we need to set schema to hive and execute and revert to current schema
or use com.apis.hive.util.HiveUtil.executeInHiveSchema method
 */
object TenantContext : ThreadLocal<TenantInfo?>() {
    override fun initialValue(): TenantInfo? {
        return null
    }

    fun getTenantId(): Long? {
        return get()?.tenantId
    }

    fun getSchema(): String? {
        return get()?.schema
    }

    fun setTenantScopeInfo(tenantScopeInfo: TenantInfo) {
        val currentTenantScopeInfo = get()
        if (currentTenantScopeInfo?.schema != null && currentTenantScopeInfo.tenantId != null) {
            if (tenantScopeInfo.schema != AppConstant.HIVE_SCHEMA_NAME &&
                currentTenantScopeInfo.schema != AppConstant.HIVE_SCHEMA_NAME &&
                (currentTenantScopeInfo.schema != tenantScopeInfo.schema || currentTenantScopeInfo.tenantId != tenantScopeInfo.tenantId)) {
                throw Exception("Setting a different tenant before clearing the current tenant info in thread local")
            }
        }
        TenantContext.set(tenantScopeInfo)
    }
}

data class TenantInfo(
    val schema: String?,
    val tenantId: Long?
)
