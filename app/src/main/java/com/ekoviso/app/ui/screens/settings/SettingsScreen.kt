package com.ekoviso.app.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ekoviso.app.ui.theme.Teal600

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onBack: () -> Unit) {
    var defaultDuration by remember { mutableStateOf("60") }
    var defaultFormat by remember { mutableStateOf("mkv") }

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
        ) {
            Text("Format par défaut", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Row {
                listOf("mkv", "mp4", "ts").forEach { fmt ->
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

            Text("Durée par défaut (minutes)", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = defaultDuration,
                onValueChange = { if (it.all { c -> c.isDigit() }) defaultDuration = it },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))

            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("EkoViso v1.0.0", style = MaterialTheme.typography.bodySmall)
                    Text("github.com/teteekoue/ekoviso", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}
