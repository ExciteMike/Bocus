package com.excitemike.bocus.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.excitemike.bocus.R

const val MAX_ALARMS = 1

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BocusApp(
    modifier: Modifier = Modifier,
    uiState: BocusUiState,
    addAlarm: (String) -> Unit,
    dismissErrorDlg: () -> Unit,
    goToScreen: (AppScreens) -> Unit,
    openAlarmDetails: (UByte)->Unit,
    closeAlarmDetails: () -> Unit
) {
    val defaultAlarmName = stringResource(R.string.default_alarm_name)

    if (uiState.errorMessage != null) {
        AlertDialog(
            title = @Composable { Text(text = stringResource(R.string.error))},
            text = @Composable { Text(text=uiState.errorMessage!!) },
            confirmButton = @Composable { TextButton(
                onClick = {
                    dismissErrorDlg()
                }
            ) {
                Text(text=stringResource(R.string.dismiss))
            } },
            onDismissRequest = { dismissErrorDlg() }
        )
    }

    Column (modifier = modifier.fillMaxSize()) {
        val screenMod = Modifier.weight(1f).padding(16.dp)
        when (uiState.currentScreen) {
            AppScreens.WELCOME -> WelcomeScreen(modifier = screenMod)
            AppScreens.ABOUT -> AboutScreen(modifier = screenMod, goToScreen = goToScreen)
            AppScreens.ALARMS -> AlarmScreen(
                modifier = screenMod,
                alarms = uiState.alarms,
                addAlarm = { addAlarm(defaultAlarmName) },
                selectedAlarm = uiState.selectedAlarm,
                openAlarmDetails = openAlarmDetails,
                closeAlarmDetails = closeAlarmDetails,
            )

            AppScreens.CREDITS -> CreditsScreen(modifier = screenMod)
        }

        NavigationBar (modifier = Modifier.fillMaxWidth()) {
            AppScreens.entries
                .filter { it.showInNav }
                .forEach { screen ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                painterResource(screen.icon),
                                contentDescription = stringResource(screen.labelId)
                            )
                        },
                        label = { Text(text=stringResource(screen.labelId)) },
                        selected = screen == uiState.currentScreen,
                        onClick = { goToScreen(screen) }
                    )
                }
        }
    }
}

enum class AppScreens(
    val labelId: Int,
    val icon: Int,
    val showInNav: Boolean
) {
    WELCOME(R.string.welcome_tab_name, R.drawable.ic_about, false),
    ALARMS(R.string.alarms_tab_name, R.drawable.ic_alarm, true),
    ABOUT(R.string.about_tab_name, R.drawable.ic_about, true),
    CREDITS(R.string.credits_tab_name, R.drawable.ic_about, false),
}