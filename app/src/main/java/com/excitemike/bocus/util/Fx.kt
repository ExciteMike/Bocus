package com.excitemike.bocus.util

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.CombinedVibration
import android.os.VibrationAttributes
import android.os.VibrationEffect
import android.os.VibratorManager
import com.excitemike.bocus.R

class Fx {

    companion object {
        /**
         * prepare for use
         */
        fun init(context: Context) {
            if (soundPool != null) {
                return
            }
            soundPool = SoundPool.Builder()
                .setMaxStreams(1)
                .setContext(context)
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .build()
                ).build()
            soundId = soundPool!!.load(context, R.raw.blip15, 1)
        }

        /**
         * play sound + vibration to accompany taps
         */
        fun buttonClickFx(context: Context) {
            soundPool?.play(
                soundId,
                1f,
                1f,
                0,
                0,
                1f
            )

            val vibMgr =
                context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            val vibFx = VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK)
            val vibAttr = VibrationAttributes.Builder()
                .setUsage(VibrationAttributes.USAGE_TOUCH)
                .build()
            vibMgr.vibrate(CombinedVibration.createParallel(vibFx), vibAttr)
        }

        private var soundId: Int = -1
        private var soundPool: SoundPool? = null
    }
}