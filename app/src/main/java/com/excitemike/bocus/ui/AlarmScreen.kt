package com.excitemike.bocus.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.excitemike.bocus.R
import com.excitemike.bocus.data.Alarm
import com.excitemike.bocus.data.Command
import com.excitemike.bocus.ui.component.AlarmGrid
import com.excitemike.bocus.ui.component.BocusButton
import com.excitemike.bocus.util.FxType

@Composable
fun AlarmScreen(
    modifier: Modifier = Modifier,
    alarms: List<Alarm>,
    addAlarm: () -> Unit,
    openAlarmDetails: (Int) -> Unit,
    requestDeleteAlarm: (String, Command, Command) -> Unit,
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (alarms.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp)
            ) {
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = stringResource(R.string.no_alarms),
                    textAlign = TextAlign.Justify
                )
            }
        } else {
            AlarmGrid(
                modifier = Modifier.weight(1f),
                alarms,
                openAlarmDetails,
                requestDeleteAlarm
            )
        }
        BocusButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            onClick = {
                addAlarm()
            },
            fx = FxType.CONFIRM
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

