package com.ekoviso.app.data.local.dao

import androidx.room.*
import com.ekoviso.app.data.local.entity.RecordingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecordingDao {
    @Query("SELECT * FROM recordings ORDER BY createdAt DESC")
    fun getAllRecordings(): Flow<List<RecordingEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(recording: RecordingEntity): Long

    @Delete
    suspend fun delete(recording: RecordingEntity)

    @Query("DELETE FROM recordings WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("UPDATE recordings SET status = :status, sizeBytes = :size WHERE id = :id")
    suspend fun updateStatus(id: Long, status: String, size: Long)
}
