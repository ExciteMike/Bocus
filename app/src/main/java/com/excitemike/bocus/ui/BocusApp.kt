package com.excitemike.bocus.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import com.excitemike.bocus.R

@SuppressLint("ScheduleExactAlarm")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BocusApp(
    modifier: Modifier = Modifier,
    activity: Activity,
    viewModel: BocusViewModel,
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

    val allPermissions = remember { viewModel.getSystemPermissionsNeeded(activity) }
    for ((permission, stringId) in allPermissions) {
        PermissionRequestFlow(activity, viewModel, permission, stringId)
    }

    Surface {
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
                    addAlarm = { viewModel.addAlarm(defaultAlarmName) },
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
                            modifier = Modifier,
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

@Composable
fun PermissionRequestFlow(
    activity: Activity,
    viewModel: BocusViewModel,
    permission: String,
    rationaleStringId:Int
) {
    var isGranted by remember { mutableStateOf(viewModel.checkPermission(activity, permission)) }
    var showRationale by remember { mutableStateOf(false) }
    var showPermissionPrompt by remember { mutableStateOf(true) }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = {
            Log.v("BocusTrace", "RequestPermission result $it")
            isGranted = it
            if (!it) {
                showRationale = viewModel.shouldShowPermissionRequestRationale(activity, permission)
            }
        }
    )
    Log.v("BocusTrace", "$permission isGranted:$isGranted; showRationale:$showRationale; showPermissionPrompt:$showPermissionPrompt")

    if (isGranted) {
        return
    }

    if (showRationale) {
        AlertDialog(
            onDismissRequest = { showRationale = false },
            title = {},
            text = {
                Column {
                    Text(stringResource(rationaleStringId))
                    Spacer(modifier = Modifier.height(8.dp))
                    Row {
                        Button (
                            onClick = {
                                permissionLauncher.launch(permission)
                                showRationale = false
                            }
                        ) {
                            Text(text=stringResource(R.string.ok))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        GoToSettingsButton(activity, permission)
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showRationale = false }) {
                    Text(stringResource(R.string.cancel_button))
                }
            },
        )
        return
    }

    if (showPermissionPrompt) {
        AlertDialog(
            onDismissRequest = { showPermissionPrompt = false },
            title = { Text(stringResource(R.string.permission_required)) },
            text = {
                    Column {
                        Text(stringResource(rationaleStringId))
                        Spacer(Modifier.height(8.dp))
                        Text(stringResource(R.string.please_enable_in_settings))
                        Spacer(Modifier.height(8.dp))
                        GoToSettingsButton(activity, permission)
                    }
                },
            confirmButton = {
                TextButton(
                    onClick = {
                            if (viewModel.shouldShowPermissionRequestRationale(activity, permission)) {
                                showRationale = true
                            } else {
                                Log.v("BocusTrace", "launching permission request")
                                permissionLauncher.launch(permission)
                            }
                        }
                    ) {
                        Text(text=stringResource(R.string.done))
                    }
                },
            dismissButton = {
                TextButton(onClick = {
                    showPermissionPrompt = false
                }) {
                    Text(text=stringResource(R.string.cancel_button))
                }
            }
        )
    }
}

@Composable
fun GoToSettingsButton(activity: Activity, permission: String) {
    Button(
        onClick = {
            val action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            val packageName = activity.applicationContext.packageName
            val data = Uri.fromParts("package", packageName, null)
            val intent = Intent(action, data)
            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            intent.data = Uri.fromParts("package", packageName, null)
            activity.startActivity(intent)
        }
    ) {
        Text(stringResource(R.string.go_to_settings))
    }
}

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
