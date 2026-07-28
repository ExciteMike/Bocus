package com.excitemike.bocus.ui.component

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import com.excitemike.bocus.data.Alarm
import com.excitemike.bocus.ui.AlarmDetail

@Composable
fun AlarmDetails(
    alarms: List<Alarm>,
    selectedAlarmIndex: Int?,
    updateAlarm: (alarm: Alarm) -> Unit,
    closeAlarmDetails: () -> Unit,
) {
    if (selectedAlarmIndex != null) {
        if (selectedAlarmIndex in alarms.indices) {
            BackHandler {
                closeAlarmDetails()
            }
            AlarmDetail(
                alarm = alarms[selectedAlarmIndex],
                updateAlarm = updateAlarm,
                close = closeAlarmDetails
            )
        } else {
            closeAlarmDetails()
        }
    }
}