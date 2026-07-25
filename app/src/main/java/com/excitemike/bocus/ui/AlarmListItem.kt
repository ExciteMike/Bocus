package com.excitemike.bocus.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.excitemike.bocus.R
import com.excitemike.bocus.data.Alarm
import com.excitemike.bocus.data.AlarmDayOfWeekFlags
import com.excitemike.bocus.data.activeDaysString
import com.excitemike.bocus.data.timeString

@Composable
fun DayIcon(isOn: Boolean, dayStringId: Int) {
    val textDecoration = if (isOn) { null } else { TextDecoration.LineThrough }
    val fontWeight = if (isOn) { FontWeight.Bold } else { FontWeight.Normal }
    OutlinedCard (modifier = Modifier.padding(horizontal = 2.dp)) {
        Text(
            modifier = Modifier.padding(4.dp),
            text = stringResource(dayStringId),
            textDecoration = textDecoration,
            fontWeight = fontWeight,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun AlarmListItem(
    alarm: Alarm,
    modifier: Modifier = Modifier
) {
    val timeFormatStr = stringResource(R.string.time_format)
    Card (modifier=modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 4.dp),
    ) {
        Column (modifier = Modifier.fillMaxWidth().padding(16.dp)) {
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
}