package com.excitemike.bocus.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AlarmReceiver: BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
        TODO("Not yet implemented")
    }

    companion object {
        const val EXTRA_NAME_TITLE = "title"
        const val EXTRA_NAME_MESSAGE = "message"
        const val EXTRA_NAME_ALARM_ID = "alarm_id"
    }
}