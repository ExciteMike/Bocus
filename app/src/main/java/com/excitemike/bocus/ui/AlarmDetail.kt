package com.excitemike.bocus.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.excitemike.bocus.R

@Composable
fun AlarmDetail(
    alarm: Alarm,
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
                Text(text="alarm details")
                TextButton (onClick = { close() }) {
                    Text(text= stringResource(R.string.done))
                }
            }
        }
    }
}