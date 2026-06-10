package com.ekoviso.app.data.repository

import com.ekoviso.app.data.local.dao.ChannelDao
import com.ekoviso.app.data.local.entity.ChannelEntity
import com.ekoviso.app.data.remote.M3uApi
import com.ekoviso.app.data.remote.M3uParser
import com.ekoviso.app.domain.repository.ChannelRepository
import com.ekoviso.app.util.Constants
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChannelRepositoryImpl @Inject constructor(
    private val channelDao: ChannelDao
) : ChannelRepository {

    override fun getAllChannels(): Flow<List<ChannelEntity>> {
        return channelDao.getAllChannels()
    }

    override suspend fun refreshChannels(url: String): Result<Int> {
        return try {
            val result = M3uApi.fetchPlaylist(url)
            result.map { content ->
                val channels = M3uParser.parse(content)
                if (channels.isNotEmpty()) {
                    channelDao.deleteAll()
                    channelDao.insertAll(channels)
                }
                channels.size
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getChannelCount(): Int {
        return channelDao.count()
    }
}
