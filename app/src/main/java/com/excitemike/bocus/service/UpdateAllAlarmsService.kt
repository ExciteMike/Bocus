package com.excitemike.bocus.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.excitemike.bocus.data.AlarmDatabase
import com.excitemike.bocus.data.OfflineBocusRepository
import com.excitemike.bocus.data.updateAllSystemAlarms
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class UpdateAllAlarmsService: Service() {
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        Log.v("Bocus", "UpdateAllAlarmsService started")
        if (intent?.action != Intent.ACTION_BOOT_COMPLETED) {
            return START_NOT_STICKY
        }
        val dao = AlarmDatabase.getDatabase(application).alarmDao()
        val alarmRepo = OfflineBocusRepository(dao)
        val job = SupervisorJob()
        val scope = CoroutineScope(Dispatchers.IO + job)
        scope.launch {
            val alarms = alarmRepo.getAllAlarmsRaw()
            updateAllSystemAlarms(application, alarms)
        }

        return START_NOT_STICKY
    }
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }
}