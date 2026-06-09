package com.ekoviso.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ekoviso.app.ui.theme.Teal600
import com.ekoviso.app.ui.theme.Teal900
import com.ekoviso.app.ui.theme.Slate50

data class DrawerItem(
    val label: String,
    val icon: @Composable () -> Unit,
    val route: String
)

@Composable
fun DrawerContent(
    currentRoute: String?,
    onItemClick: (String) -> Unit
) {
    val items = listOf(
        DrawerItem("Toutes les chaînes", { Icon(Icons.Filled.LiveTv, null) }, "channels"),
        DrawerItem("Mes enregistrements", { Icon(Icons.Filled.VideoLibrary, null) }, "recordings"),
        DrawerItem("Programmes", { Icon(Icons.Filled.Schedule, null) }, "schedules"),
        DrawerItem("Paramètres", { Icon(Icons.Filled.Settings, null) }, "settings"),
    )

    ModalDrawerSheet(modifier = Modifier.width(280.dp)) {
        // En-tête
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Teal900)
                .padding(24.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Column {
                Text(
                    "EkoViso",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Slate50,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "Your TV, Your Way",
                    style = MaterialTheme.typography.bodySmall,
                    color = Slate50.copy(alpha = 0.7f)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Items de navigation
        items.forEach { item ->
            val selected = currentRoute == item.route
            NavigationDrawerItem(
                icon = item.icon,
                label = { Text(item.label) },
                selected = selected,
                onClick = { onItemClick(item.route) },
                modifier = Modifier.padding(horizontal = 12.dp),
                colors = NavigationDrawerItemDefaults.colors(
                    selectedContainerColor = Teal600.copy(alpha = 0.3f),
                    unselectedContainerColor = MaterialTheme.colorScheme.surface,
                    selectedTextColor = Slate50,
                    unselectedTextColor = Slate50.copy(alpha = 0.8f),
                    selectedIconColor = Teal600,
                    unselectedIconColor = Slate50.copy(alpha = 0.6f)
                )
            )
        }
    }
}
