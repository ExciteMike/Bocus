package com.excitemike.bocus.receiver

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.CombinedVibration
import android.os.VibrationAttributes
import android.os.VibrationEffect
import android.os.VibratorManager
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.excitemike.bocus.R
import com.excitemike.bocus.data.AlarmDatabase
import com.excitemike.bocus.data.AlarmNotifMode
import com.excitemike.bocus.data.OfflineBocusRepository
import com.excitemike.bocus.data.getNextAlarmTime
import com.excitemike.bocus.data.scheduleSystemAlarm
import com.excitemike.bocus.util.checkFlags
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class AlarmReceiver : BroadcastReceiver() {

    /**
     * start vibration
     */
    private fun doVibration(context: Context) {
        if (vibMgr == null) {
            vibMgr = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        }
        vibMgr?.vibrate(
            CombinedVibration.createParallel(
                VibrationEffect.createWaveform(
                    longArrayOf(
                        0,
                        50,
                        200,
                        100,
                        150,
                        150,
                        100,
                        1000
                    ),
                    -1
                )
            ),
            VibrationAttributes.Builder()
                .setUsage(VibrationAttributes.USAGE_ALARM)
                .build()
        )
    }

    /**
     * called when the receiver is receiving an intent broadcast
     */
    @SuppressLint("LaunchActivityFromNotification")
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null) return
        if (intent == null) return

        if (intent.action == STOP_ALARM_ACTION) {
            val alarmId = intent.getIntExtra(EXTRA_NAME_ALARM_ID, 0)
            stopAlarm(context, alarmId)
            return
        }

        val title = intent.getStringExtra(EXTRA_NAME_TITLE)
        val message = intent.getStringExtra(EXTRA_NAME_MESSAGE)
        val alarmId = intent.getIntExtra(EXTRA_NAME_ALARM_ID, 0)
        val notifFx = intent.getIntExtra(EXTRA_NAME_ALARM_NOTIF_FX, AlarmNotifMode.RING_AND_VIBRATE)
        val stopIntent = Intent(context, AlarmReceiver::class.java).apply {
            action = STOP_ALARM_ACTION
            putExtra(EXTRA_NAME_ALARM_ID, alarmId)
            putExtra(EXTRA_NAME_ALARM_NOTIF_FX, notifFx)
        }
        val stopPendingIntent = PendingIntent.getBroadcast(
            context,
            STOP_CODE + alarmId,
            stopIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val builder = NotificationCompat.Builder(context, ALARM_CHANNEL)
            .setSmallIcon(R.drawable.ic_big)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setContentIntent(stopPendingIntent)
            .addAction(
                NotificationCompat.Action(
                    R.drawable.ic_big,
                    context.getString(R.string.stop),
                    stopPendingIntent
                )
            )
            .setDeleteIntent(stopPendingIntent)

        if (checkFlags(notifFx, AlarmNotifMode.RING)) {
            playSound(context)
        }
        if (checkFlags(notifFx, AlarmNotifMode.VIBRATE)) {
            doVibration(context)
        }

        if (ActivityCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            NotificationManagerCompat.from(context).notify(alarmId, builder.build())
        }
    }

    /**
     * start a sound playing for the notification
     */
    private fun playSound(context: Context) {
        if (mediaPlayer != null) {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
        }
        mediaPlayer =
            MediaPlayer.create(context, Settings.System.DEFAULT_ALARM_ALERT_URI)
        mediaPlayer?.start()
    }

    /**
     * stop an alarm, potentially restarting it
     */
    private fun stopAlarm(context: Context, alarmId: Int) {
        // cancel notification, sound, and vibration
        NotificationManagerCompat.from(context).cancel(alarmId)
        stopSound()
        stopVibration()

        // schedule the next occurrence, if it hasn't been deleted
        val repo = OfflineBocusRepository(
            AlarmDatabase.getDatabase(context).alarmDao(),
        )
        val job = SupervisorJob()
        val scope = CoroutineScope(Dispatchers.IO + job)
        scope.launch {
            val alarm = repo.getAlarm(alarmId)
            if (alarm != null) {
                val newAlarm = alarm.copy(scheduledAt = getNextAlarmTime(alarm))
                scheduleSystemAlarm(context, newAlarm)
            }
        }
    }

    /**
     * stop the sound, if one was playing
     */
    private fun stopSound() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    /**
     * stop the vibration, if one was playing
     */
    private fun stopVibration() {
        if (vibMgr != null) {
            vibMgr?.cancel()
            vibMgr = null
        }
    }

    companion object {
        // shared instance of MediaPlayer
        private var mediaPlayer: MediaPlayer? = null

        // shared instance of VibratorManager
        private var vibMgr: VibratorManager? = null

        const val STOP_CODE = 456
        const val EXTRA_NAME_TITLE = "title"
        const val EXTRA_NAME_MESSAGE = "message"
        const val EXTRA_NAME_ALARM_ID = "alarm_id"
        const val EXTRA_NAME_ALARM_NOTIF_FX = "alarm_flags"
        const val PLAY_ALARM_ACTION = "play_alarm"
        const val STOP_ALARM_ACTION = "stop_alarm"
        const val ALARM_CHANNEL = "alarm_channel"
    }
}
