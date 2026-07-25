package com.excitemike.bocus.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.excitemike.bocus.data.Alarm
import com.excitemike.bocus.data.Command
import com.excitemike.bocus.modifier.fadeTopAndBottom


/**
 * grid of alarms. the main content of the alarm screen
 */
@Composable
fun AlarmGrid(
    modifier: Modifier = Modifier,
    alarms: List<Alarm>,
    openAlarmDetails: (Int)->Unit,
    requestDeleteAlarm: (String, Command, Command)->Unit
) {
    Surface (
        tonalElevation = 5.dp,
        modifier = modifier
            .fadeTopAndBottom(16.dp)
    ) {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(200.dp),
            verticalArrangement = Arrangement.Top,
            horizontalArrangement = Arrangement.Center,
        ) {
            item {
                Spacer(Modifier.size(8.dp))
            }
            items(
                alarms,
                key = { it.id!! }
            ) {
                AlarmGridItem(
                    it,
                    alarms,
                    openAlarmDetails,
                    requestDeleteAlarm
                )
            }
            item {
                Spacer(Modifier.size(8.dp))
            }
        }
    }
}
