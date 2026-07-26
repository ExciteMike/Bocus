package com.excitemike.bocus.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.excitemike.bocus.R
import com.excitemike.bocus.data.Alarm
import com.excitemike.bocus.data.Command
import com.excitemike.bocus.ui.AlarmListItem
import kotlinx.coroutines.launch


@Composable
fun AlarmGridItem(
    alarm: Alarm,
    allAlarms: List<Alarm>,
    openAlarmDetails: (Int) -> Unit,
    requestDeleteAlarm: (String, Command, Command) -> Unit
) {
    val index = lazy { allAlarms.indexOfFirst { alarm.id == it.id } }
    val deleteAlarmTemplate = stringResource(R.string.confirm_delete_alarm)
    val swipeToDismissState = rememberSwipeToDismissBoxState()
    val scope = rememberCoroutineScope()
    SwipeToDismissBox(
        state = swipeToDismissState,
        backgroundContent = {
            val deleteColor = MaterialTheme.colorScheme.errorContainer
            val bgColor =
                if (swipeToDismissState.targetValue == SwipeToDismissBoxValue.Settled) {
                    Color.Transparent
                } else {
                    deleteColor
                }

            Row(
                Modifier
                    .padding(4.dp)
                    .background(
                        color = bgColor,
                        shape = RoundedCornerShape(16.dp),
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (swipeToDismissState.targetValue != SwipeToDismissBoxValue.EndToStart) {
                    Icon(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onErrorContainer
                    )
                } else {
                    Spacer(Modifier.weight(1f))
                }
                if (swipeToDismissState.targetValue != SwipeToDismissBoxValue.StartToEnd) {
                    Icon(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onErrorContainer
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
                Command.Callback { scope.launch { swipeToDismissState.reset() } }
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