package com.apis.hive.dto

import com.apis.hive.util.AppConstant
import com.fasterxml.jackson.databind.ObjectMapper
import java.sql.PreparedStatement
import java.sql.Types
import net.mguenther.idem.flake.Flake64L

class Data() : BatchSupportedEntity {
    var id: Long? = null
    var key: String? = null
    var value: Map<*, *>? = null
    var ttl: Long? = null

    constructor(id: Long, key: String?, value: Map<*, *>?, ttl: Long?) : this() {
        this.id = id
        this.key = key
        this.value = value
        this.ttl = ttl
    }

    override fun setPreparedStatement(ps: PreparedStatement, insertFields: List<String>, idgen: Flake64L?) {
        insertFields.forEachIndexed { index, value ->
            when (value) {
                AppConstant.DATA_ID -> ps.setLong(index + 1, idgen!!.nextId())
                AppConstant.DATA_KEY -> ps.setString(index + 1, this.key)
                AppConstant.DATA_VALUE -> ps.setString(index + 1, ObjectMapper().writeValueAsString(this.value))
                AppConstant.DATA_TTL -> {
                    if (this.ttl != null) {
                        ps.setLong(index + 1, this.ttl!!)
                    } else { ps.setNull(index + 1, Types.BIGINT) } }
                else -> throw UnsupportedOperationException()
            }
        }
    }
}

/*
this interface should be implemented in all classes which needs to supported for batch insert
 */
interface BatchSupportedEntity {
    fun setPreparedStatement(ps: PreparedStatement, insertFields: List<String>, idgen: Flake64L?)
}
