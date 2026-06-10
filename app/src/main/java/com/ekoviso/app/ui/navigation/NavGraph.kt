package com.ekoviso.app.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.ekoviso.app.ui.screens.live.LiveScreen
import com.ekoviso.app.ui.screens.player.PlayerScreen
import com.ekoviso.app.ui.screens.recordings.RecordingsScreen
import com.ekoviso.app.ui.screens.schedules.SchedulesScreen
import com.ekoviso.app.ui.screens.settings.SettingsScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavGraph(
    navController: NavHostController,
    drawerState: DrawerState,
    scope: CoroutineScope
) {
    NavHost(
        navController = navController,
        startDestination = "live"
    ) {
        composable("live") {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("EkoViso") },
                        navigationIcon = {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(Icons.Filled.Menu, contentDescription = "Menu")
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.background,
                            titleContentColor = MaterialTheme.colorScheme.onBackground
                        )
                    )
                }
            ) { padding ->
                LiveScreen(
                    modifier = Modifier.padding(padding),
                    onChannelClick = { channelName, channelUrl ->
                        navController.navigate("player/${channelName}/${channelUrl}")
                    }
                )
            }
        }

        composable("player/{channelName}/{channelUrl}") { backStackEntry ->
            val channelName = backStackEntry.arguments?.getString("channelName") ?: ""
            val channelUrl = backStackEntry.arguments?.getString("channelUrl") ?: ""
            PlayerScreen(
                channelName = channelName,
                channelUrl = channelUrl,
                onBack = { navController.popBackStack() }
            )
        }

        composable("recordings") {
            RecordingsScreen(onBack = { navController.popBackStack() })
        }

        composable("schedules") {
            SchedulesScreen(onBack = { navController.popBackStack() })
        }

        composable("settings") {
            SettingsScreen(onBack = { navController.popBackStack() })
        }
    }
}
