package com.excitemike.bocus.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteItem
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import com.excitemike.bocus.R
import com.excitemike.bocus.ui.theme.BocusTheme

@Composable
fun BocusApp(
    uiState: BocusUiState,
    addAlarm: (String) -> Unit,
    goToScreen: (AppScreens) -> Unit,
) {
    val defaultAlarmName = stringResource(R.string.default_alarm_name)
    
    Surface(tonalElevation = 5.dp) {
        NavigationSuiteScaffold(
            navigationItems = {
                AppScreens.entries
                    .filter { it.showInNav }
                    .forEach { screen ->
                        NavigationSuiteItem(
                            icon = {
                                Icon(
                                    painterResource(screen.icon),
                                    contentDescription = stringResource(screen.labelId)
                                )
                            },
                            label = { stringResource(screen.labelId) },
                            selected = screen == uiState.currentScreen,
                            onClick = { goToScreen(screen) }
                        )
                    }
            },

            ) {
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                val paddingMod = Modifier.padding(innerPadding)
                when (uiState.currentScreen) {
                    AppScreens.WELCOME -> WelcomeScreen(paddingMod)
                    AppScreens.ABOUT -> AboutScreen(goToScreen, paddingMod)
                    AppScreens.ALARMS -> AlarmScreen(
                        uiState.alarms,
                        addAlarm = { addAlarm(defaultAlarmName) },
                        modifier = paddingMod
                    )

                    AppScreens.CREDITS -> CreditsScreen(paddingMod)
                }
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