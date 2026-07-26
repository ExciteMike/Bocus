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
import com.excitemike.bocus.ui.theme.BocusTheme

class MainActivity : ComponentActivity() {
    lateinit var receiver: AlarmReceiver
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        receiver = AlarmReceiver()
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

fun createNotificationChannel(context: Context) {
    val channel = NotificationChannel(
        AlarmReceiver.ALARM_CHANNEL,
        AlarmReceiver.ALARM_CHANNEL,
        NotificationManager.IMPORTANCE_DEFAULT
    ).apply {
        description = "Bocus Notifications"
    }
    val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.createNotificationChannel(channel)
}