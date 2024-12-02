package com.apis.hive.entity

import jakarta.persistence.MappedSuperclass
import jakarta.persistence.PostLoad
import jakarta.persistence.PrePersist
import org.springframework.data.domain.Persistable

@MappedSuperclass
abstract class AbstractEntity<ID> : Persistable<ID> {
    @Transient
    private var isNew = true
    // set isNew() to true so that we can reduce query which is used to check whether the entity with same id is already exist while inserting as new
    override fun isNew(): Boolean {
        return isNew
    }
    @PrePersist
    @PostLoad
    fun markNotNew() {
        this.isNew = false
    }
    abstract fun getPK():ID?
    override fun getId() = getPK()

}