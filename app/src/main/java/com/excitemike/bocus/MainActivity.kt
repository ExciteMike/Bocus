package com.excitemike.bocus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
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
    var currentScreen by rememberSaveable { mutableStateOf(AppScreens.WELCOME) }

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            AppScreens.entries.forEach {
                item(
                    icon = {
                        Icon(
                            painterResource(it.icon),
                            contentDescription = it.label
                        )
                    },
                    label = { Text(it.label) },
                    selected = it == currentScreen,
                    onClick = { currentScreen = it }
                )
            }
        }
    ) {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            Greeting(
                name = "Android",
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

enum class AppScreens(
    val label: String,
    val icon: Int,
    val showInNav: Boolean
) {
    WELCOME("Welcome", R.drawable.ic_about, false),
    ALARMS("Alarms", R.drawable.ic_alarm, true),
    ABOUT("Profile", R.drawable.ic_about, true),
    CREDITS("About", R.drawable.ic_about, false),
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Composable
fun EntryUI(alarm: Alarm) {
    //Text(text = "name is $name")
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    BocusTheme {
        Greeting("Android")
    }
}