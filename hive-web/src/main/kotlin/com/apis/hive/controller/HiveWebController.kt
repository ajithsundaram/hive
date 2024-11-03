package com.apis.hive.controller

import com.apis.hive.dto.KeyValueData
import com.apis.hive.exception.HiveException
import com.apis.hive.service.KeyValueDataService
import com.apis.hive.util.APIConstant
import com.apis.hive.util.ErrorConstants
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Component
@RestController
@RequestMapping("/api")
class HiveWebController {
    private val logger = LoggerFactory.getLogger(this::class.java)
    @Autowired
    private lateinit var keyValueDataService: KeyValueDataService

    @GetMapping("/object/{key}")
    fun getKey(@PathVariable key: String): ResponseEntity<KeyValueData> {
        return try {
            val result = keyValueDataService.findDataByKey(key)
            if (result != null) {
                val response = KeyValueData()
                response.key = key
                response.value = result.value
                ResponseEntity(response, null, HttpStatusCode.valueOf(200))
            } else throw HiveException(ErrorConstants.KEY_NOT_EXIST)
        } catch (ex: Exception) {
            logger.info("Exception while processing getKey for key $key", ex)
            throw ex
        }
    }

    @PostMapping("/object")
    fun putKey(@RequestBody body: Map<String, Any?>): ResponseEntity<Any?> {
        return try {
            keyValueDataService.bulkSaveData(listOf(body))
            ResponseEntity.ok().build()
        } catch (ex: Exception) {
            logger.info("Exception while processing saving key ${body[APIConstant.KEY]}", ex)
            throw ex
        }
    }

    @PostMapping("/batch/object")
    fun putKeys(@RequestBody body: List<Map<String, Any?>>): ResponseEntity<Any?> {
        try {
            keyValueDataService.bulkSaveData(body)
        } catch (ex: Exception) {
            logger.info("Exception while processing saving key ", ex)
            throw ex
        }
        return ResponseEntity.ok().build()
    }

    @DeleteMapping("/object/{key}")
    fun deleteKey(@PathVariable key: String): ResponseEntity<Any?> {
        return try {
            keyValueDataService.deleteDataByKey(key)
            ResponseEntity.ok().build()
        } catch (ex: Exception) {
            println("Exception while deleting key $key")
            throw ex
        }
    }
}
