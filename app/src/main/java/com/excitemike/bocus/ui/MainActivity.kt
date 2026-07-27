package com.excitemike.bocus.ui

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.AudioAttributes
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
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
    val audioAttributes = AudioAttributes.Builder()
        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
        .build()
    val channel = NotificationChannel(
        AlarmReceiver.ALARM_CHANNEL,
        AlarmReceiver.ALARM_CHANNEL,
        NotificationManager.IMPORTANCE_HIGH
    ).apply {
        description = "Bocus Alarms"
        setSound(Settings.System.DEFAULT_ALARM_ALERT_URI, audioAttributes)
        enableLights(true)
        lightColor = Color.Red.toArgb()
        enableVibration(true)
        vibrationPattern = longArrayOf(0, 50, 200, 100, 150, 150, 100, 1000)
    }
    val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.createNotificationChannel(channel)
}