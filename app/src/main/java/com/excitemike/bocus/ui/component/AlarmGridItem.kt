package com.excitemike.bocus.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.excitemike.bocus.R
import com.excitemike.bocus.data.Alarm
import com.excitemike.bocus.data.Command
import com.excitemike.bocus.util.Fx
import com.excitemike.bocus.util.FxType
import kotlinx.coroutines.launch


@Composable
fun AlarmGridItem(
    modifier: Modifier = Modifier,
    alarm: Alarm,
    allAlarms: List<Alarm>,
    openAlarmDetails: (Int) -> Unit,
    requestDeleteAlarm: (String, Command, Command) -> Unit,
    deleteAlarmById: (Int) -> Unit
) {
    val context = LocalContext.current
    val index = lazy { allAlarms.indexOfFirst { alarm.id == it.id } }
    val deleteAlarmTemplate = stringResource(R.string.confirm_delete_alarm)
    val swipeToDismissState = rememberSwipeToDismissBoxState()
    val scope = rememberCoroutineScope()
    val shape = MaterialTheme.shapes.medium
    val requestDeleteThisItem = {
        // TODO: play delete fx
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
    val confirmFormat = stringResource(R.string.confirm_delete_alarm)
    val confirmPrompt = String.format(confirmFormat, alarm.name)
    val isConfirming = rememberSaveable{ mutableStateOf(false) }
    BocusSwipeToDismissBox(
        dismissConfirmPrompt = confirmPrompt,
        onConfirm = { deleteAlarmById(alarm.id!!) },
        modifier = modifier.padding(end = 8.dp),
        state = isConfirming
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 4.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                AlarmListItem(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            Fx.buttonClickFx(context, FxType.SWISH)
                            openAlarmDetails(index.value)
                        },
                    alarm = alarm
                )
                Column {
                    BocusIconButton(
                        onClick = { openAlarmDetails(index.value) },
                        fx = FxType.NORMAL
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = stringResource(R.string.edit),
                        )

                    }
                    BocusIconButton(
                        onClick = { isConfirming.value = true },
                        fx = FxType.NORMAL
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = stringResource(R.string.edit)
                        )
                    }
                }
            }
        }
    }
}