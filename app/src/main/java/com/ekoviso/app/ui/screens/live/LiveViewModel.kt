package com.ekoviso.app.ui.screens.live

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ekoviso.app.data.local.entity.ChannelEntity
import com.ekoviso.app.domain.repository.ChannelRepository
import com.ekoviso.app.util.normalize
import com.ekoviso.app.util.similarity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class LiveViewModel @Inject constructor(
    private val channelRepository: ChannelRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _channelCount = MutableStateFlow(0)
    val channelCount: StateFlow<Int> = _channelCount

    val filteredChannels: StateFlow<List<ChannelEntity>> = combine(
        channelRepository.getAllChannels(),
        _searchQuery.debounce(300)
    ) { channels, query ->
        if (query.isBlank()) channels
        else fuzzySearch(channels, query)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun loadChannels() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val count = channelRepository.getChannelCount()
                _channelCount.value = count
                if (count == 0) {
                    refreshChannels()
                }
            } catch (e: Exception) {
                _errorMessage.value = "Erreur : ${e.message}"
            }
            _isLoading.value = false
        }
    }

    fun refreshChannels() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            channelRepository.refreshChannels().fold(
                onSuccess = { count ->
                    _channelCount.value = count
                    _errorMessage.value = null
                },
                onFailure = { e ->
                    _errorMessage.value = "Échec du chargement : ${e.message}"
                }
            )
            _isLoading.value = false
        }
    }

    private fun fuzzySearch(channels: List<ChannelEntity>, query: String): List<ChannelEntity> {
        val queryNorm = normalize(query)
        val queryWords = queryNorm.split(" ").filter { it.isNotBlank() }
        if (queryWords.isEmpty()) return channels

        return channels.map { channel ->
            val nameNorm = normalize(channel.name)
            val groupNorm = normalize(channel.group)
            var score = 0.0
            when {
                queryNorm in nameNorm -> score = 1.0
                queryNorm in groupNorm -> score = 0.9
                queryWords.all { it in nameNorm } -> score = 0.8
                queryWords.all { it in nameNorm || it in groupNorm } -> score = 0.7
                queryWords.any { it in nameNorm } -> score = 0.5
                queryWords.any { it in groupNorm } -> score = 0.4
                else -> {
                    val sim = similarity(queryNorm, nameNorm.take(queryNorm.length + 10))
                    if (sim > 0.7) score = 0.3 * sim
                }
            }
            Pair(channel, score)
        }
        .filter { it.second > 0 }
        .sortedByDescending { it.second }
        .map { it.first }
    }
}
