package com.excitemike.bocus.ui

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.excitemike.bocus.R
import com.excitemike.bocus.data.AlarmDatabase
import com.excitemike.bocus.receiver.AlarmReceiver
import com.excitemike.bocus.ui.theme.BocusTheme
import com.excitemike.bocus.ui.viewmodel.AlarmScreenViewModel
import com.excitemike.bocus.ui.viewmodel.BillingViewModel
import com.excitemike.bocus.ui.viewmodel.MessagesViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        createNotificationChannel(this)

        setContent {
            val alarmDao = AlarmDatabase.getDatabase(application).alarmDao()
            val messageDao = AlarmDatabase.getDatabase(application).messageDao()
            val alarmScreenViewModel =
                viewModel { AlarmScreenViewModel(alarmDao = alarmDao) }
            val defaultMessage = stringResource(R.string.default_message)
            val messageListsScreenViewModel =
                viewModel { MessagesViewModel(messageDao, defaultMessage) }
            val billingViewModel = viewModel { BillingViewModel(this@MainActivity) }
            LaunchedEffect(true) {
                billingViewModel.init()
            }

            BocusTheme {
                BocusApp(
                    activity = this,
                    alarmScreenViewModel = alarmScreenViewModel,
                    billingViewModel = billingViewModel,
                    messagesViewModel = messageListsScreenViewModel
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