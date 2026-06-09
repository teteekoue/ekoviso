package com.ekoviso.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "schedules")
data class ScheduleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val channelName: String,
    val channelUrl: String,
    val startTimeMillis: Long,
    val durationMinutes: Int,
    val repeatType: String = "once", // once, daily, weekly
    val status: String = "waiting" // waiting, running, completed, failed
)
