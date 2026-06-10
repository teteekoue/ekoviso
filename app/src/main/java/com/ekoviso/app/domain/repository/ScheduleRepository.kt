package com.ekoviso.app.domain.repository

import com.ekoviso.app.data.local.entity.ScheduleEntity
import kotlinx.coroutines.flow.Flow

interface ScheduleRepository {
    fun getAllSchedules(): Flow<List<ScheduleEntity>>
    suspend fun addSchedule(schedule: ScheduleEntity): Long
    suspend fun deleteSchedule(id: Long)
    suspend fun updateScheduleStatus(id: Long, status: String)
}
