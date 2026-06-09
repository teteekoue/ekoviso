package com.ekoviso.app.ui.screens.recordings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ekoviso.app.data.local.entity.RecordingEntity
import com.ekoviso.app.domain.repository.RecordingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecordingsViewModel @Inject constructor(
    private val recordingRepository: RecordingRepository
) : ViewModel() {

    val recordings: StateFlow<List<RecordingEntity>> = recordingRepository
        .getAllRecordings()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun deleteRecording(id: Long) {
        viewModelScope.launch {
            recordingRepository.deleteRecording(id)
        }
    }
}
