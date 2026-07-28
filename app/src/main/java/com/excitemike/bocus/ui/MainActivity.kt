package com.excitemike.bocus.ui

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.excitemike.bocus.receiver.AlarmReceiver
import com.excitemike.bocus.receiver.BootCompletedReceiver
import com.excitemike.bocus.ui.theme.BocusTheme

class MainActivity : ComponentActivity() {
    lateinit var alarmReceiver: AlarmReceiver
    lateinit var bootCompletedReceiver: BootCompletedReceiver
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // TODO: I'm not actually sure instantiating the receivers here accomplishes anything
        alarmReceiver = AlarmReceiver()
        bootCompletedReceiver = BootCompletedReceiver()

        createNotificationChannel(this)

        setContent {
            BocusTheme {
                BocusApp(
                    activity = this,
                    viewModel = viewModel { BocusViewModel(application) },
                )
            }
        }
    }
}

/**
 * prepare a notification channel
 */
fun createNotificationChannel(context: Context) {
    val channel = NotificationChannel(
        AlarmReceiver.ALARM_CHANNEL,
        AlarmReceiver.ALARM_CHANNEL,
        NotificationManager.IMPORTANCE_HIGH
    ).apply {
        description = "Bocus Alarms"
        setSound(null, null)
        enableVibration(false)
    }
    val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.createNotificationChannel(channel)
}