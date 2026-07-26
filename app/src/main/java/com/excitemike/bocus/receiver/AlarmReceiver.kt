package com.excitemike.bocus.receiver

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.provider.Settings
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.excitemike.bocus.R
import com.excitemike.bocus.ui.MainActivity

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val mediaPlayer: MediaPlayer =
            MediaPlayer.create(context, Settings.System.DEFAULT_ALARM_ALERT_URI)
        mediaPlayer.isLooping = true

        Log.v("BocusTrace", "AlarmReceiver.onReceive intent $intent")

        if (intent == null) return

        Log.v("BocusTrace", "AlarmReceiver.onReceive intent action = ${intent.action}")
        Log.v("BocusTrace", "extra = ${intent.getIntExtra(EXTRA_NAME_ALARM_ID, 0)}")

        if (intent.action == STOP_ALARM_ACTION) {
            val alarmId = intent.getIntExtra(EXTRA_NAME_ALARM_ID, 0)
            NotificationManagerCompat.from(context).cancel(alarmId)

            mediaPlayer.release()
            mediaPlayer.stop()

            val intentToCancel = Intent(context, AlarmReceiver::class.java).apply {
                action = PLAY_ALARM_ACTION
            }
            alarmManager.cancel(
                PendingIntent.getBroadcast(
                    context,
                    alarmId,
                    intentToCancel,
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_CANCEL_CURRENT
                )
            )

            return
        }

        val title = intent.getStringExtra(EXTRA_NAME_TITLE)
        val message = intent.getStringExtra(EXTRA_NAME_MESSAGE)
        val alarmId = intent.getIntExtra(EXTRA_NAME_ALARM_ID, 0)
        val newIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            NOTIF_REQ_CODE,
            newIntent,
            PendingIntent.FLAG_IMMUTABLE
        )
        val stopIntent = Intent(context, AlarmReceiver::class.java).apply {
            action = STOP_ALARM_ACTION
            putExtra(EXTRA_NAME_ALARM_ID, alarmId)
        }
        val stopPendingIntent = PendingIntent.getBroadcast(
            context,
            STOP_CODE + alarmId,
            stopIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val builder = NotificationCompat.Builder(context, ALARM_CHANNEL)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setContentIntent(pendingIntent)
            .addAction(
                NotificationCompat.Action(
                    R.drawable.ic_launcher_foreground,
                    context.getString(R.string.stop),
                    stopPendingIntent
                )
            )
            .setOngoing(true)

        if (ActivityCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            NotificationManagerCompat.from(context).notify(alarmId, builder.build())
            mediaPlayer.start()
        }
    }

    companion object {
        const val STOP_CODE = 456
        const val NOTIF_REQ_CODE = 789
        const val EXTRA_NAME_TITLE = "title"
        const val EXTRA_NAME_MESSAGE = "message"
        const val EXTRA_NAME_ALARM_ID = "alarm_id"
        const val PLAY_ALARM_ACTION = "play_alarm"
        const val STOP_ALARM_ACTION = "stop_alarm"
        const val ALARM_CHANNEL = "alarm_channel"
    }
}