package com.ekoviso.app.di

import android.content.Context
import androidx.room.Room
import com.ekoviso.app.data.local.AppDatabase
import com.ekoviso.app.data.local.dao.ChannelDao
import com.ekoviso.app.data.local.dao.RecordingDao
import com.ekoviso.app.data.local.dao.ScheduleDao
import com.ekoviso.app.data.repository.ChannelRepositoryImpl
import com.ekoviso.app.data.repository.RecordingRepositoryImpl
import com.ekoviso.app.data.repository.ScheduleRepositoryImpl
import com.ekoviso.app.domain.repository.ChannelRepository
import com.ekoviso.app.domain.repository.RecordingRepository
import com.ekoviso.app.domain.repository.ScheduleRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "ekoviso.db"
        ).build()
    }

    @Provides
    fun provideChannelDao(db: AppDatabase): ChannelDao = db.channelDao()

    @Provides
    fun provideRecordingDao(db: AppDatabase): RecordingDao = db.recordingDao()

    @Provides
    fun provideScheduleDao(db: AppDatabase): ScheduleDao = db.scheduleDao()

    @Provides
    @Singleton
    fun provideChannelRepository(impl: ChannelRepositoryImpl): ChannelRepository = impl

    @Provides
    @Singleton
    fun provideRecordingRepository(impl: RecordingRepositoryImpl): RecordingRepository = impl

    @Provides
    @Singleton
    fun provideScheduleRepository(impl: ScheduleRepositoryImpl): ScheduleRepository = impl
}
