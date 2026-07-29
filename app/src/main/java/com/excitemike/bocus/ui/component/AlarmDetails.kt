package com.excitemike.bocus.ui.component

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.excitemike.bocus.data.Alarm
import com.excitemike.bocus.ui.AlarmDetail
import com.excitemike.bocus.util.Fx
import com.excitemike.bocus.util.FxType

@Composable
fun AlarmDetails(
    alarms: List<Alarm>,
    selectedAlarmIndex: Int?,
    updateAlarm: (alarm: Alarm) -> Unit,
    closeAlarmDetails: () -> Unit,
) {
    val context = LocalContext.current
    if (selectedAlarmIndex != null) {
        if (selectedAlarmIndex in alarms.indices) {
            BackHandler {
                Fx.buttonClickFx(context, FxType.BACK)
                closeAlarmDetails()
            }
            AlarmDetail(
                alarm = alarms[selectedAlarmIndex],
                updateAlarm = updateAlarm,
                close = {
                    if (it) {
                        Fx.buttonClickFx(context, FxType.BACK)
                    }
                    closeAlarmDetails()
                }
            )
        } else {
            closeAlarmDetails()
        }
    }
}