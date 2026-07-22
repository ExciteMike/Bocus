package com.excitemike.bocus.ui

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.repeatOnLifecycle
import com.excitemike.bocus.R
import com.excitemike.bocus.data.AlarmDao
import com.excitemike.bocus.data.AlarmDatabase
import com.excitemike.bocus.ui.theme.BocusTheme

class MainActivity : ComponentActivity() {
    private var dao: AlarmDao? = null
    private var viewModel: BocusViewModel? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dao = dao ?: AlarmDatabase.getDatabase(application).alarmDao()
        viewModel = viewModel ?: BocusViewModel(dao!!)
        enableEdgeToEdge()
        setContent {
            val uiState = viewModel!!.uiState.collectAsState()
            BocusTheme {
                BocusApp(
                    uiState = uiState.value,
                    goToScreen = { viewModel!!.goToScreen(it) },
                    addAlarm = { name ->
                        viewModel!!.addAlarm(
                            name = name,
                            context = applicationContext
                        )
                    },
                    openAlarmDetails = { selectedAlarm ->
                        viewModel!!.openAlarmDetails(
                            selectedAlarm
                        )
                    },
                    closeAlarmDetails = { viewModel!!.closeAlarmDetails() },
                    dismissErrorDlg = { viewModel!!.dismissErrorDlg() }
                )
            }
        }
    }
}
