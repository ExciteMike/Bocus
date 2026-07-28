package com.excitemike.bocus.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.excitemike.bocus.R
import com.excitemike.bocus.data.updateAllSystemAlarms
import com.excitemike.bocus.ui.component.AlarmDetails
import com.excitemike.bocus.ui.component.BocusButton

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
    val selectedAlarmIndex = uiState.selectedAlarmIndex
    val alarms = viewModel.alarmState.collectAsState()

    if (uiState.errorMessage != null) {
        AlertDialog(
            title = @Composable { Text(text = stringResource(R.string.error)) },
            text = @Composable { Text(text = uiState.errorMessage!!) },
            confirmButton = @Composable {
                TextButton(
                    onClick = {
                        viewModel.dismissErrorDlg()
                    }
                ) {
                    Text(text = stringResource(R.string.dismiss))
                }
            },
            onDismissRequest = { viewModel.dismissErrorDlg() }
        )
    }

    if (uiState.confirmMessage != null) {
        AlertDialog(
            text = { Text(text = uiState.confirmMessage!!) },
            confirmButton = @Composable {
                TextButton(
                    onClick = {
                        viewModel.onConfirm()
                    }
                ) {
                    Text(text = stringResource(R.string.confirm_button))
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.dismissConfirmDlg() }) {
                    Text(
                        text = stringResource(
                            R.string.cancel_button
                        )
                    )
                }
            },
            onDismissRequest = { viewModel.dismissConfirmDlg() }
        )
    }

    val allPermissions = remember { viewModel.getSystemPermissionsNeeded() }
    for ((permission, stringId) in allPermissions) {
        PermissionRequestFlow(activity, viewModel, permission, stringId)
    }

    AlarmDetails(
        alarms = alarms.value,
        selectedAlarmIndex = selectedAlarmIndex,
        updateAlarm = { viewModel.updateAlarm(it) },
        closeAlarmDetails = { viewModel.closeAlarmDetails() }
    )

    Surface(modifier = Modifier.fillMaxSize()) {
        Scaffold(contentWindowInsets = WindowInsets.systemBars) { innerPadding ->
            val screenMod = modifier.fillMaxSize().padding(innerPadding)
            when (uiState.currentScreen) {
                AppScreens.ABOUT -> AboutScreen(
                    modifier = screenMod,
                    goToScreen = { viewModel.goToScreen(it) })

                AppScreens.ALARMS -> AlarmScreen(
                    modifier = screenMod,
                    alarms = viewModel.alarmState.collectAsState(),
                    addAlarm = { viewModel.addAlarm(defaultAlarmName) },
                    openAlarmDetails = { viewModel.openAlarmDetails(it) },
                    requestDeleteAlarm = { message, onConfirm, onCancel ->
                        viewModel.requestDeleteAlarm(
                            message,
                            onConfirm,
                            onCancel
                        )
                    },
                    goToScreen = { viewModel.goToScreen(it) }
                )
            }
        }
    }
}

@Composable
fun PermissionRequestFlow(
    activity: Activity,
    viewModel: BocusViewModel,
    permission: String,
    rationaleStringId: Int
) {
    val isGranted = remember { mutableStateOf(viewModel.checkPermission(activity, permission)) }
    val isFirstTime = remember { mutableStateOf(true) }
    val showPermissionPrompt = remember { mutableStateOf(true) }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = {
            val prevValue = isGranted.value
            isGranted.value = it
            showPermissionPrompt.value = !it
            if (it && !prevValue) {
                updateAllSystemAlarms(activity, viewModel.alarmState.value)
            }
        }
    )
    val rationaleString = stringResource(rationaleStringId)

    if (isGranted.value) {
        return
    }

    if (showPermissionPrompt.value) {
        PermissionPrompt(
            activity = activity,
            onConfirm = {
                showPermissionPrompt.value = false
                isFirstTime.value = false
                permissionLauncher.launch(permission)
            },
            onDismissRequest = {
                showPermissionPrompt.value = false
                if (!isGranted.value && isFirstTime.value) {
                    permissionLauncher.launch(permission)
                }
                isFirstTime.value = false
            },
            rationaleString = rationaleString,
            showSettingsBtn = !isFirstTime.value
        )
    }
}

@Composable
fun PermissionPrompt(
    onConfirm: () -> Unit,
    onDismissRequest: () -> Unit,
    rationaleString: String,
    showSettingsBtn: Boolean,
    activity: Activity
) {
    val dismissBtn = if (showSettingsBtn) {
        @Composable {
            TextButton(onClick = onDismissRequest) {
                Text(text = stringResource(R.string.cancel_button))
            }
        }
    } else {
        null
    }
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(stringResource(R.string.permission_required)) },
        text = {
            Column {
                Text(rationaleString)
                Spacer(Modifier.height(8.dp))
                if (showSettingsBtn) {
                    Text(stringResource(R.string.please_enable_in_settings))
                    Spacer(Modifier.height(8.dp))
                    GoToSettingsButton(activity)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = stringResource(R.string.ok))
            }
        },
        dismissButton = dismissBtn
    )
}

@Composable
fun GoToSettingsButton(activity: Activity) {
    BocusButton(
        onClick = {
            val action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            val packageName = activity.applicationContext.packageName
            val data = Uri.fromParts("package", packageName, null)
            val intent = Intent(action, data)
            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            intent.data = Uri.fromParts("package", packageName, null)
            activity.startActivity(intent)
        },
    ) {
        Text(stringResource(R.string.go_to_settings))
    }
}

enum class AppScreens {
    ALARMS,
    ABOUT
}
