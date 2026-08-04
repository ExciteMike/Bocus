package com.excitemike.bocus.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.excitemike.bocus.R
import com.excitemike.bocus.data.Alarm
import com.excitemike.bocus.data.AlarmNotifMode
import com.excitemike.bocus.util.checkFlags

/**
 * Widget for picking days of week
 */
@Composable
fun NotifMode(
    alarm: Alarm,
    updateAlarm: (Alarm) -> Unit,
) {
    val context = LocalContext.current
    Column(Modifier.fillMaxWidth()) {
        Text(
            modifier = Modifier.align(Alignment.Start).padding(start = 16.dp),
            text = stringResource(R.string.notif_effects),
            maxLines = 1,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        val chipData = listOf(
            AlarmNotifMode.RING to stringResource(R.string.ring),
            AlarmNotifMode.VIBRATE to stringResource(R.string.vibrate),
        )
        Row {
            for ((mask, label) in chipData) {
                val active = checkFlags(alarm.notifMode, mask)
                FilterChip(
                    selected = active,
                    onClick = {
                        val bits = if (active) {
                            alarm.notifMode and mask.inv()
                        } else {
                            alarm.notifMode or mask
                        }
                        updateAlarm(alarm.copy(notifMode = bits))
                    },
                    label = {
                        Text(
                            modifier = Modifier.align(Alignment.CenterVertically),
                            text = label,
                            maxLines = 1,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = if (active) {
                                FontWeight.Bold
                            } else {
                                FontWeight.Light
                            }
                        )
                    },
                )
            }
        }
    }
}