package com.excitemike.bocus.ui

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.excitemike.bocus.R
import com.excitemike.bocus.data.Alarm
import com.excitemike.bocus.data.Command
import com.excitemike.bocus.ui.component.AlarmGrid

@Composable
fun AlarmScreen(
    modifier: Modifier = Modifier,
    alarms: State<List<Alarm>>,
    addAlarm: () -> Unit,
    openAlarmDetails: (Int) -> Unit,
    requestDeleteAlarm: (String, Command, Command) -> Unit,
    goToScreen: (AppScreens) -> Unit
) {
    Box(modifier = modifier.fillMaxSize()) {
        AlarmList(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(top = 32.dp),
            alarms = alarms.value,
            addAlarm = addAlarm,
            openAlarmDetails = openAlarmDetails,
            requestDeleteAlarm = requestDeleteAlarm,
        )

        Text(
            modifier = Modifier.align(Alignment.TopCenter),
            text = "Alarms",
            style = MaterialTheme.typography.titleLarge
        )
        IconButton(
            onClick = { goToScreen(AppScreens.ABOUT) },
            modifier = Modifier.align(Alignment.TopEnd)
        ) {
            Icon(
                modifier = Modifier.fillMaxSize(),
                imageVector = Icons.Default.Info,
                contentDescription = stringResource(R.string.about),
            )
        }
    }
}

@Composable
fun AlarmList(
    modifier: Modifier = Modifier,
    alarms: List<Alarm>,
    addAlarm: () -> Unit,
    openAlarmDetails: (Int) -> Unit,
    requestDeleteAlarm: (String, Command, Command) -> Unit,
) {
    val shape = MaterialTheme.shapes.medium
    val containerColor = ButtonDefaults.buttonColors().containerColor
    val contentColor = ButtonDefaults.buttonColors().contentColor
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
