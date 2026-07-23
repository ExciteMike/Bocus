package com.excitemike.bocus.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.excitemike.bocus.R
import com.excitemike.bocus.data.Alarm

@Composable
fun AlarmDetail(
    alarm: Alarm,
    updateAlarm: (Alarm) -> Unit,
    close: ()->Unit
) {
    Dialog (
        onDismissRequest = { close() },
    ) {
        Card (modifier = Modifier
            .fillMaxWidth()
        ) {
            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.edit_alarm),
                    style = MaterialTheme.typography.titleLarge
                )

                TextField(
                    value = alarm.name,
                    onValueChange = { name -> updateAlarm(alarm.copy(name=name)) },
                    label = { Text(stringResource(R.string.alarm_name_label)) },
                )

                TextButton (onClick = { close() }) {
                    Text(text= stringResource(R.string.done))
                }
            }
        }
    }
}