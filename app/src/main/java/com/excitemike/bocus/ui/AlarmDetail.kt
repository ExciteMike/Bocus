package com.excitemike.bocus.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.byValue
import androidx.compose.foundation.text.input.maxLength
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.then
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TimeInput
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.excitemike.bocus.R
import com.excitemike.bocus.data.Alarm
import com.excitemike.bocus.data.AlarmMeta
import kotlin.math.max
import kotlin.math.min

@Composable
fun AlarmDetail(
    alarm: Alarm,
    updateAlarm: (Alarm) -> Unit,
    close: ()->Unit
) {
    Dialog(
        onDismissRequest = { close() },
    ) {
        Card {
            Column (modifier = Modifier.padding(16.dp)) {
                Text(
                    text = stringResource(R.string.edit_alarm),
                    style = MaterialTheme.typography.titleLarge
                )

                AlarmDetailControls(modifier = Modifier.weight(1f, true), alarm, updateAlarm)

                TextButton(
                    onClick = { close() }
                ) {
                    Text(text = stringResource(R.string.done))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlarmDetailControls(modifier: Modifier,
                        alarm: Alarm,
                        updateAlarm: (Alarm) -> Unit,) {
    val fillMaxWidth = Modifier.fillMaxWidth()
    val forceDigits = InputTransformation
        .byValue { current, proposed -> forceNDigits(current, proposed, 2) }

    val startTimeState = rememberTimePickerState(alarm.startHour, alarm.startMinute)
    LaunchedEffect(startTimeState.hour) {
        if (alarm.startHour != startTimeState.hour) {
            updateAlarm(alarm.copy(startHour = startTimeState.hour))
        }
    }
    LaunchedEffect(startTimeState.minute) {
        if (alarm.startMinute != startTimeState.minute) {
            updateAlarm(alarm.copy(startMinute = startTimeState.minute))
        }
    }

    val endTimeState = rememberTimePickerState(alarm.endHour, alarm.endMinute)
    LaunchedEffect(endTimeState.hour) {
        if (alarm.endHour != endTimeState.hour) {
            updateAlarm(alarm.copy(endHour = endTimeState.hour))
        }
    }
    LaunchedEffect(endTimeState.minute) {
        if (alarm.endMinute != endTimeState.minute) {
            updateAlarm(alarm.copy(endMinute = endTimeState.minute))
        }
    }

    val freqMinState = rememberTextFieldState(alarm.frequencyMin.toString())
    val freqMaxState = rememberTextFieldState(alarm.frequencyMax.toString())

    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TextField(
            modifier = fillMaxWidth,
            state = rememberTextFieldState(alarm.name),
            inputTransformation = InputTransformation
                .maxLength(AlarmMeta.NAME_LEN_MAX)
                .then { updateAlarm(alarm.copy(name = this.toString())) },
            label = { Text(stringResource(R.string.alarm_name_label)) },
        )

        TextField(
            modifier = fillMaxWidth,
            state = rememberTextFieldState(alarm.message),
            inputTransformation = InputTransformation
                .maxLength(AlarmMeta.MESSAGE_LEN_MAX)
                .then { updateAlarm(alarm.copy(message = this.toString())) },
            label = { Text(stringResource(R.string.alarm_message_label)) },
        )

        Text(
            text = stringResource(R.string.frequency_label),
            style = MaterialTheme.typography.titleMedium
        )

        Text(
            text = String.format(
                stringResource(R.string.every_x_to_y_minutes),
                alarm.frequencyMin,
                alarm.frequencyMax
            )
        )

        //
        // Frequency min
        //
        TextField(
            modifier = fillMaxWidth,
            state = freqMinState,
            inputTransformation = forceDigits
                .then {
                    val updatedMin = this.toString().toInt()
                    val updatedMax = max(alarm.frequencyMax, updatedMin)
                    freqMaxState.edit { replace(0, length, updatedMax.toString()) }
                    updateAlarm(
                        alarm.copy(
                            frequencyMin = updatedMin,
                            frequencyMax = updatedMax
                        )
                    )
                },
            label = { Text(stringResource(R.string.minimum_label)) }
        )

        //
        // Frequency max
        //
        TextField(
            modifier = fillMaxWidth,
            state = freqMaxState,
            inputTransformation = forceDigits
                .then {
                    val updatedMax = this.toString().toInt()
                    val updatedMin = min(alarm.frequencyMin, updatedMax)
                    freqMinState.edit { replace(0, length, updatedMin.toString()) }
                    updateAlarm(
                        alarm.copy(
                            frequencyMin = updatedMin,
                            frequencyMax = updatedMax,
                        )
                    )
                },
            label = { Text(stringResource(R.string.maximum_label)) }
        )

        //
        // Start Time Label
        //
        Text(
            text = stringResource(R.string.start_time_label),
            style = MaterialTheme.typography.titleMedium
        )

        //
        // Start Time
        //
        TimeInput(
            state = startTimeState,
            modifier = fillMaxWidth,
        )

        //
        // End Time Label
        //
        Text(
            text = stringResource(R.string.end_time_label),
            style = MaterialTheme.typography.titleMedium
        )

        //
        // End Time
        //
        TimeInput(
            state = endTimeState,
            modifier = fillMaxWidth,
        )
    }
}

/**
 * force the CharSequence to be a one or two-digit non-negative integer
 */
fun forceNDigits(current: CharSequence, proposed: CharSequence, maxDigits: Int): CharSequence {
    require(maxDigits > 0)
    require(maxDigits < 99)
    if (proposed.isEmpty()) {
        return "0"
    }
    if (!"""\d{1,${maxDigits}}""".toRegex().matches(proposed)) {
        return current
    }
    return proposed
}