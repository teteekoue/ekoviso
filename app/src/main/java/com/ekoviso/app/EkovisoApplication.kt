package com.ekoviso.app

import android.app.Application
import com.ekoviso.app.worker.M3uSyncWorker
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class EkovisoApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        M3uSyncWorker.schedule(this)
    }
}
