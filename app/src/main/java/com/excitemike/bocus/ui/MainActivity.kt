package com.excitemike.bocus.ui

import android.app.AlarmManager
import android.content.Context
import android.content.IntentFilter
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.excitemike.bocus.data.AlarmDatabase
import com.excitemike.bocus.receiver.AlarmReceiver
import com.excitemike.bocus.data.OfflineBocusRepository
import com.excitemike.bocus.ui.theme.BocusTheme

class MainActivity : ComponentActivity() {
    lateinit var receiver: AlarmReceiver
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        receiver = AlarmReceiver()

        setContent {
            BocusTheme {
                BocusApp(
                    viewModel = viewModel { BocusViewModel(application) },
                    context = applicationContext,
                )
            }
        }
    }
}
