package com.excitemike.bocus.ui.component

import androidx.compose.foundation.border
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
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.excitemike.bocus.R
import com.excitemike.bocus.util.timeString

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeAccordion(state: TimePickerState, labelOpen: String, labelClosed: String) {
    // TODO: use AnimatedContent
    val timeFormatStr = stringResource(R.string.time_format)
    val isOpen = remember { mutableStateOf(false) }
    if (isOpen.value) {
        Box(
            modifier = Modifier.border(
                1.dp,
                shape = MaterialTheme.shapes.medium,
                color = DividerDefaults.color
            )
        ) {
            Column(Modifier.padding(horizontal = 8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        modifier = Modifier.weight(1f),
                        text = labelOpen,
                        style = MaterialTheme.typography.titleSmall
                    )
                    IconButton(
                        onClick = { isOpen.value = false },
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(R.string.close),
                        )
                    }
                }
                TimeInput(
                    state = state,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    } else {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = String.format(
                    labelClosed,
                    timeString(timeFormatStr, state.hour, state.minute)
                ),
                style = MaterialTheme.typography.titleSmall
            )
            IconButton(
                modifier = Modifier.height(32.dp), // TODO: make configurable
                onClick = { isOpen.value = true },
            ) {
                Icon(
                    modifier = Modifier.height(24.dp), // TODO: make configurable
                    imageVector = Icons.Default.Edit,
                    contentDescription = stringResource(R.string.edit),
                )
            }
        }
    }
}