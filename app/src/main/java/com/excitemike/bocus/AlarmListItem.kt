package com.excitemike.bocus

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun AlarmListItem(alarm: MutableState<Alarm>, modifier: Modifier = Modifier) {
    Row (modifier=modifier, verticalAlignment = Alignment.CenterVertically) {
        Text("...")
    }
}