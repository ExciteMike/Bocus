package com.excitemike.bocus.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.excitemike.bocus.R

@Composable
fun AlarmScreen(
    alarms: List<Alarm>,
    addAlarm: ()->Unit,
    modifier: Modifier = Modifier)
{
    Column (
        modifier = modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Alarms go here",
            textAlign = TextAlign.Center
        )
        alarms.forEach { alarm ->
            AlarmListItem(
                modifier = Modifier.fillMaxWidth(),
                alarm = alarm,)
        }
        FloatingActionButton (
            onClick = addAlarm,
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
            contentColor = MaterialTheme.colorScheme.onTertiaryContainer
        ) {
            Icon(
                painterResource(R.drawable.ic_add),
                contentDescription = stringResource(R.string.add_alarm)
            )
        }
    }
}
