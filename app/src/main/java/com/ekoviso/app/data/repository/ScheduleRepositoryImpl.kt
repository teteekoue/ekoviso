package com.ekoviso.app.data.repository

import com.ekoviso.app.data.local.dao.ScheduleDao
import com.ekoviso.app.data.local.entity.ScheduleEntity
import com.ekoviso.app.domain.repository.ScheduleRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ScheduleRepositoryImpl @Inject constructor(
    private val scheduleDao: ScheduleDao
) : ScheduleRepository {

    override fun getAllSchedules(): Flow<List<ScheduleEntity>> {
        return scheduleDao.getAllSchedules()
    }

    override suspend fun addSchedule(schedule: ScheduleEntity): Long {
        return scheduleDao.insert(schedule)
    }

    override suspend fun deleteSchedule(id: Long) {
        scheduleDao.deleteById(id)
    }

    override suspend fun updateScheduleStatus(id: Long, status: String) {
        scheduleDao.updateStatus(id, status)
    }
}
