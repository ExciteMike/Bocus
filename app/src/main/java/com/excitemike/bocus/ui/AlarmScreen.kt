package com.excitemike.bocus.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.excitemike.bocus.R
import java.util.Optional
import kotlin.jvm.optionals.getOrNull

@Composable
fun AlarmScreen(
    alarms: List<Alarm>,
    selectedAlarm: UByte?,
    modifier: Modifier = Modifier,
    addAlarm: ()->Unit,
    openAlarmDetails: (UByte)->Unit,
    closeAlarmDetails: () -> Unit
) {
    if (selectedAlarm != null) {
        BackHandler {
            closeAlarmDetails()
        }
        AlarmDetail(
            alarm = alarms[selectedAlarm.toInt()],
            close = { closeAlarmDetails() }
        )
    }
    AlarmList(alarms = alarms, modifier = modifier, addAlarm=addAlarm, openAlarmDetails=openAlarmDetails)
}

@Composable
fun AlarmList(
    alarms: List<Alarm>,
    modifier: Modifier = Modifier,
    addAlarm: ()->Unit,
    openAlarmDetails: (UByte)->Unit
) {
    Box (
        modifier = modifier.fillMaxSize().padding(16.dp),
    ) {
        Text(
            text = "Alarms",
            textAlign = TextAlign.Center
        )
        LazyColumn(modifier.fillMaxSize()) {
            items(count = alarms.size, key= { alarms[it].id } ) {
                    i ->
                AlarmListItem(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { openAlarmDetails(i.toUByte()) },
                    alarm = alarms[i],)
            }
        }
        FloatingActionButton (
            modifier = Modifier.align(Alignment.BottomEnd),
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
