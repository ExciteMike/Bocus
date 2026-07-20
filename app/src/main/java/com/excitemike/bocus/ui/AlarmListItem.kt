package com.excitemike.bocus.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AlarmListItem(
    alarm: Alarm,
    modifier: Modifier = Modifier
) {
    Card (modifier=modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 4.dp),
    ) {
        Column (modifier = Modifier.fillMaxWidth().padding(20.dp)) {
            Text(
                text = alarm.name
            )
            Text(
                text = "..."
            )
            Text(
                text = "..."
            )
        }
    }
}