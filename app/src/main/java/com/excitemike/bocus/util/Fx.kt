package com.excitemike.bocus.util

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.CombinedVibration
import android.os.VibrationAttributes
import android.os.VibrationEffect
import android.os.VibratorManager
import com.excitemike.bocus.R

enum class FxType {
    BACK,
    CANCEL,
    CONFIRM,
    DELETE,
    EDIT,
    NORMAL,
    SWISH,
}

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
                .setMaxStreams(3)
                .setContext(context)
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .build()
                ).build()
            backSound = soundPool!!.load(context, R.raw.lowerbloop, 1)
            cancelSound = soundPool!!.load(context, R.raw.cancel, 1)
            confirmSound = soundPool!!.load(context, R.raw.highblip, 1)
            deleteSound = soundPool!!.load(context, R.raw.delete, 1)
            editSound = soundPool!!.load(context, R.raw.edit, 1)
            normalSound = soundPool!!.load(context, R.raw.lowbloop, 1)
            swishSound = soundPool!!.load(context, R.raw.swish, 1)
        }

        /**
         * play sound + vibration to accompany taps
         */
        fun buttonClickFx(context: Context, kind: FxType) {
            val soundId = when (kind) {
                FxType.BACK -> backSound
                FxType.CANCEL -> cancelSound
                FxType.CONFIRM -> confirmSound
                FxType.DELETE -> deleteSound
                FxType.EDIT -> editSound
                FxType.NORMAL -> normalSound
                FxType.SWISH -> swishSound
            }
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

        private var backSound: Int = -1
        private var cancelSound: Int = -1
        private var confirmSound: Int = -1
        private var deleteSound: Int = -1
        private var editSound: Int = -1
        private var normalSound: Int = -1
        private var swishSound: Int = -1
        private var soundPool: SoundPool? = null
    }
}