package com.excitemike.bocus.ui.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.excitemike.bocus.data.AppScreens
import com.excitemike.bocus.data.INITIAL_APP_SCREEN
import com.excitemike.bocus.ui.screen.AboutScreen
import com.excitemike.bocus.ui.screen.AlarmScreen
import com.excitemike.bocus.ui.viewmodel.AlarmScreenViewModel
import com.excitemike.bocus.ui.viewmodel.MessagesViewModel

@Composable
fun BocusNavHost(
    navController: NavHostController,
    alarmScreenViewModel: AlarmScreenViewModel,
    messagesViewModel: MessagesViewModel,
    onError: (Int) -> Unit,
) {
    NavHost(
        navController,
        startDestination = INITIAL_APP_SCREEN.name
    ) {
        AppScreens.entries.forEach { screen ->
            composable(screen.name) {
                when (screen) {
                    AppScreens.ABOUT -> AboutScreen(
                        modifier = Modifier.fillMaxWidth()
                    )

                    AppScreens.ALARMS -> {
                        AlarmScreen(
                            alarmScreenViewModel = alarmScreenViewModel,
                            messagesViewModel = messagesViewModel,
                            onError = onError,
                        )
                    }
                }
            }
        }
    }
}