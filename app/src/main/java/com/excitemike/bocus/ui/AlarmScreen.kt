package com.excitemike.bocus.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.excitemike.bocus.R
import com.excitemike.bocus.data.Alarm
import com.excitemike.bocus.data.Command
import com.excitemike.bocus.modifier.fadeTopAndBottom

@Composable
fun AlarmScreen(
    modifier: Modifier = Modifier,
    alarms: State<List<Alarm>>,
    selectedAlarmIndex: Int?,
    addAlarm: ()->Unit,
    updateAlarm: (alarm:Alarm)->Unit,
    openAlarmDetails: (Int)->Unit,
    closeAlarmDetails: () -> Unit,
    requestDeleteAlarm: (String, Command, Command)->Unit
) {
    if (selectedAlarmIndex != null) {
        if (selectedAlarmIndex in 0..<alarms.value.size) {
            BackHandler {
                closeAlarmDetails()
            }
            AlarmDetail(
                alarm =  alarms.value[selectedAlarmIndex],
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
    addAlarm: ()->Unit,
    openAlarmDetails: (Int)->Unit,
    requestDeleteAlarm: (String, Command, Command)->Unit
) {
    Box(modifier = modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        Column (
            modifier = Modifier.fillMaxSize(),
        ) {
            Text(
                text = "Alarms",
                style = MaterialTheme.typography.titleLarge,
            )
            LazyVerticalGrid(
                columns = GridCells.Adaptive(200.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .fadeTopAndBottom(16.dp),
                verticalArrangement = Arrangement.Top,
                horizontalArrangement = Arrangement.Center,
            ) {
                if (alarms.isEmpty()) {
                    item {
                        Text(text = stringResource(R.string.no_alarms))
                    }
                }
                items(
                    alarms,
                    key = { it.id!! }
                ) { alarm ->
                    val index = lazy { alarms.indexOfFirst { it.id == alarm.id } }
                    val deleteAlarmTemplate = stringResource(R.string.confirm_delete_alarm)
                    val swipeToDismissState = rememberSwipeToDismissBoxState()
                    SwipeToDismissBox(
                        state = swipeToDismissState,
                        backgroundContent = {
                            val deleteColor = Color(1f,0f,0f,0.85f)
                            val bgColor =  if (swipeToDismissState.targetValue == SwipeToDismissBoxValue.Settled) {
                                Color.Transparent
                            } else {
                                deleteColor
                            }

                            Row (
                                Modifier.padding(4.dp)
                                    .background(
                                        color = bgColor,
                                        shape = RoundedCornerShape(16.dp),
                                    ),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (swipeToDismissState.targetValue != SwipeToDismissBoxValue.EndToStart) {
                                    Icon (
                                        modifier = Modifier
                                            .weight(1f)
                                            .fillMaxHeight(),
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = null
                                    )
                                } else {
                                    Spacer(Modifier.weight(1f))
                                }
                                Spacer(Modifier.weight(1f))
                                if (swipeToDismissState.targetValue != SwipeToDismissBoxValue.StartToEnd) {
                                    Icon (
                                        modifier = Modifier
                                            .weight(1f)
                                            .fillMaxHeight(),
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = null
                                    )
                                } else {
                                    Spacer(Modifier.weight(1f))
                                }
                            }
                        },
                        onDismiss = {
                            val message = String.format(
                                deleteAlarmTemplate,
                                alarm.name
                            )
                            requestDeleteAlarm(
                                message,
                                Command.DeleteAlarm(alarm.id!!),
                                Command.Callback( suspend { swipeToDismissState.reset() } )
                            )
                        }
                    ) {
                        AlarmListItem(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { openAlarmDetails(index.value) },
                            alarm = alarm,
                        )
                    }
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
