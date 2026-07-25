package com.excitemike.bocus.ui

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.excitemike.bocus.R
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Surface
import androidx.compose.ui.graphics.vector.ImageVector

@SuppressLint("ScheduleExactAlarm")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BocusApp(
    modifier: Modifier = Modifier,
    viewModel: BocusViewModel,
    context: Context,
) {
    val uiState = viewModel.uiState.collectAsState().value

    val defaultAlarmName = stringResource(R.string.default_alarm_name)

    if (uiState.errorMessage != null) {
        AlertDialog(
            title = @Composable { Text(text = stringResource(R.string.error))},
            text = @Composable { Text(text=uiState.errorMessage!!) },
            confirmButton = @Composable { TextButton(
                onClick = {
                    viewModel.dismissErrorDlg()
                }
            ) {
                Text(text=stringResource(R.string.dismiss))
            } },
            onDismissRequest = { viewModel.dismissErrorDlg() }
        )
    }

    if (uiState.confirmMessage != null) {
        AlertDialog(
            text = { Text(text = uiState.confirmMessage!!) },
            confirmButton = @Composable { TextButton(
                onClick = {
                    viewModel.onConfirm()
                }
            ) {
                Text(text=stringResource(R.string.confirm_button))
            } },
            dismissButton = {
                TextButton(onClick = { viewModel.dismissConfirmDlg() }) { Text(text=stringResource(R.string.cancel_button)) }
            },
            onDismissRequest = { viewModel.dismissConfirmDlg() }
        )
    }

    Surface () {
        Column(modifier = modifier.fillMaxSize().padding(top = 32.dp)) {
            val screenMod = Modifier.weight(1f)
            when (uiState.currentScreen) {
                AppScreens.WELCOME -> WelcomeScreen(modifier = screenMod)
                AppScreens.ABOUT -> AboutScreen(
                    modifier = screenMod,
                    goToScreen = { viewModel.goToScreen(it) })

                AppScreens.ALARMS -> AlarmScreen(
                    modifier = screenMod,
                    alarms = viewModel.alarmState.collectAsState(),
                    selectedAlarmIndex = uiState.selectedAlarmIndex,
                    addAlarm = { viewModel.addAlarm(defaultAlarmName, context) },
                    updateAlarm = { viewModel.updateAlarm(it) },
                    openAlarmDetails = { viewModel.openAlarmDetails(it) },
                    closeAlarmDetails = { viewModel.closeAlarmDetails() },
                    requestDeleteAlarm = { message, onConfirm, onCancel ->
                        viewModel.requestDeleteAlarm(
                            message,
                            onConfirm,
                            onCancel
                        )
                    },
                )

                AppScreens.CREDITS -> CreditsScreen(modifier = screenMod)
            }

            // TODO: replace with a navigation rail or something because it looks funny with just two things and also takes up a lot of vertical space as-is
            NavigationBar(modifier = Modifier.fillMaxWidth()) {
                AppScreens.entries
                    .filter { it.showInNav }
                    .forEach { screen ->
                        NavigationBarItem(
                            modifier = Modifier.padding(top = 4.dp),
                            icon = {
                                Icon(
                                    imageVector = screen.icon,
                                    contentDescription = stringResource(screen.labelId)
                                )
                            },
                            label = { Text(text = stringResource(screen.labelId)) },
                            selected = screen == uiState.currentScreen,
                            onClick = { viewModel.goToScreen(screen) },
                        )
                    }
            }
        }
    }
}

// TODO: animate screen transitions
enum class AppScreens(
    val labelId: Int,
    val icon: ImageVector,
    val showInNav: Boolean
) {
    // TODO: remove welcome screen. or at least make it come up only once
    WELCOME(R.string.welcome_tab_name, Icons.Default.Home, false),
    ALARMS(R.string.alarms_tab_name, Icons.Default.Home, true),
    ABOUT(R.string.about_tab_name, Icons.Default.Info, true),
    CREDITS(R.string.credits_tab_name, Icons.Default.Info, false),
}
