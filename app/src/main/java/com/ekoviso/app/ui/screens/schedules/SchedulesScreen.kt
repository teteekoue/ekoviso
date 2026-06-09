package com.ekoviso.app.ui.screens.schedules

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
import com.ekoviso.app.ui.theme.Teal600
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SchedulesScreen(
    onBack: () -> Unit,
    viewModel: SchedulesViewModel = hiltViewModel()
) {
    val schedules by viewModel.schedules.collectAsState()
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Programmes") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, "Retour")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog = true },
                containerColor = Teal600
            ) {
                Icon(Icons.Filled.Add, "Ajouter", tint = MaterialTheme.colorScheme.onPrimary)
            }
        }
    ) { padding ->
        if (schedules.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("Aucun programme", style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            LazyColumn(modifier = Modifier.padding(padding)) {
                items(schedules, key = { it.id }) { schedule ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val statusIcon = when (schedule.status) {
                                "completed" -> Icons.Filled.CheckCircle
                                "failed" -> Icons.Filled.Error
                                "running" -> Icons.Filled.PlayCircle
                                else -> Icons.Filled.Schedule
                            }
                            Icon(statusIcon, null, tint = Orange500)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(schedule.channelName)
                                Text(
                                    SimpleDateFormat("dd/MM/yyyy HH:mm").format(Date(schedule.startTimeMillis)),
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Text("${schedule.durationMinutes} min", style = MaterialTheme.typography.bodySmall)
                            }
                            IconButton(onClick = { viewModel.deleteSchedule(schedule.id) }) {
                                Icon(Icons.Filled.Delete, "Supprimer")
                            }
                        }
                    }
                }
            }
        }
    }
}
