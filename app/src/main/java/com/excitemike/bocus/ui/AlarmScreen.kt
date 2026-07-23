package com.excitemike.bocus.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.excitemike.bocus.R
import com.excitemike.bocus.data.Alarm

@Composable
fun AlarmScreen(
    modifier: Modifier = Modifier,
    alarms: State<List<Alarm>>,
    selectedAlarm: State<Int?>,
    addAlarm: ()->Unit,
    openAlarmDetails: (Int)->Unit,
    closeAlarmDetails: () -> Unit
) {
    if (selectedAlarm.value != null) {
        BackHandler {
            closeAlarmDetails()
        }
        AlarmDetail(
            alarm =  alarms.value[selectedAlarm.value!!],
            close = { closeAlarmDetails() }
        )
    }
    AlarmList(alarms = alarms.value, modifier = modifier, addAlarm=addAlarm, openAlarmDetails=openAlarmDetails)
}

@Composable
fun AlarmList(
    alarms: List<Alarm>,
    modifier: Modifier = Modifier,
    addAlarm: ()->Unit,
    openAlarmDetails: (Int)->Unit
) {
    Box(modifier = modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        Column (
            modifier = Modifier.fillMaxSize(),
        ) {
            Text(
                text = "Alarms",
                style = MaterialTheme.typography.titleLarge
            )
            LazyColumn(Modifier.fillMaxSize().weight(1f)) {
                items(count = alarms.size, key = { alarms[it].id?:0 }) { i ->
                    AlarmListItem(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { openAlarmDetails(i) },
                        alarm = alarms[i],
                    )
                }
            }
        }
        FloatingActionButton (
            modifier = Modifier.align(Alignment.BottomEnd).padding(bottom=16.dp),
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
