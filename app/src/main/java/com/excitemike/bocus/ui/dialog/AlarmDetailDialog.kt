package com.excitemike.bocus.ui.dialog

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.maxLength
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.then
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.excitemike.bocus.R
import com.excitemike.bocus.data.Alarm
import com.excitemike.bocus.data.AlarmDetailsMessages
import com.excitemike.bocus.data.MessageList
import com.excitemike.bocus.ui.BocusViewModel
import com.excitemike.bocus.ui.component.DaysOfWeek
import com.excitemike.bocus.ui.component.MinMax
import com.excitemike.bocus.ui.component.NotifMode
import com.excitemike.bocus.ui.component.TimeAccordion
import com.excitemike.bocus.ui.modifier.verticalScrollbar

@Composable
fun AlarmDetailDialog(
    selectedAlarm: Alarm,
    messageLists: List<MessageList>,
    updateAlarm: (alarm: Alarm) -> Unit,
    close: () -> Unit,
) {
    val showChooseMessageListDialog = rememberSaveable { mutableStateOf(false) }

    if (showChooseMessageListDialog.value) {
        ChooseMessageListDialog(
            messageLists = messageLists,
            close = { showChooseMessageListDialog.value = false },
            onChooseMessageList = { messageListId ->
                updateAlarm(selectedAlarm.copy(messageListId = messageListId))
            }
        )
    }

    val messageList = findMessageListById(messageLists, selectedAlarm.messageListId)
    BocusDialog(
        title = stringResource(R.string.edit_alarm),
        close = close
    ) {
        Column(
            modifier = Modifier,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AlarmDetailControls(
                alarm = selectedAlarm,
                messageList = messageList,
                updateAlarm = updateAlarm,
                openChooseMessageList = { showChooseMessageListDialog.value = true }
            )
        }
    }
}

@SuppressLint("FrequentlyChangingValue")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AlarmDetailControls(
    alarm: Alarm,
    messageList: MessageList?,
    updateAlarm: (Alarm) -> Unit,
    openChooseMessageList: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val fillMaxWidth = Modifier.fillMaxWidth()

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

    val scrollState = rememberScrollState()
    Column(
        modifier = modifier.verticalScroll(scrollState)
            .verticalScrollbar(scrollState)
            .padding(end = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp, Alignment.Top)
    ) {
        TextField(
            modifier = fillMaxWidth,
            state = rememberTextFieldState(initialText = alarm.name),
            inputTransformation = InputTransformation
                .maxLength(BocusViewModel.MAX_NAME_LEN)
                .then {
                    updateAlarm(alarm.copy(name = this.toString()))
                },
            label = { Text(stringResource(R.string.alarm_name_label)) },
            lineLimits = TextFieldLineLimits.SingleLine,
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent
            )
        )
        HorizontalDivider()

        AlarmDetailsMessages(
            currentMessageList = messageList,
            openChooseMessageList = openChooseMessageList,
            modifier = fillMaxWidth,
        )
        HorizontalDivider()

        // Frequencies
        MinMax(
            alarm.frequencyMin,
            alarm.frequencyMax,
            label = stringResource(R.string.frequency),
            onMinChanged = {
                updateAlarm(alarm.copy(frequencyMin = it))
            },
            onMaxChanged = {
                updateAlarm(alarm.copy(frequencyMax = it))
            },
        )
        HorizontalDivider()

        //
        // Start Time
        //
        TimeAccordion(
            timePickerState = startTimeState,
            labelOpen = stringResource(R.string.start_time_label),
            labelClosed = stringResource(R.string.start_time_label_and_value),
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
        HorizontalDivider()
        //
        // End Time
        //
        TimeAccordion(
            timePickerState = endTimeState,
            labelOpen = stringResource(R.string.end_time_label),
            labelClosed = stringResource(R.string.end_time_label_and_value),
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
        HorizontalDivider()

        DaysOfWeek(alarm, updateAlarm)
        HorizontalDivider()

        NotifMode(alarm, updateAlarm)
    }
}

/**
 * Given a list of MessageLists, and an id, return the one with the matching id or null
 */
private fun findMessageListById(messageLists: List<MessageList>, id: Long?): MessageList? {
    if (id == null) return null
    for (messageList in messageLists) {
        if (messageList.id == id) {
            return messageList
        }
    }
    return null
}