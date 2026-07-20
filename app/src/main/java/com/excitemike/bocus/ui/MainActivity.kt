package com.excitemike.bocus.ui

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.excitemike.bocus.R
import com.excitemike.bocus.ui.theme.BocusTheme
import kotlinx.coroutines.flow.MutableStateFlow

class MainActivity : ComponentActivity() {
    private val viewModel by viewModels<BocusViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val uiState = viewModel.uiState.collectAsState()
            BocusApp(
                uiState = uiState.value,
                goToScreen = { viewModel.goToScreen(it) },
                addAlarm = { name -> viewModel.addAlarm(name=name, context=applicationContext) },
                openAlarmDetails = { selectedAlarm -> viewModel.openAlarmDetails(selectedAlarm) },
                closeAlarmDetails = { viewModel.closeAlarmDetails() },
                dismissErrorDlg = { viewModel.dismissErrorDlg() }
            )
        }
    }
}

@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "DefaultPreviewDark"
)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    name = "DefaultPreviewLight"
)
@Composable
fun BocusAppPreview() {
    /*val _uiState = rememberSaveable()  { MutableStateFlow(BocusUiState()) }
    val uiState = _uiState.collectAsState()
    BocusTheme {
        BocusApp (
            uiState = uiState.value,
            goToScreen = {  },
            addAlarm = {  }
        )
    }*/
}