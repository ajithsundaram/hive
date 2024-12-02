package com.apis.hive.repository

import com.apis.hive.entity.Data
import com.apis.hive.entity.TenantKey
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface DataRepo: JpaRepository<Data,TenantKey> {

    @Query("select data from Data data where data.id.tenantId=:tenantId and data.id.key =:key and (data.ttl > :currentTimeInMilies or data.ttl is null)")
    fun findByTenantIdAndKey(tenantId: Long, key: String,currentTimeInMilies:Long = System.currentTimeMillis()):Data?

    @Query("select count(data) from Data data where data.id.tenantId = :tenantId and (data.ttl > :currentTimeInMilies or data.ttl is null)")
    fun getValidKeyCount(tenantId:Long, currentTimeInMilies:Long = System.currentTimeMillis()):Int

}