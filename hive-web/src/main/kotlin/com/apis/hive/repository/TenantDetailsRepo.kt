package com.apis.hive.repository

import com.apis.hive.entity.TenantDetails
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TenantDetailsRepo: JpaRepository<TenantDetails,Long> {

}