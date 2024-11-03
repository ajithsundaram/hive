package com.apis.hive.service

import com.apis.hive.util.AppConstant
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class HiveAPIService {

    @Autowired
    private lateinit var restTemplate: RestTemplate

    @Value("\${app.hive.schemaManager.url}")
    private lateinit var schemaManagerUrl: String

    @Value("\${app.hive.interService.apiKey}")
    private lateinit var iscToken: String

    fun sendPostRequest(tenantId: String): Map<String, Any?>? {
        val url = schemaManagerUrl + AppConstant.ASSOCIATE_SCHEMA_ENDPOINT
        val headers = HttpHeaders()
        headers.set("Content-Type", "application/json")
        headers.set("Authorization", "Bearer $iscToken")

        val requestBody = mutableMapOf(AppConstant.TENANT_ID to tenantId)
        val requestEntity = HttpEntity<Map<String, Any>>(requestBody, headers)
        val response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, Any::class.java)
        println("Status Code:${response.statusCode},response :: ${response.body}")
        return response.body as Map<String, Any?>?
    }
}
