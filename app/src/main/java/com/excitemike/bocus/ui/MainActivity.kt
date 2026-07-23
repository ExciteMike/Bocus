package com.excitemike.bocus.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.excitemike.bocus.data.AlarmDatabase
import com.excitemike.bocus.data.OfflineBocusRepository
import com.excitemike.bocus.ui.theme.BocusTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val dao = AlarmDatabase.getDatabase(application).alarmDao()
        setContent {
            BocusTheme {
                BocusApp(
                    viewModel = viewModel { BocusViewModel(OfflineBocusRepository(dao)) },
                    context = applicationContext,
                )
            }
        }
    }
}
