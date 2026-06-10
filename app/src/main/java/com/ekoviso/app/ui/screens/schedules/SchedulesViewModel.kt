package com.ekoviso.app.ui.screens.schedules

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ekoviso.app.data.local.entity.ScheduleEntity
import com.ekoviso.app.domain.repository.ScheduleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.ekoviso.app.data.local.entity.ChannelEntity
import com.ekoviso.app.domain.repository.ChannelRepository
...
@HiltViewModel
class SchedulesViewModel @Inject constructor(
    private val scheduleRepository: ScheduleRepository,
    private val channelRepository: ChannelRepository
) : ViewModel() {

    val schedules: StateFlow<List<ScheduleEntity>> = scheduleRepository
        .getAllSchedules()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val channels: StateFlow<List<ChannelEntity>> = channelRepository
        .getAllChannels()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addSchedule(schedule: ScheduleEntity) {
        viewModelScope.launch {
            scheduleRepository.addSchedule(schedule)
        }
    }

    fun deleteSchedule(id: Long) {
...
        viewModelScope.launch {
            scheduleRepository.deleteSchedule(id)
        }
    }
}
