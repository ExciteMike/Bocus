package com.excitemike.bocus.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.excitemike.bocus.R
import com.excitemike.bocus.data.Alarm
import com.excitemike.bocus.data.Command
import com.excitemike.bocus.ui.component.AlarmGrid

@Composable
fun AlarmScreen(
    modifier: Modifier = Modifier,
    alarms: State<List<Alarm>>,
    selectedAlarmIndex: Int?,
    addAlarm: () -> Unit,
    updateAlarm: (alarm: Alarm) -> Unit,
    openAlarmDetails: (Int) -> Unit,
    closeAlarmDetails: () -> Unit,
    requestDeleteAlarm: (String, Command, Command) -> Unit
) {
    if (selectedAlarmIndex != null) {
        if (selectedAlarmIndex in 0..<alarms.value.size) {
            BackHandler {
                closeAlarmDetails()
            }
            AlarmDetail(
                alarm = alarms.value[selectedAlarmIndex],
                updateAlarm = updateAlarm,
                close = { closeAlarmDetails() }
            )
        } else {
            closeAlarmDetails()
        }
    }
    AlarmList(
        alarms = alarms.value,
        modifier = modifier,
        addAlarm = addAlarm,
        openAlarmDetails = openAlarmDetails,
        requestDeleteAlarm = requestDeleteAlarm
    )
}

@Composable
fun AlarmList(
    alarms: List<Alarm>,
    modifier: Modifier = Modifier,
    addAlarm: () -> Unit,
    openAlarmDetails: (Int) -> Unit,
    requestDeleteAlarm: (String, Command, Command) -> Unit
) {
    val shape = MaterialTheme.shapes.medium
    val containerColor = ButtonDefaults.buttonColors().containerColor
    val contentColor = ButtonDefaults.buttonColors().contentColor
    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            Text(
                text = "Alarms",
                style = MaterialTheme.typography.titleLarge,
            )
            if (alarms.isEmpty()) {
                Text(
                    modifier = Modifier
                        .weight(1f)
                        .wrapContentHeight(Alignment.CenterVertically),
                    text = stringResource(R.string.no_alarms),
                )
            } else {
                AlarmGrid(
                    modifier = Modifier.fillMaxSize().weight(1f),
                    alarms,
                    openAlarmDetails,
                    requestDeleteAlarm
                )
            }
            Surface(
                modifier = Modifier
                    .semantics { role = Role.Button }
                    .padding(8.dp)
                    .fillMaxWidth()
                    .background(
                        color = containerColor,
                        shape = shape
                    ),
                color = Color.Transparent,
                contentColor = contentColor,
                border = BorderStroke(1.dp, contentColor),
                shape = shape,
                onClick = addAlarm,
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        modifier = Modifier.size(48.dp),
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(R.string.add_alarm),
                    )
                    Text(
                        text = stringResource(R.string.add_alarm)
                    )
                }
            }
        }
    }
}
