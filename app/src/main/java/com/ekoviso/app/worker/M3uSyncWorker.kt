package com.ekoviso.app.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.ekoviso.app.domain.repository.ChannelRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.concurrent.TimeUnit

@HiltWorker
class M3uSyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val channelRepository: ChannelRepository
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return channelRepository.refreshChannels().fold(
            onSuccess = { Result.success() },
            onFailure = { Result.retry() }
        )
    }

    companion object {
        fun schedule(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val request = PeriodicWorkRequestBuilder<M3uSyncWorker>(
                24, TimeUnit.HOURS
            )
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(
                    "m3u_sync",
                    ExistingPeriodicWorkPolicy.KEEP,
                    request
                )
        }
    }
}
