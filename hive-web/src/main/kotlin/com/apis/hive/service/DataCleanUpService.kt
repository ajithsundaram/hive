package com.apis.hive.service

import com.apis.hive.configuration.BeanConfig
import com.apis.hive.repository.DataRepo
import org.quartz.Job
import org.quartz.JobBuilder
import org.quartz.JobExecutionContext
import org.quartz.SimpleScheduleBuilder
import org.quartz.Trigger
import org.quartz.TriggerBuilder
import org.quartz.impl.StdSchedulerFactory
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
@Service
class DataCleanUpService( @Value("\${app.hive.cleanup.intervalInMins}")
                          private val cleanupIntervalInMins: String) {
    init {
        val jobDetail = JobBuilder.newJob(CleanUpJob::class.java)
            .withIdentity("cleanupJob", "group1")
            .build()
        val trigger: Trigger = TriggerBuilder.newTrigger()
            .withIdentity("cleanupTrigger", "group1")
            .startNow()
            .withSchedule(
                SimpleScheduleBuilder.simpleSchedule()
                    .withIntervalInMinutes(cleanupIntervalInMins.toInt())
                    .repeatForever()
            )
            .build()
        val scheduler = StdSchedulerFactory.getDefaultScheduler()
        scheduler.scheduleJob(jobDetail, trigger)
        scheduler.start()
    }
}
class CleanUpJob: Job {
    private val logger = LoggerFactory.getLogger(this::class.java)
    override fun execute(context: JobExecutionContext?) {
        try {
            logger.info("cleanup started")
            val dataRepo = BeanConfig.getBean(DataRepo::class.java)
            dataRepo.cleanupExpiredKeys()
            logger.info("cleanup finished")
        } catch (ex: Exception) {
            logger.error("exception while cleanup keys",ex)
        }
    }
}