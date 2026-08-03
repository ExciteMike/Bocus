package com.excitemike.bocus.ui.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.excitemike.bocus.R
import com.excitemike.bocus.util.FxType
import com.excitemike.bocus.util.timeString

/**
 * Bocus's collapsing time picker
 * @param modifier the Compose Modifier for this composable
 * @param timePickerState he state for the internal [TimeInput]
 * @param labelOpen how to label the UI element when in the open state
 * @param labelClosed how to label the UI element when in the closed state
 * @param onBlur get notified when a part of the time picker loses focus
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeAccordion(
    timePickerState: TimePickerState,
    labelOpen: String,
    labelClosed: String,
    modifier: Modifier = Modifier,
    onBlur: () -> Unit = {}
) {
    val isOpen = rememberSaveable { mutableStateOf(false) }

    AnimatedContent(
        targetState = isOpen.value,
        modifier = modifier,
        label = "time accordion state anim",
    ) { state ->
        if (state) {
            TimeAccordionOpen(
                labelOpen = labelOpen,
                close = { isOpen.value = false },
                onBlur = onBlur,
                timePickerState = timePickerState,
            )
        } else {
            TimeAccordionClosed(
                labelClosed = labelClosed,
                timePickerState = timePickerState,
                open = { isOpen.value = true }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimeAccordionOpen(
    labelOpen: String,
    close: () -> Unit,
    onBlur: () -> Unit = {},
    timePickerState: TimePickerState
) {
    Box(
        modifier = Modifier.border(
            1.dp,
            shape = MaterialTheme.shapes.medium,
            color = DividerDefaults.color
        )
    ) {
        Column(Modifier.padding(start = 8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = labelOpen,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                BocusIconButton(
                    onClick = close,
                    fx = FxType.BACK
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(R.string.close),
                    )
                }
            }
            TimeInput(
                state = timePickerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged {
                        if (!it.isFocused) {
                            onBlur()
                        }
                    },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimeAccordionClosed(
    labelClosed: String,
    timePickerState: TimePickerState,
    open: () -> Unit,
) {
    val timeFormatStr = stringResource(R.string.time_format)
    Box(Modifier.fillMaxWidth().padding(start = 16.dp)) {
        Text(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .clickable(
                    enabled = true,
                    onClickLabel = stringResource(R.string.edit),
                    role = Role.Button,
                    onClick = open
                ),
            text = String.format(
                labelClosed,
                timeString(timeFormatStr, timePickerState.hour, timePickerState.minute)
            ),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        BocusIconButton(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .height(32.dp), // TODO: make configurable
            onClick = open,
            fx = FxType.NORMAL
        ) {
            Icon(
                modifier = Modifier.height(24.dp), // TODO: make configurable
                imageVector = Icons.Default.Edit,
                contentDescription = stringResource(R.string.edit),
            )
        }
    }
}