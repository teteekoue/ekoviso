package com.ekoviso.app.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ekoviso.app.ui.theme.Teal600
import com.ekoviso.app.util.Constants

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onBack: () -> Unit) {
    var playlistUrl by remember { mutableStateOf(Constants.M3U_URL) }
    var defaultFormat by remember { mutableStateOf("mkv") }
    var defaultDuration by remember { mutableStateOf("60") }
    var videoQuality by remember { mutableStateOf("best") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Paramètres") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, "Retour")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // URL Playlist
            Text("URL de la playlist M3U", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = playlistUrl,
                onValueChange = { playlistUrl = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Text(
                "Ajoutez une nouvelle URL de playlist IPTV",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Format par défaut
            Text("Format d'enregistrement", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Row {
                listOf("mkv", "mp4").forEach { fmt ->
                    FilterChip(
                        selected = defaultFormat == fmt,
                        onClick = { defaultFormat = fmt },
                        label = { Text(fmt.uppercase()) },
                        modifier = Modifier.padding(end = 8.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Teal600
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Durée par défaut
            Text("Durée par défaut (minutes)", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = defaultDuration,
                onValueChange = { if (it.all { c -> c.isDigit() }) defaultDuration = it },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Qualité vidéo
            Text("Qualité vidéo", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Row {
                listOf("best" to "Max", "1080p" to "1080p", "720p" to "720p").forEach { (val_, label) ->
                    FilterChip(
                        selected = videoQuality == val_,
                        onClick = { videoQuality = val_ },
                        label = { Text(label) },
                        modifier = Modifier.padding(end = 8.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Teal600
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Infos app
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("EkoViso v1.0.0", style = MaterialTheme.typography.bodySmall)
                    Text("github.com/teteekoue/ekoviso", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}
