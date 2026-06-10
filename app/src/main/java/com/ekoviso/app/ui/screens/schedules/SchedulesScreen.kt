package com.ekoviso.app.ui.screens.schedules

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
import com.ekoviso.app.data.local.entity.ChannelEntity
import com.ekoviso.app.data.local.entity.ScheduleEntity
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
    val channels by viewModel.channels.collectAsState()
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
                                Text("${schedule.durationMinutes} min • ${schedule.repeatType}", style = MaterialTheme.typography.bodySmall)
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

    if (showDialog) {
        AddScheduleDialog(
            channels = channels,
            onDismiss = { showDialog = false },
            onConfirm = { schedule ->
                viewModel.addSchedule(schedule)
                showDialog = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddScheduleDialog(
    channels: List<ChannelEntity>,
    onDismiss: () -> Unit,
    onConfirm: (ScheduleEntity) -> Unit
) {
    var selectedChannel by remember { mutableStateOf<ChannelEntity?>(null) }
    var duration by remember { mutableStateOf("60") }
    var repeatType by remember { mutableStateOf("once") }
    var expanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Programmer un enregistrement") },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Sélection de chaîne
                Text("Chaîne :", style = MaterialTheme.typography.labelLarge)
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = selectedChannel?.name ?: "Choisir une chaîne",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        channels.take(20).forEach { channel ->
                            DropdownMenuItem(
                                text = { Text(channel.name) },
                                onClick = {
                                    selectedChannel = channel
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Durée
                Text("Durée (minutes) :", style = MaterialTheme.typography.labelLarge)
                OutlinedTextField(
                    value = duration,
                    onValueChange = { if (it.all { c -> c.isDigit() }) duration = it },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Répétition
                Text("Répétition :", style = MaterialTheme.typography.labelLarge)
                Row {
                    listOf("once" to "Une fois", "daily" to "Quotidien", "weekly" to "Hebdo").forEach { (type, label) ->
                        FilterChip(
                            selected = repeatType == type,
                            onClick = { repeatType = type },
                            label = { Text(label) },
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    selectedChannel?.let {
                        onConfirm(
                            ScheduleEntity(
                                channelName = it.name,
                                channelUrl = it.url,
                                startTimeMillis = System.currentTimeMillis() + 60000, // Dans 1 minute par défaut pour le test
                                durationMinutes = duration.toIntOrNull() ?: 60,
                                repeatType = repeatType
                            )
                        )
                    }
                },
                enabled = selectedChannel != null
            ) {
                Text("Valider")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Annuler") }
        }
    )
}
