package com.excitemike.bocus.ui.component

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.excitemike.bocus.data.AppScreens
import com.excitemike.bocus.data.INITIAL_APP_SCREEN
import com.excitemike.bocus.ui.screen.AboutScreen
import com.excitemike.bocus.ui.screen.AlarmScreen
import com.excitemike.bocus.ui.screen.MessageListScreen
import com.excitemike.bocus.ui.viewmodel.AlarmScreenViewModel
import com.excitemike.bocus.ui.viewmodel.MessageListScreenViewModel

@Composable
fun BocusNavHost(
    navController: NavHostController,
    alarmScreenViewModel: AlarmScreenViewModel,
    messageListScreenViewModel: MessageListScreenViewModel,
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
                        modifier = Modifier.fillMaxSize()
                    )

                    AppScreens.MESSAGE_LISTS -> MessageListScreen(
                        modifier = Modifier.fillMaxSize(),
                        messageListScreenViewModel = messageListScreenViewModel,
                        onError = onError
                    )

                    AppScreens.ALARMS -> {
                        AlarmScreen(
                            alarmScreenViewModel = alarmScreenViewModel,
                            messageListScreenViewModel = messageListScreenViewModel,
                            onError = onError,
                        )
                    }
                }
            }
        }
    }
}