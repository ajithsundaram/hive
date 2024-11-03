package com.apis.hive.repo.resultSetMapper

import com.apis.hive.dto.Data
import com.apis.hive.dto.SchemaDetails
import com.apis.hive.dto.TenantDetails
import com.apis.hive.util.AppConstant
import com.fasterxml.jackson.databind.ObjectMapper
import java.sql.ResultSet
import org.springframework.jdbc.core.RowMapper

class DataRowMapper : RowMapper<Data> {
    private val objectMapper = ObjectMapper()
    override fun mapRow(rs: ResultSet, rowNum: Int): Data {
        val id = rs.getLong(AppConstant.DATA_ID)
        val key = rs.getString(AppConstant.DATA_KEY)
        val data = rs.getString(AppConstant.DATA_VALUE)
        val dataJson = objectMapper.readValue(data, Map::class.java)
        val ttl = rs.getLong(AppConstant.DATA_TTL)
        return Data(id, key, dataJson, ttl)
    }
}

class SchemaRowMapper : RowMapper<SchemaDetails> {
    override fun mapRow(rs: ResultSet, rowNum: Int): SchemaDetails {
        return SchemaDetails(rs.getString(AppConstant.SCHEMA_DETAILS_NAME), rs.getLong(AppConstant.SCHEME_DETAILS_TENANT_ID))
    }
}

class TenantDetailsRowMapper: RowMapper<TenantDetails> {
    override fun mapRow(rs: ResultSet, rowNum: Int): TenantDetails {
        return TenantDetails(rs.getString(AppConstant.TENANT_DETAILS_TENANT_ID), rs.getLong(AppConstant.TENANT_DETAILS_STORAGE_SIZE))
    }

}
