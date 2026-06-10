package com.ekoviso.app.data.repository

import com.ekoviso.app.data.local.dao.RecordingDao
import com.ekoviso.app.data.local.entity.RecordingEntity
import com.ekoviso.app.domain.repository.RecordingRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecordingRepositoryImpl @Inject constructor(
    private val recordingDao: RecordingDao
) : RecordingRepository {

    override fun getAllRecordings(): Flow<List<RecordingEntity>> {
        return recordingDao.getAllRecordings()
    }

    override suspend fun addRecording(recording: RecordingEntity): Long {
        return recordingDao.insert(recording)
    }

    override suspend fun deleteRecording(id: Long) {
        recordingDao.deleteById(id)
    }

    override suspend fun updateRecordingStatus(id: Long, status: String, size: Long) {
        recordingDao.updateStatus(id, status, size)
    }
}
