package com.example.pipeconnectboard.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

enum class SoundEffect {
    SELECT,
    ROTATE,
    PLACE,
    EXTEND,
    WIN,
    FAIL
}

class SoundPlayer(private val coroutineScope: CoroutineScope) {

    private val sampleRate = 44100
    var isEnabled: Boolean = true

    // Pre-rendered PCM buffers for instant zero-latency playback
    private val audioBuffers: Map<SoundEffect, ShortArray> by lazy {
        mapOf(
            SoundEffect.SELECT to generateSelectSound(),
            SoundEffect.ROTATE to generateRotateSound(),
            SoundEffect.PLACE to generatePlaceSound(),
            SoundEffect.EXTEND to generateExtendSound(),
            SoundEffect.WIN to generateWinSound(),
            SoundEffect.FAIL to generateFailSound()
        )
    }

    fun play(effect: SoundEffect) {
        if (!isEnabled) return

        coroutineScope.launch(Dispatchers.Default) {
            try {
                val samples = audioBuffers[effect] ?: return@launch
                val bufferSize = samples.size * 2 // 2 bytes per 16-bit sample

                val audioTrack = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_GAME)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(bufferSize)
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()

                audioTrack.write(samples, 0, samples.size)
                audioTrack.play()

                // Clean up track once finished
                val durationMs = (samples.size * 1000L) / sampleRate
                kotlinx.coroutines.delay(durationMs + 50)
                audioTrack.stop()
                audioTrack.release()
            } catch (_: Exception) {
                // Audio failure shouldn't crash gameplay
            }
        }
    }

    private fun generateSelectSound(): ShortArray {
        val durationMs = 28
        val numSamples = (sampleRate * durationMs) / 1000
        val buffer = ShortArray(numSamples)
        val freq = 1100.0
        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            val envelope = exp(-t * 120.0)
            val sample = (sin(2.0 * PI * freq * t) * envelope * 0.4 * Short.MAX_VALUE).toInt()
            buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        return buffer
    }

    private fun generateRotateSound(): ShortArray {
        val durationMs = 45
        val numSamples = (sampleRate * durationMs) / 1000
        val buffer = ShortArray(numSamples)
        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            val freq = 1400.0 - (t * 8000.0) // pitch slide downward ratchet
            val envelope = exp(-t * 70.0)
            val sample = (sin(2.0 * PI * freq * t) * envelope * 0.45 * Short.MAX_VALUE).toInt()
            buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        return buffer
    }

    private fun generatePlaceSound(): ShortArray {
        val durationMs = 70
        val numSamples = (sampleRate * durationMs) / 1000
        val buffer = ShortArray(numSamples)
        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            val envelope = exp(-t * 50.0)
            val s1 = sin(2.0 * PI * 260.0 * t)
            val s2 = sin(2.0 * PI * 520.0 * t) * 0.5
            val sample = ((s1 + s2) * envelope * 0.5 * Short.MAX_VALUE).toInt()
            buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        return buffer
    }

    private fun generateExtendSound(): ShortArray {
        val durationMs = 130
        val numSamples = (sampleRate * durationMs) / 1000
        val buffer = ShortArray(numSamples)
        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            val envelope = exp(-t * 22.0)
            val s1 = sin(2.0 * PI * 880.0 * t)
            val s2 = sin(2.0 * PI * 1320.0 * t) * 0.4
            val sample = ((s1 + s2) * envelope * 0.55 * Short.MAX_VALUE).toInt()
            buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        return buffer
    }

    private fun generateWinSound(): ShortArray {
        val durationMs = 450
        val numSamples = (sampleRate * durationMs) / 1000
        val buffer = ShortArray(numSamples)
        // Ascending harmonic arpeggio: C5 (523), E5 (659), G5 (784), C6 (1046)
        val freqs = doubleArrayOf(523.25, 659.25, 783.99, 1046.50)
        val noteSamples = numSamples / 4

        for (i in 0 until numSamples) {
            val noteIndex = (i / noteSamples).coerceAtMost(3)
            val noteT = (i % noteSamples).toDouble() / sampleRate
            val freq = freqs[noteIndex]
            val envelope = exp(-noteT * 12.0)
            val s1 = sin(2.0 * PI * freq * noteT)
            val s2 = sin(2.0 * PI * (freq * 2.0) * noteT) * 0.3
            val sample = ((s1 + s2) * envelope * 0.6 * Short.MAX_VALUE).toInt()
            buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        return buffer
    }

    private fun generateFailSound(): ShortArray {
        val durationMs = 260
        val numSamples = (sampleRate * durationMs) / 1000
        val buffer = ShortArray(numSamples)
        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            val freq = 200.0 - (t * 250.0) // downward descending frequency
            val envelope = exp(-t * 15.0)
            val sample = (sin(2.0 * PI * freq * t) * envelope * 0.5 * Short.MAX_VALUE).toInt()
            buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        return buffer
    }
}
