package com.ekoviso.app.data.local.dao

import androidx.room.*
import com.ekoviso.app.data.local.entity.ChannelEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChannelDao {
    @Query("SELECT * FROM channels ORDER BY name ASC")
    fun getAllChannels(): Flow<List<ChannelEntity>>

    @Query("SELECT * FROM channels WHERE name LIKE '%' || :query || '%' OR `group` LIKE '%' || :query || '%' ORDER BY name ASC")
    fun searchChannels(query: String): Flow<List<ChannelEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(channels: List<ChannelEntity>)

    @Query("DELETE FROM channels")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM channels")
    suspend fun count(): Int
}
