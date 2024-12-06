package com.apis.hive.controller

import com.apis.hive.dto.DataDTO
import com.apis.hive.exception.KeyNotFoundException
import com.apis.hive.service.KeyValueDataService
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
    fun getKey(@PathVariable key: String): ResponseEntity<DataDTO> {
        return try {
            val result = keyValueDataService.findDataByKey(key)
            if (result != null) {
                val resultDTO = DataDTO().apply {
                    this.dataKey = result.id?.dataKey!!
                    this.dataValue = result.dataValue
                }
                ResponseEntity(resultDTO, HttpStatusCode.valueOf(200))
            } else throw KeyNotFoundException("key not found $key")
        } catch (ex: Exception) {
            logger.info("Exception while processing getKey for key $key", ex)
            throw ex
        }
    }

    @PostMapping("/object")
    fun putKey(@RequestBody body: Map<String, Any?>): ResponseEntity<Any?> {
        keyValueDataService.bulkSaveData(listOf(body))
        return ResponseEntity.ok().build()
    }

    @PostMapping("/batch/object")
    fun putKeys(@RequestBody body: List<Map<String, Any?>>): ResponseEntity<Any?> {
        keyValueDataService.bulkSaveData(body)
        return ResponseEntity.ok().build()
    }

    @DeleteMapping("/object/{key}")
    fun deleteKey(@PathVariable key: String): ResponseEntity<Any?> {
        keyValueDataService.deleteDataByKey(key)
        return ResponseEntity.ok().build()
    }
}
