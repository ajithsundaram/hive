package com.apis.hive.repo

import com.apis.hive.dto.BatchSupportedEntity
import java.sql.PreparedStatement
import net.mguenther.idem.flake.Flake64L
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.BatchPreparedStatementSetter
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class WriteRepository {
    @Autowired
    private lateinit var jdbcTemplate: JdbcTemplate
    @Autowired
    private lateinit var idgen: Flake64L

    fun deleteDataByKey(entityName: String?, condition: String?, params: List<Any>): Int {
        val query = buildDeleteQuery(entityName, condition)
        return jdbcTemplate.update(query, *params.toTypedArray())
    }

    private fun buildDeleteQuery(entityName: String?, condition: String?): String {
        val query = StringBuilder(1000)
        query.append("DELETE FROM ")
        query.append("$entityName ")
        if (condition != null) {
            query.append("WHERE $condition")
        }
        return query.toString()
    }

    @Transactional
    fun bulkInsertData(entityName: String, data: List<BatchSupportedEntity>, insertColumns: List<String>) {
        val sql = buildInsertQuery(entityName, insertColumns)
        jdbcTemplate.batchUpdate(sql, object : BatchPreparedStatementSetter {
            override fun setValues(ps: PreparedStatement, i: Int) {
                val currentData = data[i]
                currentData.setPreparedStatement(ps, insertColumns, idgen)
            }
            override fun getBatchSize(): Int {
                return data.size
            }
        })
    }

    private fun buildInsertQuery(entityName: String?, insertFields: List<String>): String {
        val query = StringBuilder(1000)
        query.append("INSERT INTO ")
        query.append("$entityName ")
        query.append("( ${insertFields.joinToString(",") { "`$it`" }} ) ")
        query.append("VALUES (${insertFields.joinToString(", ") { "?" }})")
        return query.toString()
    }
}
