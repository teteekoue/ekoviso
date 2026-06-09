package com.ekoviso.app.ui.screens.player

import android.app.PictureInPictureParams
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Rational
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.ekoviso.app.ui.theme.Orange500
import com.ekoviso.app.ui.theme.RecordRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerScreen(
    channelName: String,
    channelUrl: String,
    onBack: () -> Unit,
    viewModel: PlayerViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val isRecording by viewModel.isRecording.collectAsState()
    val showRecordDialog by viewModel.showRecordDialog.collectAsState()
    var showControls by remember { mutableStateOf(true) }

    BackHandler { onBack() }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        // ExoPlayer
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = viewModel.player
                    useController = false
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // Contrôles overlay
        if (showControls) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Barre supérieure
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Black.copy(alpha = 0.6f))
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, "Retour", tint = Color.White)
                    }
                    Text(channelName, color = Color.White, modifier = Modifier.weight(1f))
                    if (isRecording) {
                        Icon(Icons.Filled.FiberManualRecord, "REC", tint = RecordRed)
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Barre inférieure
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Black.copy(alpha = 0.6f))
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(
                        onClick = { viewModel.toggleRecordDialog() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isRecording) RecordRed else Orange500
                        )
                    ) {
                        Icon(
                            if (isRecording) Icons.Filled.Stop else Icons.Filled.FiberManualRecord,
                            null,
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (isRecording) "Arrêter" else "Enregistrer")
                    }
                }
            }
        }
    }

    // Dialogue d'enregistrement
    if (showRecordDialog) {
        RecordDialog(
            channelName = channelName,
            onDismiss = { viewModel.toggleRecordDialog() },
            onStartRecord = { duration, format ->
                viewModel.startRecording(channelUrl, channelName, duration, format)
            }
        )
    }

    LaunchedEffect(Unit) {
        viewModel.initializePlayer(context, channelUrl)
    }

    DisposableEffect(Unit) {
        onDispose { viewModel.releasePlayer() }
    }
}

@Composable
fun RecordDialog(
    channelName: String,
    onDismiss: () -> Unit,
    onStartRecord: (Int, String) -> Unit
) {
    var duration by remember { mutableIntStateOf(60) }
    var format by remember { mutableStateOf("mkv") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Enregistrer $channelName") },
        text = {
            Column {
                Text("Durée (minutes) :")
                OutlinedTextField(
                    value = duration.toString(),
                    onValueChange = { it.toIntOrNull()?.let { v -> duration = v } },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text("Format :")
                Row {
                    listOf("mkv", "mp4", "ts").forEach { fmt ->
                        FilterChip(
                            selected = format == fmt,
                            onClick = { format = fmt },
                            label = { Text(fmt.uppercase()) },
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { onStartRecord(duration, format) }) {
                Text("Démarrer")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Annuler") }
        }
    )
}
