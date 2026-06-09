package com.ekoviso.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recordings")
data class RecordingEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val channelName: String,
    val channelUrl: String,
    val filePath: String,
    val fileName: String,
    val format: String,
    val durationMinutes: Int,
    val sizeBytes: Long = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val status: String = "completed" // completed, failed, in_progress
)
