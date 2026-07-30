package com.excitemike.bocus.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.excitemike.bocus.data.AlarmDatabase
import com.excitemike.bocus.data.OfflineBocusRepository
import com.excitemike.bocus.data.rescheduleAllSystemAlarms
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class UpdateAllAlarmsService : Service() {
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        if (intent?.action != ACTION_RESCHEDULE_ALL_ALARMS) {
            return START_NOT_STICKY
        }
        val dao = AlarmDatabase.getDatabase(application).alarmDao()
        val alarmRepo = OfflineBocusRepository(dao)
        val job = SupervisorJob()
        val scope = CoroutineScope(Dispatchers.IO + job)
        scope.launch {
            val alarms = alarmRepo.getAllAlarmsRaw()
            rescheduleAllSystemAlarms(
                application,
                alarms,
                updateAlarm = { newAlarm -> alarmRepo.updateAlarm(newAlarm) }
            )
        }

        return START_NOT_STICKY
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    companion object {
        const val ACTION_RESCHEDULE_ALL_ALARMS = "reschedule_all_alarms"
    }
}