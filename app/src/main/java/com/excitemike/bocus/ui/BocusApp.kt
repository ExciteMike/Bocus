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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.excitemike.bocus.R
import com.excitemike.bocus.data.AppScreens
import com.excitemike.bocus.data.checkSystemPermission
import com.excitemike.bocus.data.rescheduleAllSystemAlarms
import com.excitemike.bocus.ui.component.BocusButton
import com.excitemike.bocus.ui.component.BocusNavHost
import com.excitemike.bocus.ui.component.BocusTabRow
import com.excitemike.bocus.ui.component.ErrorToasts
import com.excitemike.bocus.ui.component.SupportButton
import com.excitemike.bocus.ui.viewmodel.AlarmScreenViewModel
import com.excitemike.bocus.ui.viewmodel.BillingViewModel
import com.excitemike.bocus.ui.viewmodel.MessagesViewModel
import kotlinx.coroutines.launch
import kotlin.math.max

@SuppressLint("ScheduleExactAlarm")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BocusApp(
    modifier: Modifier = Modifier,
    activity: Activity,
    alarmScreenViewModel: AlarmScreenViewModel,
    billingViewModel: BillingViewModel,
    messagesViewModel: MessagesViewModel
) {
    val toastMessageState = rememberSaveable { mutableStateOf<Int?>(null) }
    val onError = { resId: Int -> toastMessageState.value = resId }

    val allPermissions = remember { com.excitemike.bocus.data.getSystemPermissionsNeeded() }
    for ((permission, stringId) in allPermissions) {
        PermissionRequestFlow(activity, alarmScreenViewModel, permission, stringId)
    }

    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets.systemBars
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            val navController = rememberNavController()
            val backStackEntry = navController.currentBackStackEntryAsState()
            val route = backStackEntry.value?.destination?.route
            val currentScreenIndex = max(
                0,
                AppScreens.entries.indexOfFirst { it.name == route })
            BocusTabRow(
                currentScreenIndex = currentScreenIndex,
                onNav = {
                    navController.navigate(it.name)
                }
            )
            BocusNavHost(
                modifier = Modifier.weight(1f),
                navController = navController,
                alarmScreenViewModel = alarmScreenViewModel,
                messagesViewModel = messagesViewModel,
                onError = onError
            )

            SupportButton(activity, billingViewModel)
        }
    }
    ErrorToasts(
        messageResId = toastMessageState.value,
        onTimeout = { toastMessageState.value = null }
    )
}

@Composable
fun PermissionRequestFlow(
    activity: Activity,
    viewModel: AlarmScreenViewModel,
    permission: String,
    rationaleStringId: Int
) {
    val isGranted = remember { mutableStateOf(checkSystemPermission(activity, permission)) }
    val isFirstTime = remember { mutableStateOf(true) }
    val showPermissionPrompt = remember { mutableStateOf(true) }
    val reschedScope = rememberCoroutineScope()
    val updateScope = rememberCoroutineScope()
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { userGrantedPermission ->
            val prevValue = isGranted.value
            isGranted.value = userGrantedPermission
            showPermissionPrompt.value = !userGrantedPermission
            if (userGrantedPermission && !prevValue) {
                reschedScope.launch {
                    rescheduleAllSystemAlarms(
                        activity,
                        viewModel.allAlarmsState.value,
                        updateAlarm = { alarm ->
                            updateScope.launch {
                                viewModel.updateAlarmNoReschedule(
                                    alarm
                                )
                            }
                        }
                    )
                }
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
    ) {
        Text(stringResource(R.string.go_to_settings))
    }
}

