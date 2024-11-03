package com.apis.hive.repo

import com.apis.hive.dto.SchemaDetails
import com.apis.hive.repo.resultSetMapper.SchemaRowMapper
import com.apis.hive.util.AppConstant
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository

@Repository
class HiveDataRepository {
    @Autowired
    private lateinit var jdbcTemplate: JdbcTemplate
    private var schemaRowMapper = SchemaRowMapper()
    @Autowired
    private lateinit var readRepository: ReadRepository

    fun createSchemaIfNotExist(schemaName: String) {
        val query = "CREATE SCHEMA IF NOT EXISTS $schemaName"
        jdbcTemplate.execute(query)
    }

    fun getAllTenantSchema(): List<String> {
        val sql = "SELECT name from SchemaDetails"
        return jdbcTemplate.queryForList(sql, String::class.java)
    }

    fun getOneFreeSchemaForTenant(): String? {
        val sql = "SELECT name from SchemaDetails where tenantId is null limit 1"
        return jdbcTemplate.queryForObject(sql, String::class.java)
    }

    fun getUnassignedSchemaCount(): Int {
        val sql = "SELECT count(*) from SchemaDetails where tenantId is null"
        return jdbcTemplate.queryForObject(sql, Int::class.java) ?: 0
    }

    fun dropSchemaIfExist(schemaName: String) {
        if (schemaName.startsWith(AppConstant.TENANT_DB_PREFIX)) {
            val sql = "DROP Database IF EXISTS $schemaName"
            jdbcTemplate.execute(sql)
        }
    }

    fun saveInSchemaDetails(schemaName: String) {
        val sql = "INSERT INTO SchemaDetails (name) VALUES ('$schemaName')"
        jdbcTemplate.execute(sql)
    }

    fun saveInTenantDetails(tenantId: Long, storageSize: Long) {
        val sql = "INSERT INTO TenantDetails (tenantId,storageSize) VALUES ('$tenantId',$storageSize)"
        jdbcTemplate.execute(sql)
    }

    fun updateTenantForSchema(tenantId: Long, schemaName: String) {
        val sql = "UPDATE SchemaDetails set tenantId = '$tenantId' where name = '$schemaName'"
        jdbcTemplate.execute(sql)
    }

    fun lockForSchemaAssignment() {
        val sql = "SELECT * from SchemaDetailsLock where lockName = '${AppConstant.DB_ASSIGNMENT_LOCK}' for UPDATE"
        jdbcTemplate.execute(sql)
    }

    fun findByTenantId(tenantId: Long): SchemaDetails? {
        val params = listOf(tenantId)
        return readRepository.find(AppConstant.SCHEMA_DETAILS_TABLE, setOf(), AppConstant.TENANT_ID_CONDITION, params, schemaRowMapper)
    }
}
