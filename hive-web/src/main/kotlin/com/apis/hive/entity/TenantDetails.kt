package com.apis.hive.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "TenantDetails",schema = "hive")
class TenantDetails: AbstractEntity<Long>() {
    @Id
    var tenantId : Long? = null
    var storageSize: Int? = null
    override fun getPK() = tenantId
}