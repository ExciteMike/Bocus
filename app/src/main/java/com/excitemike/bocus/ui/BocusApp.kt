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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.excitemike.bocus.R
import com.excitemike.bocus.data.INITIAL_APP_SCREEN
import com.excitemike.bocus.data.updateAllSystemAlarms
import com.excitemike.bocus.ui.component.AlarmDetails
import com.excitemike.bocus.ui.component.BocusButton
import com.excitemike.bocus.ui.component.BocusNavHost
import com.excitemike.bocus.ui.component.BocusTabRow
import com.excitemike.bocus.util.FxType

@SuppressLint("ScheduleExactAlarm")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BocusApp(
    modifier: Modifier = Modifier,
    activity: Activity,
    viewModel: BocusViewModel,
) {
    val uiState = viewModel.uiState.collectAsState().value
    val selectedAlarmIndex = uiState.selectedAlarmIndex
    val alarms = viewModel.alarmState.collectAsState()

    if (uiState.errorMessage != null) {
        AlertDialog(
            title = @Composable { Text(text = stringResource(R.string.error)) },
            text = @Composable { Text(text = uiState.errorMessage!!) },
            confirmButton = @Composable {
                BocusButton(
                    onClick = {
                        viewModel.dismissErrorDlg()
                    },
                    fx = FxType.NORMAL
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
                BocusButton(
                    onClick = {
                        viewModel.onConfirm()
                    },
                    fx = FxType.CONFIRM
                ) {
                    Text(text = stringResource(R.string.confirm_button))
                }
            },
            dismissButton = {
                BocusButton(
                    onClick = { viewModel.dismissConfirmDlg() },
                    fx = FxType.CANCEL
                ) {
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

    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets.systemBars
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            val navController = rememberNavController()
            var currentScreenIndex by rememberSaveable { mutableIntStateOf(INITIAL_APP_SCREEN.ordinal) }
            val alarms = viewModel.alarmState.collectAsState()
            BocusTabRow(
                currentScreenIndex = currentScreenIndex,
                onNav = {
                    navController.navigate(it.name)
                    currentScreenIndex = it.ordinal
                }
            )
            val defaultAlarmName = stringResource(R.string.default_alarm_name)
            BocusNavHost(
                navController = navController,
                alarms = alarms.value,
                addAlarm = { viewModel.addAlarm(defaultAlarmName) },
                openAlarmDetails = { viewModel.openAlarmDetails(it) },
                requestDeleteAlarm = { message, onConfirm, onCancel ->
                    viewModel.requestDeleteAlarm(
                        message,
                        onConfirm,
                        onCancel
                    )
                }
            )
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
            BocusButton(
                onClick = onDismissRequest,
                fx = FxType.CANCEL
            ) {
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
            BocusButton(
                onClick = onConfirm,
                fx = FxType.NORMAL
            ) {
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
        fx = FxType.NORMAL
    ) {
        Text(stringResource(R.string.go_to_settings))
    }
}

