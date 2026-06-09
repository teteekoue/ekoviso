package com.ekoviso.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ekoviso.app.data.local.dao.ChannelDao
import com.ekoviso.app.data.local.dao.RecordingDao
import com.ekoviso.app.data.local.dao.ScheduleDao
import com.ekoviso.app.data.local.entity.ChannelEntity
import com.ekoviso.app.data.local.entity.RecordingEntity
import com.ekoviso.app.data.local.entity.ScheduleEntity

@Database(
    entities = [ChannelEntity::class, RecordingEntity::class, ScheduleEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun channelDao(): ChannelDao
    abstract fun recordingDao(): RecordingDao
    abstract fun scheduleDao(): ScheduleDao
}
