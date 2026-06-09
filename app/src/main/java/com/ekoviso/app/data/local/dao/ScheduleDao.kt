package com.ekoviso.app.data.local.dao

import androidx.room.*
import com.ekoviso.app.data.local.entity.ScheduleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ScheduleDao {
    @Query("SELECT * FROM schedules ORDER BY startTimeMillis ASC")
    fun getAllSchedules(): Flow<List<ScheduleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(schedule: ScheduleEntity): Long

    @Delete
    suspend fun delete(schedule: ScheduleEntity)

    @Query("DELETE FROM schedules WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("UPDATE schedules SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: Long, status: String)
}
