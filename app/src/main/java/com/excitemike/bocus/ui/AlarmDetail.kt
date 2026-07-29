package com.excitemike.bocus.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.maxLength
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.then
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.excitemike.bocus.R
import com.excitemike.bocus.data.Alarm
import com.excitemike.bocus.data.AlarmLimits
import com.excitemike.bocus.ui.component.BocusButton
import com.excitemike.bocus.ui.component.DaysOfWeek
import com.excitemike.bocus.ui.component.MinMax
import com.excitemike.bocus.ui.component.NotifMode
import com.excitemike.bocus.ui.component.TimeAccordion
import com.excitemike.bocus.util.Fx
import com.excitemike.bocus.util.FxType

@Composable
fun AlarmDetail(
    alarm: Alarm,
    updateAlarm: (Alarm) -> Unit,
    close: (Boolean) -> Unit
) {
    Dialog(
        onDismissRequest = { close(true) },
    ) {
        Card {
            Column(
                modifier = Modifier
                    .padding(horizontal = 8.dp, vertical = 16.dp)
                    .imePadding(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.edit_alarm),
                    style = MaterialTheme.typography.titleLarge
                )

                AlarmDetailControls(modifier = Modifier.weight(1f), alarm, updateAlarm)

                BocusButton(
                    onClick = { close(false) },
                    fx = FxType.CONFIRM
                ) {
                    Text(text = stringResource(R.string.done))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlarmDetailControls(
    modifier: Modifier,
    alarm: Alarm,
    updateAlarm: (Alarm) -> Unit,
) {
    val context = LocalContext.current
    val fillMaxWidth = Modifier.fillMaxWidth()

    val startTimeState = rememberTimePickerState(alarm.startHour, alarm.startMinute)
    LaunchedEffect(startTimeState.hour) {
        if (alarm.startHour != startTimeState.hour) {
            Fx.buttonClickFx(context, FxType.EDIT)
            updateAlarm(alarm.copy(startHour = startTimeState.hour))
        }
    }
    LaunchedEffect(startTimeState.minute) {
        if (alarm.startMinute != startTimeState.minute) {
            Fx.buttonClickFx(context, FxType.EDIT)
            updateAlarm(alarm.copy(startMinute = startTimeState.minute))
        }
    }

    val endTimeState = rememberTimePickerState(alarm.endHour, alarm.endMinute)
    LaunchedEffect(endTimeState.hour) {
        if (alarm.endHour != endTimeState.hour) {
            Fx.buttonClickFx(context, FxType.EDIT)
            updateAlarm(alarm.copy(endHour = endTimeState.hour))
        }
    }
    LaunchedEffect(endTimeState.minute) {
        if (alarm.endMinute != endTimeState.minute) {
            Fx.buttonClickFx(context, FxType.EDIT)
            updateAlarm(alarm.copy(endMinute = endTimeState.minute))
        }
    }

    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        TextField(
            modifier = fillMaxWidth,
            state = rememberTextFieldState(alarm.name),
            inputTransformation = InputTransformation
                .maxLength(AlarmLimits.NAME_LEN_MAX)
                .then {
                    Fx.buttonClickFx(context, FxType.EDIT)
                    updateAlarm(alarm.copy(name = this.toString()))
                },
            label = { Text(stringResource(R.string.alarm_name_label)) },
            lineLimits = TextFieldLineLimits.SingleLine
        )

        TextField(
            modifier = fillMaxWidth,
            state = rememberTextFieldState(alarm.message),
            inputTransformation = InputTransformation
                .maxLength(AlarmLimits.MESSAGE_LEN_MAX)
                .then {
                    Fx.buttonClickFx(context, FxType.EDIT)
                    updateAlarm(alarm.copy(message = this.toString()))
                },
            label = { Text(stringResource(R.string.alarm_message_label)) },
            lineLimits = TextFieldLineLimits.SingleLine
        )

        // Frequencies
        MinMax(
            alarm.frequencyMin,
            alarm.frequencyMax,
            label = stringResource(R.string.frequency),
            onMinChanged = {
                Fx.buttonClickFx(context, FxType.EDIT)
                updateAlarm(alarm.copy(frequencyMin = it))
            },
            onMaxChanged = {
                Fx.buttonClickFx(context, FxType.EDIT)
                updateAlarm(alarm.copy(frequencyMax = it))
            },
        )

        //
        // Start Time
        //
        TimeAccordion(
            modifier = Modifier.padding(start = 16.dp),
            startTimeState,
            stringResource(R.string.start_time_label),
            stringResource(R.string.start_time_label_and_value),
            onBlur = {
                // bump the end up if earlier than start
                val start = startTimeState.hour * 60 + startTimeState.minute
                val end = endTimeState.hour * 60 + endTimeState.minute
                if (start > end) {
                    endTimeState.hour = startTimeState.hour
                    endTimeState.minute = startTimeState.minute
                }
            }
        )
        Spacer(Modifier.size(8.dp))
        //
        // End Time
        //
        TimeAccordion(
            modifier = Modifier.padding(start = 16.dp),
            endTimeState,
            stringResource(R.string.end_time_label),
            stringResource(R.string.end_time_label_and_value),
            onBlur = {
                // bump the start down if later than end
                val start = startTimeState.hour * 60 + startTimeState.minute
                val end = endTimeState.hour * 60 + endTimeState.minute
                if (start > end) {
                    startTimeState.hour = endTimeState.hour
                    startTimeState.minute = endTimeState.minute
                }
            }
        )

        DaysOfWeek(alarm, updateAlarm)
        NotifMode(alarm, updateAlarm)

        // TODO: alarmlength
    }
}
