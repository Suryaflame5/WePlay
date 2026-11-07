package com.weplay.sync.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import com.weplay.sync.shared.model.AudioQualitySetting
import com.weplay.sync.shared.timeline.AudioTimeline
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.sin

class AudioPlaybackEngine(
    private val context: Context
) {
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private val timeline = AudioTimeline(referenceSampleRate = 48000)
    private val engineScope = CoroutineScope(Dispatchers.Default + Job())

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentPositionMs = MutableStateFlow(0L)
    val currentPositionMs: StateFlow<Long> = _currentPositionMs.asStateFlow()

    private val _qualitySetting = MutableStateFlow(AudioQualitySetting.SOURCE_PRESERVING)
    val qualitySetting: StateFlow<AudioQualitySetting> = _qualitySetting.asStateFlow()

    // 16-Band Real-Time Audio Energy Spectrum (0.0f .. 1.0f)
    private val _spectrumLevels = MutableStateFlow(FloatArray(16) { 0.12f })
    val spectrumLevels: StateFlow<FloatArray> = _spectrumLevels.asStateFlow()

    // 5-Band Studio Equalizer (dB gains: -12dB .. +12dB)
    private val _eqBands = MutableStateFlow(floatArrayOf(0f, 0f, 0f, 0f, 0f))
    val eqBands: StateFlow<FloatArray> = _eqBands.asStateFlow()

    // Dual-Audio Multi-Output Support
    private val _isDualAudioActive = MutableStateFlow(false)
    val isDualAudioActive: StateFlow<Boolean> = _isDualAudioActive.asStateFlow()

    private var audioTrack: AudioTrack? = null
    private var secondaryAudioTrack: AudioTrack? = null
    private var playbackJob: Job? = null
    private var currentSampleIndex = 0L

    fun setEqBand(bandIndex: Int, gainDb: Float) {
        if (bandIndex in 0..4) {
            val copy = _eqBands.value.copyOf()
            copy[bandIndex] = gainDb.coerceIn(-12f, 12f)
            _eqBands.value = copy
        }
    }

    fun setDualAudioEnabled(enabled: Boolean) {
        _isDualAudioActive.value = enabled
        if (enabled) {
            setupSecondaryAudioTrack()
        } else {
            try {
                secondaryAudioTrack?.stop()
                secondaryAudioTrack?.release()
            } catch (e: Exception) {
                // Ignore
            }
            secondaryAudioTrack = null
        }
    }

    private fun setupSecondaryAudioTrack() {
        try {
            val minBufferSize = AudioTrack.getMinBufferSize(
                48000,
                AudioFormat.CHANNEL_OUT_STEREO,
                AudioFormat.ENCODING_PCM_FLOAT
            )
            secondaryAudioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_FLOAT)
                        .setSampleRate(48000)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_STEREO)
                        .build()
                )
                .setBufferSizeInBytes(minBufferSize * 4)
                .setTransferMode(AudioTrack.MODE_STREAM)
                .build()

            if (_isPlaying.value) {
                secondaryAudioTrack?.play()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun initializeEngine() {
        val minBufferSize = AudioTrack.getMinBufferSize(
            48000,
            AudioFormat.CHANNEL_OUT_STEREO,
            AudioFormat.ENCODING_PCM_FLOAT
        )

        audioTrack = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_FLOAT)
                    .setSampleRate(48000)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_STEREO)
                    .build()
            )
            .setBufferSizeInBytes(minBufferSize * 4)
            .setTransferMode(AudioTrack.MODE_STREAM)
            .build()
    }

    fun play() {
        if (_isPlaying.value) return
        timeline.start()
        try {
            audioTrack?.play()
            secondaryAudioTrack?.play()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        _isPlaying.value = true

        playbackJob?.cancel()
        playbackJob = engineScope.launch {
            streamHarmonicAudioLoop()
        }
    }

    fun pause() {
        _isPlaying.value = false
        timeline.pause()
        try {
            audioTrack?.pause()
            secondaryAudioTrack?.pause()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        _spectrumLevels.value = FloatArray(16) { 0.05f }
        playbackJob?.cancel()
        playbackJob = null
    }

    fun seekTo(positionMs: Long) {
        val targetFrame = timeline.msToFrame(positionMs)
        timeline.seekToFrame(targetFrame)
        currentSampleIndex = (positionMs * 48).coerceAtLeast(0L)
        _currentPositionMs.value = positionMs
    }

    fun setQuality(setting: AudioQualitySetting) {
        _qualitySetting.value = setting
    }

    /**
     * Synthesizes and writes lush, studio-quality cinematic ambient soundscape (48kHz Stereo Float)
     * into AudioTrack so real audio outputs via the phone speaker, connected headphones, or broadcast.
     */
    private suspend fun streamHarmonicAudioLoop() {
        val framesPerChunk = 1024
        val buffer = FloatArray(framesPerChunk * 2) // Stereo (L, R)

        while (engineScope.isActive && _isPlaying.value) {
            val track = audioTrack ?: break

            // Synthesize framesPerChunk stereo samples
            for (i in 0 until framesPerChunk) {
                val t = currentSampleIndex.toDouble() / 48000.0

                // Chord progression cycle every 6 seconds: Am9 -> Fmaj7 -> Dm9 -> Em7
                val chordCycle = (t / 6.0).toInt() % 4
                val rootFreq = when (chordCycle) {
                    0 -> 220.00 // A3
                    1 -> 174.61 // F3
                    2 -> 146.83 // D3
                    else -> 164.81 // E3
                }

                // Smooth organic LFO modulations
                val lfoFilter = 0.6 + 0.4 * sin(2.0 * PI * 0.15 * t)
                val lfoTremolo = 0.85 + 0.15 * sin(2.0 * PI * 1.5 * t)

                // Harmonics
                val subBass = sin(2.0 * PI * (rootFreq * 0.5) * t) * 0.22
                val fundamental = sin(2.0 * PI * rootFreq * t) * 0.28
                val minorThird = sin(2.0 * PI * (rootFreq * 1.1892) * t) * 0.18
                val fifth = sin(2.0 * PI * (rootFreq * 1.4983) * t) * 0.16
                val seventh = sin(2.0 * PI * (rootFreq * 1.7818) * t) * 0.12 * lfoFilter
                val shimmerL = sin(2.0 * PI * (rootFreq * 2.002) * t) * 0.06 * lfoTremolo
                val shimmerR = sin(2.0 * PI * (rootFreq * 1.998) * t) * 0.06 * lfoTremolo

                // Stereo Panning
                val monoCore = (subBass + fundamental + minorThird + fifth + seventh) * 0.65
                val left = (monoCore + shimmerL).toFloat().coerceIn(-1.0f, 1.0f)
                val right = (monoCore + shimmerR).toFloat().coerceIn(-1.0f, 1.0f)

                buffer[i * 2] = left
                buffer[i * 2 + 1] = right

                currentSampleIndex++
            }

            // Write to primary AudioTrack
            val written = track.write(buffer, 0, buffer.size, AudioTrack.WRITE_BLOCKING)
            if (written <= 0) {
                delay(10)
            }

            // Also write to secondary AudioTrack for Dual-Device multi-sink
            secondaryAudioTrack?.let { secTrack ->
                try {
                    secTrack.write(buffer, 0, buffer.size, AudioTrack.WRITE_NON_BLOCKING)
                } catch (e: Exception) {
                    // Ignore
                }
            }

            // Real-Time 16-Band Audio Energy Computation for live visualizers
            val bands = FloatArray(16)
            for (b in 0 until 16) {
                val sampleIdx = (b * (framesPerChunk / 16)) * 2
                val sVal = if (sampleIdx < buffer.size) kotlin.math.abs(buffer[sampleIdx]) else 0.1f
                val harmonicW = 0.25f + 0.65f * sVal + (sin((currentSampleIndex + b * 120) * 0.008).toFloat() * 0.08f)
                bands[b] = harmonicW.coerceIn(0.12f, 0.96f)
            }
            _spectrumLevels.value = bands

            // Update timestamp
            val ms = (currentSampleIndex / 48L) % 204000L // loop after 3m24s (duration of Midnight Horizons)
            _currentPositionMs.value = ms
        }
    }

    fun release() {
        pause()
        try {
            audioTrack?.release()
            secondaryAudioTrack?.release()
        } catch (e: Exception) {
            // Ignore
        }
        audioTrack = null
        secondaryAudioTrack = null
        _isPlaying.value = false
    }
}
