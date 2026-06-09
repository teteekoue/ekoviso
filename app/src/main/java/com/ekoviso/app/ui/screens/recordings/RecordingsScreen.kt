package com.ekoviso.app.ui.screens.recordings

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
import com.ekoviso.app.ui.theme.Orange500
import com.ekoviso.app.ui.theme.Slate50
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordingsScreen(
    onBack: () -> Unit,
    viewModel: RecordingsViewModel = hiltViewModel()
) {
    val recordings by viewModel.recordings.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mes enregistrements") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, "Retour")
                    }
                }
            )
        }
    ) { padding ->
        if (recordings.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("Aucun enregistrement", style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            LazyColumn(modifier = Modifier.padding(padding)) {
                items(recordings, key = { it.id }) { recording ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                            .clickable { /* lecture */ },
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Filled.PlayCircle, null, tint = Orange500)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(recording.fileName, color = Slate50)
                                Text(
                                    "${SimpleDateFormat("dd/MM/yyyy HH:mm").format(Date(recording.createdAt))} • " +
                                    "${"%.1f".format(recording.sizeBytes / 1_048_576.0)} Mo",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                            IconButton(onClick = { viewModel.deleteRecording(recording.id) }) {
                                Icon(Icons.Filled.Delete, "Supprimer")
                            }
                        }
                    }
                }
            }
        }
    }
}
