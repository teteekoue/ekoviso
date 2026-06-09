package com.ekoviso.app.domain.repository

import com.ekoviso.app.data.local.entity.RecordingEntity
import kotlinx.coroutines.flow.Flow

interface RecordingRepository {
    fun getAllRecordings(): Flow<List<RecordingEntity>>
    suspend fun addRecording(recording: RecordingEntity): Long
    suspend fun deleteRecording(id: Long)
    suspend fun updateRecordingStatus(id: Long, status: String, size: Long)
}
