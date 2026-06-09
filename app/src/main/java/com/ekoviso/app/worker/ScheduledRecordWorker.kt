package com.ekoviso.app.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.ekoviso.app.data.local.entity.RecordingEntity
import com.ekoviso.app.domain.repository.RecordingRepository
import com.ekoviso.app.domain.repository.ScheduleRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

@HiltWorker
class ScheduledRecordWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val scheduleRepository: ScheduleRepository,
    private val recordingRepository: RecordingRepository
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val scheduleId = inputData.getLong("schedule_id", -1)
        if (scheduleId == -1L) return Result.failure()

        val schedules = scheduleRepository.getAllSchedules().first()
        val schedule = schedules.find { it.id == scheduleId } ?: return Result.failure()

        if (schedule.status != "waiting") return Result.success()

        scheduleRepository.updateScheduleStatus(scheduleId, "running")

        val dir = android.os.Environment.getExternalStoragePublicDirectory(
            android.os.Environment.DIRECTORY_MOVIES
        )
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val fileName = "${timestamp}_${schedule.channelName.replace(Regex("[^a-zA-Z0-9_\\-]"), "_")}.mkv"
        val outputFile = File(dir, fileName)

        val recording = RecordingEntity(
            channelName = schedule.channelName,
            channelUrl = schedule.channelUrl,
            filePath = outputFile.absolutePath,
            fileName = fileName,
            format = "mkv",
            durationMinutes = schedule.durationMinutes,
            status = "in_progress"
        )

        val recordingId = recordingRepository.addRecording(recording)

        return try {
            val cmd = arrayOf(
                "ffmpeg", "-y",
                "-reconnect", "1",
                "-reconnect_streamed", "1",
                "-reconnect_delay_max", "10",
                "-i", schedule.channelUrl,
                "-c", "copy",
                "-t", (schedule.durationMinutes * 60).toString(),
                outputFile.absolutePath
            )
            val process = Runtime.getRuntime().exec(cmd)
            val rc = process.waitFor()

            if (rc == 0) {
                recordingRepository.updateRecordingStatus(recordingId, "completed", outputFile.length())
                scheduleRepository.updateScheduleStatus(scheduleId, "completed")
            } else {
                recordingRepository.updateRecordingStatus(recordingId, "failed", 0)
                scheduleRepository.updateScheduleStatus(scheduleId, "failed")
            }

            if (schedule.repeatType == "daily") {
                scheduleNext(schedule, TimeUnit.DAYS.toMillis(1))
            } else if (schedule.repeatType == "weekly") {
                scheduleNext(schedule, TimeUnit.DAYS.toMillis(7))
            }

            Result.success()
        } catch (e: Exception) {
            recordingRepository.updateRecordingStatus(recordingId, "failed", 0)
            scheduleRepository.updateScheduleStatus(scheduleId, "failed")
            Result.failure()
        }
    }

    private fun scheduleNext(schedule: com.ekoviso.app.data.local.entity.ScheduleEntity, offsetMillis: Long) {
        val data = Data.Builder()
            .putLong("schedule_id", schedule.id)
            .build()

        val request = OneTimeWorkRequestBuilder<ScheduledRecordWorker>()
            .setInitialDelay(offsetMillis, TimeUnit.MILLISECONDS)
            .setInputData(data)
            .build()

        WorkManager.getInstance(applicationContext)
            .enqueue(request)
    }
}
