package com.ekoviso.app.ui.screens.channels

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
class ChannelsViewModel @Inject constructor(
    private val channelRepository: ChannelRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    val filteredChannels: StateFlow<List<ChannelEntity>> = combine(
        channelRepository.getAllChannels(),
        _searchQuery.debounce(300)
    ) { channels, query ->
        if (query.isBlank()) {
            channels
        } else {
            fuzzySearch(channels, query)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun loadChannels() {
        viewModelScope.launch {
            _isLoading.value = true
            val count = channelRepository.getChannelCount()
            if (count == 0) {
                channelRepository.refreshChannels()
            }
            _isLoading.value = false
        }
    }

    fun refreshChannels() {
        viewModelScope.launch {
            _isLoading.value = true
            channelRepository.refreshChannels()
            _isLoading.value = false
        }
    }

    private fun fuzzySearch(channels: List<ChannelEntity>, query: String): List<ChannelEntity> {
        val queryNorm = normalize(query)
        val queryWords = queryNorm.split(" ").filter { it.isNotBlank() }

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
                    else {
                        val wordMatch = queryWords.count { qw ->
                            nameNorm.split(" ").any { nw -> similarity(qw, nw) > 0.8 }
                        }
                        if (wordMatch > 0) score = 0.2 * (wordMatch.toDouble() / queryWords.size)
                    }
                }
            }

            Pair(channel, score)
        }
        .filter { it.second > 0 }
        .sortedByDescending { it.second }
        .map { it.first }
    }
}
