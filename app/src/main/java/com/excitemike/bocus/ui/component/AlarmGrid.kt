package com.excitemike.bocus.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.excitemike.bocus.data.Alarm
import com.excitemike.bocus.data.Command
import com.excitemike.bocus.ui.modifier.fadeTopAndBottom


/**
 * grid of alarms. the main content of the alarm screen
 */
@Composable
fun AlarmGrid(
    modifier: Modifier = Modifier,// TODO: scrollbar modifier based on https://stackoverflow.com/questions/75035946/how-to-add-scrollbars-to-column
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
            modifier = Modifier.padding(top = 8.dp, bottom = 8.dp),
            columns = GridCells.Adaptive(200.dp),
            verticalArrangement = Arrangement.Top,
            horizontalArrangement = Arrangement.Center,
        ) {
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
        }
    }
}
