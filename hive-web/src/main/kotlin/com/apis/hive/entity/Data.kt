package com.apis.hive.entity

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.io.Serializable

@Entity
class Data(): AbstractEntity<TenantKey>() {
    @EmbeddedId
    var id: TenantKey? = null
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    var value: MutableMap<String,Any?>? = null
    var ttl: Long? = null
    override fun getPK() = this.id
}

@Embeddable
data class TenantKey(
    var tenantId: Long? = null,
    @Column(name = "`key`")
    var key: String? = null
) : Serializable