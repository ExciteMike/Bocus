package com.excitemike.bocus.ui.screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.excitemike.bocus.R
import com.excitemike.bocus.data.Alarm
import com.excitemike.bocus.data.Command
import com.excitemike.bocus.ui.component.AlarmGridItem
import com.excitemike.bocus.ui.component.GridWithAddButton

@Composable
fun AlarmScreen(
    modifier: Modifier = Modifier,
    alarms: List<Alarm>,
    addAlarm: () -> Unit,
    openAlarmDetails: (Int) -> Unit,
    requestDeleteAlarm: (String, Command, Command) -> Unit,
) {
    GridWithAddButton(
        data = alarms,
        dataKey = { it.id!! },
        addButtonLabel = stringResource(R.string.add_alarm),
        onAdd = addAlarm,
        modifier = modifier.fillMaxSize(),
        messageIfEmpty = stringResource(R.string.no_alarms)
    ) {
        AlarmGridItem(
            modifier = Modifier.padding(end = 8.dp),
            alarm = it,
            allAlarms = alarms,
            openAlarmDetails = openAlarmDetails,
            requestDeleteAlarm = requestDeleteAlarm
        )
    }
}

