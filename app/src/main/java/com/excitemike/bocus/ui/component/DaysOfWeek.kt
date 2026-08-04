package com.excitemike.bocus.ui.component

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
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
import com.excitemike.bocus.data.AlarmDayOfWeekFlags
import com.excitemike.bocus.util.checkFlags

/**
 * Widget for picking days of week
 */
@Composable
fun DaysOfWeek(
    alarm: Alarm,
    updateAlarm: (Alarm) -> Unit,
) {
    val context = LocalContext.current
    Column {
        Text(
            modifier = Modifier.align(Alignment.Start).padding(start = 16.dp),
            text = stringResource(R.string.days_of_week),
            maxLines = 1,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        val dayData = listOf(
            AlarmDayOfWeekFlags.SUNDAY to stringResource(R.string.day_short_sunday),
            AlarmDayOfWeekFlags.MONDAY to stringResource(R.string.day_short_monday),
            AlarmDayOfWeekFlags.TUESDAY to stringResource(R.string.day_short_tuesday),
            AlarmDayOfWeekFlags.WEDNESDAY to stringResource(R.string.day_short_wednesday),
            AlarmDayOfWeekFlags.THURSDAY to stringResource(R.string.day_short_thursday),
            AlarmDayOfWeekFlags.FRIDAY to stringResource(R.string.day_short_friday),
            AlarmDayOfWeekFlags.SATURDAY to stringResource(R.string.day_short_saturday),
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .height(50.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            for ((mask, label) in dayData) {
                val active = checkFlags(alarm.activeDays, mask)
                Box(modifier = Modifier.height(48.dp)) {
                    FilterChip(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .height(40.dp)
                            .width(42.dp),
                        selected = active,
                        onClick = {
                            val bits = if (active) {
                                alarm.activeDays and mask.inv()
                            } else {
                                alarm.activeDays or mask
                            }
                            updateAlarm(alarm.copy(activeDays = bits))
                        },
                        label = {},
                    )
                    Text(
                        modifier = Modifier.align(Alignment.Center),
                        text = label,
                        maxLines = 1,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = if (active) {
                            FontWeight.Bold
                        } else {
                            FontWeight.Light
                        }
                    )
                }
            }
        }
    }
}