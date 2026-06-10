package com.ekoviso.app.domain.repository

import com.ekoviso.app.data.local.entity.ChannelEntity
import kotlinx.coroutines.flow.Flow

interface ChannelRepository {
    fun getAllChannels(): Flow<List<ChannelEntity>>
    suspend fun refreshChannels(url: String = "https://iptv-org.github.io/iptv/index.m3u"): Result<Int>
    suspend fun getChannelCount(): Int
}
