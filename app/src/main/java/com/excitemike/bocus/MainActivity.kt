package com.excitemike.bocus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteItem
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import com.excitemike.bocus.ui.theme.BocusTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BocusTheme {
                BocusApp()
            }
        }
    }
}

@PreviewScreenSizes
@Composable
fun BocusApp() {
    val currentScreen = rememberSaveable { mutableStateOf(AppScreens.WELCOME) }
    val alarms = rememberSaveable { mutableStateListOf<Alarm>() }

    NavigationSuiteScaffold(
        navigationItems = {
            AppScreens.entries
            .filter {it.showInNav}
            .forEach { screen ->
                NavigationSuiteItem(
                    icon = {
                        Icon(
                            painterResource(screen.icon),
                            contentDescription = stringResource(screen.labelId)
                        )
                    },
                    label = { stringResource(screen.labelId) },
                    selected = screen == currentScreen.value,
                    onClick = { currentScreen.value = screen }
                )
            }
        }
    ) {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            val paddingMod = Modifier.padding(innerPadding)
            when (currentScreen.value) {
                AppScreens.WELCOME -> WelcomeScreen(paddingMod)
                AppScreens.ABOUT -> AboutScreen(currentScreen, paddingMod)
                AppScreens.ALARMS -> AlarmScreen(alarms, paddingMod)
                AppScreens.CREDITS -> CreditsScreen(paddingMod)
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
