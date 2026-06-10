package com.ekoviso.app.ui.screens.live

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ekoviso.app.ui.theme.Teal600
import com.ekoviso.app.ui.theme.Red500

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveScreen(
    modifier: Modifier = Modifier,
    onChannelClick: (String, String) -> Unit,
    viewModel: LiveViewModel = hiltViewModel()
) {
    val channels by viewModel.filteredChannels.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val channelCount by viewModel.channelCount.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadChannels()
    }

    Column(modifier = modifier.fillMaxSize()) {
        // Barre de recherche
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.onSearchQueryChanged(it) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            placeholder = { Text("Rechercher une chaîne...") },
            leadingIcon = { Icon(Icons.Filled.Search, null) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { viewModel.onSearchQueryChanged("") }) {
                        Icon(Icons.Filled.Clear, null)
                    }
                }
            },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Teal600,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        )

        // Compteur
        if (channelCount > 0) {
            Text(
                "$channelCount chaînes disponibles",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                style = MaterialTheme.typography.bodySmall,
                color = Teal600
            )
        }

        when {
            isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = Teal600)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Chargement des chaînes...")
                    }
                }
            }
            errorMessage != null -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Filled.Error, null, tint = Red500, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(errorMessage ?: "Erreur inconnue", color = Red500)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.refreshChannels() }) {
                            Text("Réessayer")
                        }
                    }
                }
            }
            channels.isEmpty() && !isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Button(onClick = { viewModel.refreshChannels() }) {
                            Text("Charger les chaînes")
                        }
                    }
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    items(channels, key = { it.id }) { channel ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable { onChannelClick(channel.name, channel.url) },
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Filled.PlayCircle,
                                    null,
                                    tint = Teal600,
                                    modifier = Modifier.size(36.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = channel.name,
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = channel.group,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Teal600
                                    )
                                }
                                Icon(
                                    Icons.Filled.ChevronRight,
                                    null,
                                    tint = MaterialTheme.colorScheme.outline
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
