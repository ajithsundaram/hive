package com.apis.hive.repo

import com.apis.hive.util.AppConstant
import java.lang.StringBuilder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Service

@Service
class ReadRepository {

    @Autowired
    private lateinit var jdbcTemplate: JdbcTemplate

    fun <T> find(entityName: String, selectFields: Set<String>, condition: String?, conditionParams: List<Any>, rowMapper: RowMapper<T>): T? {
        val selectQuery = buildSelectQuery(entityName, selectFields, condition)
        val result = jdbcTemplate.query(selectQuery, conditionParams.toTypedArray(), rowMapper)
        return result.firstOrNull()
    }

    fun findAggregate(entityName: String,field: String?,aggregateFunction:AggregateFunction, condition: String?, conditionParams: List<Any?> ): Any? {
        val query = buildCountQuery(entityName,field,aggregateFunction,condition)
        return jdbcTemplate.queryForObject(query, Int::class.java, *conditionParams.toTypedArray()) ?: 0
    }


    private fun buildCountQuery(entityName: String,aggregateField: String?,aggregateFunction: AggregateFunction, condition: String?): String {
        val query = StringBuilder(QueryConstant.SELECT_QUERY_LENGTH)
        query.append("${QueryConstant.SELECT} ")
        if (aggregateField == null) {
            query.append("${aggregateFunction.name}(*)")
        } else {
            query.append("${aggregateFunction.name}($aggregateField)")
        }
        query.append("${QueryConstant.FROM} $entityName ")
        if (!condition.isNullOrBlank()) {
            query.append("${QueryConstant.WHERE} $condition")
        }
        return query.toString()
    }

    private fun buildSelectQuery(entityName: String, selectFields: Set<String>, condition: String?): String {
        val query = StringBuilder(QueryConstant.SELECT_QUERY_LENGTH)
        query.append("${QueryConstant.SELECT} ")
        if (selectFields.isNotEmpty()) {
            query.append(selectFields.map { "`$it`" }.joinToString(","))
        } else {
            query.append("* ")
        }
        query.append("${QueryConstant.FROM} $entityName ")
        if (!condition.isNullOrBlank()) {
            query.append("${QueryConstant.WHERE} $condition")
        }
        return query.toString()
    }

}

object QueryConstant {
    const val SELECT = "SELECT"
    const val FROM = "FROM"
    const val WHERE = "WHERE"
    const val SELECT_QUERY_LENGTH = 1000

    const val KEY_CONDITION = "`${AppConstant.DATA_KEY}` = ?"
    const val TTL_CONDITION = "(`${AppConstant.DATA_TTL}` > ? OR `${AppConstant.DATA_TTL}` is null)"

    const val TENANT_ID_CONDITION = "`${AppConstant.TENANT_DETAILS_TENANT_ID}` = ?"

}

enum class AggregateFunction {
    COUNT
}
