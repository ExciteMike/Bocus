package com.excitemike.bocus.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.excitemike.bocus.service.UpdateAllAlarmsService
import com.excitemike.bocus.ui.MainActivity

class BootCompletedReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action != Intent.ACTION_BOOT_COMPLETED) {
            return
        }
        val intent = Intent(context, UpdateAllAlarmsService::class.java).apply {
            setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context?.startService(intent)
    }
}