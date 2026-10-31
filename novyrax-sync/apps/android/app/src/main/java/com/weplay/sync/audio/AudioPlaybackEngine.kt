package com.weplay.sync.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import com.weplay.sync.shared.model.AudioQualitySetting
import com.weplay.sync.shared.timeline.AudioTimeline
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AudioPlaybackEngine(
    private val context: Context
) {
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private val timeline = AudioTimeline(referenceSampleRate = 48000)

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentPositionMs = MutableStateFlow(0L)
    val currentPositionMs: StateFlow<Long> = _currentPositionMs.asStateFlow()

    private val _qualitySetting = MutableStateFlow(AudioQualitySetting.SOURCE_PRESERVING)
    val qualitySetting: StateFlow<AudioQualitySetting> = _qualitySetting.asStateFlow()

    private var audioTrack: AudioTrack? = null

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
            .setBufferSizeInBytes(minBufferSize * 2)
            .setTransferMode(AudioTrack.MODE_STREAM)
            .build()
    }

    fun play() {
        timeline.start()
        audioTrack?.play()
        _isPlaying.value = true
    }

    fun pause() {
        timeline.pause()
        audioTrack?.pause()
        _isPlaying.value = false
    }

    fun seekTo(positionMs: Long) {
        val targetFrame = timeline.msToFrame(positionMs)
        timeline.seekToFrame(targetFrame)
        _currentPositionMs.value = positionMs
    }

    fun setQuality(setting: AudioQualitySetting) {
        _qualitySetting.value = setting
    }

    fun release() {
        audioTrack?.release()
        audioTrack = null
        _isPlaying.value = false
    }
}
