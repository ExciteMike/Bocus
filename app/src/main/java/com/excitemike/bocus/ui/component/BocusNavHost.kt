package com.excitemike.bocus.ui.component

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.excitemike.bocus.data.Alarm
import com.excitemike.bocus.data.AppScreens
import com.excitemike.bocus.data.Command
import com.excitemike.bocus.data.INITIAL_APP_SCREEN
import com.excitemike.bocus.ui.AboutScreen
import com.excitemike.bocus.ui.AlarmScreen

@Composable
fun BocusNavHost(
    navController: NavHostController,
    alarms: List<Alarm>,
    addAlarm: () -> Unit,
    openAlarmDetails: (Int) -> Unit,
    requestDeleteAlarm: (String, Command, Command) -> Unit
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

                    AppScreens.ALARMS -> AlarmScreen(
                        modifier = Modifier.fillMaxSize(),
                        alarms = alarms,
                        addAlarm = addAlarm,
                        openAlarmDetails = openAlarmDetails,
                        requestDeleteAlarm = requestDeleteAlarm,
                    )
                }
            }
        }
    }
}