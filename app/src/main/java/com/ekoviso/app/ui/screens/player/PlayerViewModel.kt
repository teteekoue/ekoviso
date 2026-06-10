package com.ekoviso.app.ui.screens.player

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.ekoviso.app.data.local.entity.RecordingEntity
import com.ekoviso.app.domain.repository.RecordingRepository
import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.ReturnCode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.File
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val recordingRepository: RecordingRepository
) : ViewModel() {

    var player: ExoPlayer? = null
        private set

    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording

    private val _showRecordDialog = MutableStateFlow(false)
    val showRecordDialog: StateFlow<Boolean> = _showRecordDialog

    private var recordingJob: Job? = null
    private var recordingId: Long? = null
    private var outputFile: File? = null

    fun initializePlayer(context: Context, url: String) {
        player = ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(url))
            prepare()
            playWhenReady = true
        }
    }

    fun toggleRecordDialog() {
        if (_isRecording.value) {
            stopRecording()
        } else {
            _showRecordDialog.value = !_showRecordDialog.value
        }
    }

    fun startRecording(url: String, channelName: String, durationMinutes: Int, format: String) {
        _showRecordDialog.value = false
        _isRecording.value = true

        val dir = android.os.Environment.getExternalStoragePublicDirectory(
            android.os.Environment.DIRECTORY_MOVIES
        )
        val timestamp = java.text.SimpleDateFormat("yyyyMMdd_HHmmss").format(java.util.Date())
        val fileName = "${timestamp}_${channelName.replace(Regex("[^a-zA-Z0-9_\\-]"), "_")}.$format"
        outputFile = File(dir, fileName)

        val recording = RecordingEntity(
            channelName = channelName,
            channelUrl = url,
            filePath = outputFile!!.absolutePath,
            fileName = fileName,
            format = format,
            durationMinutes = durationMinutes,
            status = "in_progress"
        )

        recordingJob = viewModelScope.launch(Dispatchers.IO) {
            recordingId = recordingRepository.addRecording(recording)
            
            val ffmpegCommand = "-y -reconnect 1 -reconnect_streamed 1 -reconnect_delay_max 10 -i \"$url\" -c copy -t ${durationMinutes * 60} \"${outputFile!!.absolutePath}\""
            
            val session = FFmpegKit.execute(ffmpegCommand)
            val returnCode = session.returnCode
            val size = outputFile?.length() ?: 0
            
            if (ReturnCode.isSuccess(returnCode)) {
                recordingId?.let { recordingRepository.updateRecordingStatus(it, "completed", size) }
            } else {
                recordingId?.let { recordingRepository.updateRecordingStatus(it, "failed", size) }
            }
            
            withContext(Dispatchers.Main) {
                _isRecording.value = false
            }
        }
    }

    private fun stopRecording() {
        recordingJob?.cancel()
        _isRecording.value = false
    }

    fun releasePlayer() {
        player?.release()
        player = null
    }
}
