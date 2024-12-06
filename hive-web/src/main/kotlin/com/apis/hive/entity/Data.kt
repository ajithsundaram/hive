package com.apis.hive.entity

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.Table
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.io.Serializable

@Entity
@Table(name = "Data",schema = "hive")
class Data(): AbstractEntity<TenantKey>() {
    @EmbeddedId
    var id: TenantKey? = null
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json", name = "dataValue")
    var dataValue: MutableMap<String,Any?>? = null
    var ttl: Long? = null
    override fun getPK() = this.id

    constructor(id: TenantKey, value: MutableMap<String,Any?>? ,ttl: Long? ) : this() {
        this.id = id
        this.dataValue = value
        this.ttl = ttl
    }
}

@Embeddable
data class TenantKey(
    var tenantId: Long? = null,
    var dataKey: String? = null
) : Serializable