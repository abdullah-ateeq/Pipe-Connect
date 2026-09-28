package com.example.pipeconnectboard.haptics

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

enum class HapticType {
    SELECTION,
    ROTATION,
    PLACEMENT,
    CONNECTION_EXTENDED,
    SUCCESS,
    FAILURE
}

class HapticManager(private val context: Context) {

    var isEnabled: Boolean = true

    private val vibrator: Vibrator? by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

    fun trigger(type: HapticType) {
        if (!isEnabled) return
        val vib = vibrator ?: return
        if (!vib.hasVibrator()) return

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val effect = when (type) {
                    HapticType.SELECTION -> VibrationEffect.createOneShot(12, 50)
                    HapticType.ROTATION -> VibrationEffect.createOneShot(16, 60)
                    HapticType.PLACEMENT -> VibrationEffect.createOneShot(24, 100)
                    HapticType.CONNECTION_EXTENDED -> VibrationEffect.createWaveform(
                        longArrayOf(0, 20, 40, 25),
                        intArrayOf(0, 80, 0, 140),
                        -1
                    )
                    HapticType.SUCCESS -> VibrationEffect.createWaveform(
                        longArrayOf(0, 30, 50, 40, 50, 80),
                        intArrayOf(0, 100, 0, 160, 0, 220),
                        -1
                    )
                    HapticType.FAILURE -> VibrationEffect.createWaveform(
                        longArrayOf(0, 60, 40, 60),
                        intArrayOf(0, 150, 0, 120),
                        -1
                    )
                }
                vib.vibrate(effect)
            } else {
                @Suppress("DEPRECATION")
                when (type) {
                    HapticType.SELECTION, HapticType.ROTATION -> vib.vibrate(15)
                    HapticType.PLACEMENT, HapticType.CONNECTION_EXTENDED -> vib.vibrate(30)
                    HapticType.SUCCESS -> vib.vibrate(longArrayOf(0, 30, 40, 60), -1)
                    HapticType.FAILURE -> vib.vibrate(longArrayOf(0, 50, 30, 50), -1)
                }
            }
        } catch (_: Exception) {
            // Haptics failure shouldn't crash app
        }
    }
}
