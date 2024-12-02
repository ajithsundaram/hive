package com.apis.hive.configuration

import org.springframework.beans.BeansException
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.stereotype.Component

@Component
object BeanConfig: ApplicationContextAware {
    private var context: ApplicationContext? = null
    fun <T> getBean(beanClass: Class<T>): T {
        return context!!.getBean(beanClass)
    }

    @Throws(BeansException::class)
    override fun setApplicationContext(applicationContext: ApplicationContext) {
        context = applicationContext
    }
}