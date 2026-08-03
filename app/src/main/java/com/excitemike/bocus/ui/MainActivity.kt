package com.excitemike.bocus.ui

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.excitemike.bocus.data.AlarmDatabase
import com.excitemike.bocus.receiver.AlarmReceiver
import com.excitemike.bocus.receiver.BootCompletedReceiver
import com.excitemike.bocus.ui.theme.BocusTheme
import com.excitemike.bocus.ui.viewmodel.MessageListScreenViewModel
import com.excitemike.bocus.util.Fx

class MainActivity : ComponentActivity() {
    lateinit var alarmReceiver: AlarmReceiver
    lateinit var bootCompletedReceiver: BootCompletedReceiver
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        Fx.init(this)

        // TODO: I'm not actually sure instantiating the receivers here accomplishes anything
        alarmReceiver = AlarmReceiver()
        bootCompletedReceiver = BootCompletedReceiver()

        createNotificationChannel(this)

        setContent {
            val bocusViewModel = viewModel { BocusViewModel(application) }
            val messageListDao = AlarmDatabase.getDatabase(application).messageListDao()
            val messageDao = AlarmDatabase.getDatabase(application).messageDao()
            val messageListsScreenViewModel =
                viewModel { MessageListScreenViewModel(messageListDao, messageDao) }

            BocusTheme {
                BocusApp(
                    activity = this,
                    viewModel = bocusViewModel,
                    messageListScreenViewModel = messageListsScreenViewModel
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