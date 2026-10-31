package com.weplay.sync.audio

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioPlaybackCaptureConfiguration
import android.media.AudioRecord
import android.media.projection.MediaProjection
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.weplay.sync.MainActivity
import com.weplay.sync.shared.model.AudioSharingSession
import com.weplay.sync.shared.model.CaptureSourceType
import com.weplay.sync.shared.model.CaptureStatus
import com.weplay.sync.shared.model.ConnectedListener
import com.weplay.sync.shared.model.DeviceType
import com.weplay.sync.shared.model.SharingTransport
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class AudioShareService : Service() {

    private val binder = LocalBinder()
    private val serviceScope = CoroutineScope(Dispatchers.Default + Job())

    private var audioRecord: AudioRecord? = null
    private var isRecording = false

    private val _session = MutableStateFlow(
        AudioSharingSession(
            sessionCode = "WP-4829",
            sessionName = "My Device Audio",
            isHost = true,
            sourceType = CaptureSourceType.DEVICE_MEDIA_PLAYBACK,
            status = CaptureStatus.READY_TO_START,
            transport = SharingTransport.WIFI_RECEIVER,
            listeners = listOf(
                ConnectedListener("l1", "Robin's Pixel Buds Pro", DeviceType.HEADPHONES, SharingTransport.WIFI_RECEIVER, 0.98f, 1.4, 11.2),
                ConnectedListener("l2", "Maya's WH-1000XM5", DeviceType.HEADPHONES, SharingTransport.BLUETOOTH_AURACAST, 0.92f, 2.1, 14.5),
                ConnectedListener("l3", "Living Room Speaker", DeviceType.SPEAKER, SharingTransport.WIFI_RECEIVER, 0.88f, 3.8, 18.0)
            )
        )
    )
    val session: StateFlow<AudioSharingSession> = _session.asStateFlow()

    inner class LocalBinder : Binder() {
        fun getService(): AudioShareService = this@AudioShareService
    }

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    fun startSharing(mediaProjection: MediaProjection? = null) {
        val notification = buildForegroundNotification("Sharing device audio with listeners...")
        startForeground(NOTIFICATION_ID, notification)

        if (mediaProjection != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            setupPlaybackCapture(mediaProjection)
        } else {
            // Simulated / local buffer distribution loop
            startDistributionLoop()
        }

        _session.value = _session.value.copy(
            status = CaptureStatus.CAPTURING_ACTIVE
        )
    }

    private fun setupPlaybackCapture(mediaProjection: MediaProjection) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) return

        try {
            val config = AudioPlaybackCaptureConfiguration.Builder(mediaProjection)
                .addMatchingUsage(AudioAttributes.USAGE_MEDIA)
                .addMatchingUsage(AudioAttributes.USAGE_GAME)
                .addMatchingUsage(AudioAttributes.USAGE_UNKNOWN)
                .build()

            val sampleRate = 48000
            val channelConfig = AudioFormat.CHANNEL_IN_STEREO
            val audioFormat = AudioFormat.ENCODING_PCM_16BIT
            val minBufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)

            audioRecord = AudioRecord.Builder()
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(audioFormat)
                        .setSampleRate(sampleRate)
                        .setChannelMask(channelConfig)
                        .build()
                )
                .setAudioPlaybackCaptureConfig(config)
                .setBufferSizeInBytes(minBufferSize * 2)
                .build()

            audioRecord?.startRecording()
            isRecording = true

            serviceScope.launch {
                val buffer = ByteArray(minBufferSize)
                while (isActive && isRecording) {
                    val read = audioRecord?.read(buffer, 0, buffer.size) ?: 0
                    if (read > 0) {
                        // Forward captured PCM frame to connected listeners over UDP/Socket
                    }
                }
            }
        } catch (e: Exception) {
            startDistributionLoop()
        }
    }

    private fun startDistributionLoop() {
        serviceScope.launch {
            while (isActive) {
                delay(20)
                // Real-time packet telemetry ticks
            }
        }
    }

    fun stopSharing() {
        isRecording = false
        audioRecord?.stop()
        audioRecord?.release()
        audioRecord = null

        _session.value = _session.value.copy(
            status = CaptureStatus.STOPPED
        )
        stopForeground(STOP_FOREGROUND_REMOVE)
    }

    fun toggleMute() {
        val current = _session.value
        val newStatus = if (current.status == CaptureStatus.MUTED) CaptureStatus.CAPTURING_ACTIVE else CaptureStatus.MUTED
        _session.value = current.copy(status = newStatus)
    }

    fun addListener(name: String, deviceType: DeviceType, transport: SharingTransport) {
        val newListener = ConnectedListener(
            id = "listener_${System.currentTimeMillis()}",
            name = name,
            deviceType = deviceType,
            transport = transport,
            signalQuality = 0.95f,
            clockOffsetMs = (10..35).random() / 10.0,
            rttMs = (100..180).random() / 10.0
        )
        _session.value = _session.value.copy(
            listeners = _session.value.listeners + newListener
        )
    }

    fun removeListener(id: String) {
        _session.value = _session.value.copy(
            listeners = _session.value.listeners.filterNot { it.id == id }
        )
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "WePlay Audio Sharing",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Notifies when WePlay is sharing audio in the background"
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun buildForegroundNotification(content: String): Notification {
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("WePlay • On Air")
            .setContentText(content)
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }

    companion object {
        private const val NOTIFICATION_ID = 4091
        private const val CHANNEL_ID = "weplay_audio_share_channel"
    }
}
