package com.excitemike.bocus.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFrom
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.excitemike.bocus.R
import com.excitemike.bocus.data.Alarm
import com.excitemike.bocus.data.FRIDAY
import com.excitemike.bocus.data.MONDAY
import com.excitemike.bocus.data.NotifMode
import com.excitemike.bocus.data.SATURDAY
import com.excitemike.bocus.data.SUNDAY
import com.excitemike.bocus.data.THURSDAY
import com.excitemike.bocus.data.TUESDAY
import com.excitemike.bocus.data.WEDNESDAY
import com.excitemike.bocus.data.timeString

fun checkFlags(bits:UByte, desiredBits:UByte): Boolean {
    return desiredBits == (bits and desiredBits)
}

@Composable
fun DayIcon(isOn: Boolean, dayStringId: Int) {
    val colors = if (isOn)  { CardDefaults.outlinedCardColors() } else { CardDefaults.cardColors() }
    val border = if (isOn) { CardDefaults.outlinedCardBorder() } else { null }
    val shape = RoundedCornerShape(16.dp)
    val textDecoration = if (isOn) { null } else { TextDecoration.LineThrough }
    val fontWeight = if (isOn) { FontWeight.Bold } else { FontWeight.Normal }
    Card (
        shape = shape,
        //colors = colors,
        //border = border,
    ) {
        Text(
            modifier = Modifier.padding(4.dp),
            text = stringResource(dayStringId),
            textDecoration = textDecoration,
            fontWeight = fontWeight
        )
    }
}

@Composable
fun AlarmListItem(
    alarm: Alarm,
    modifier: Modifier = Modifier
) {
    Card (modifier=modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 4.dp),
    ) {
        Column (modifier = Modifier.fillMaxWidth().padding(20.dp)) {
            Text(
                text = alarm.name,
                style = MaterialTheme.typography.titleMedium
            )

            Text(text = String.format(
                    stringResource(R.string.every_x_to_y_minutes),
                    alarm.frequency.first,
                    alarm.frequency.second
                ))

            Text(text = String.format(
                stringResource(R.string.time_to_time),
                timeString(alarm.startTime),
                timeString(alarm.endTime)
            ))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(modifier = Modifier.padding(end = 4.dp), text = stringResource(R.string.on))

                DayIcon(
                    isOn = checkFlags(alarm.activeDays, SUNDAY),
                    dayStringId = R.string.day_short_sunday
                )
                DayIcon(
                    isOn = checkFlags(alarm.activeDays, MONDAY),
                    dayStringId = R.string.day_short_monday
                )
                DayIcon(
                    isOn = checkFlags(alarm.activeDays, TUESDAY),
                    dayStringId = R.string.day_short_tuesday
                )
                DayIcon(
                    isOn = checkFlags(alarm.activeDays, WEDNESDAY),
                    dayStringId = R.string.day_short_wednesday
                )
                DayIcon(
                    isOn = checkFlags(alarm.activeDays, THURSDAY),
                    dayStringId = R.string.day_short_thursday
                )
                DayIcon(
                    isOn = checkFlags(alarm.activeDays, FRIDAY),
                    dayStringId = R.string.day_short_friday
                )
                DayIcon(
                    isOn = checkFlags(alarm.activeDays, SATURDAY),
                    dayStringId = R.string.day_short_saturday
                )
            }

            val bellStrId = when (alarm.notifMode) {
                NotifMode.Bell -> R.string.ring_bell
                NotifMode.Vibrate -> R.string.vibrate
                NotifMode.BellAndVibrate -> R.string.ring_bell_and_vibrate
            }

            Text(text = stringResource(bellStrId))

            Text(text = String.format(
                stringResource(R.string.show_message_x),
                alarm.message
            ))
        }
    }
}