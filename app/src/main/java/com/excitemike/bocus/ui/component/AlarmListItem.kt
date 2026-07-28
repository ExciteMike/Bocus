package com.excitemike.bocus.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.excitemike.bocus.R
import com.excitemike.bocus.data.Alarm
import com.excitemike.bocus.data.activeDaysString
import com.excitemike.bocus.util.timeString

@Composable
fun AlarmListItem(
    alarm: Alarm,
    modifier: Modifier = Modifier
) {
    val timeFormatStr = stringResource(R.string.time_format)
    Column(modifier = modifier.padding(4.dp)) {
        Row {
            Text(
                text = alarm.name,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Text(
            text = String.format(
                stringResource(R.string.every_x_to_y_minutes),
                alarm.frequencyMin,
                alarm.frequencyMax
            ),
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Text(
            text = String.format(
                stringResource(R.string.time_to_time),
                timeString(timeFormatStr, alarm.startHour, alarm.startMinute),
                timeString(timeFormatStr, alarm.endHour, alarm.endMinute)
            ),
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Text(
            text = activeDaysString(alarm),
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}