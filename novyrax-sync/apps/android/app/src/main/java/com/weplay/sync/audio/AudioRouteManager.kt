package com.weplay.sync.audio

import android.content.Context
import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.os.Build
import com.weplay.sync.shared.model.AudioCapability
import com.weplay.sync.shared.model.AudioOutputDevice
import com.weplay.sync.shared.model.ConnectionStatus
import com.weplay.sync.shared.model.DeviceType
import com.weplay.sync.shared.model.TransportType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AudioRouteManager(
    private val context: Context
) {
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    private val _availableRoutes = MutableStateFlow<List<AudioOutputDevice>>(emptyList())
    val availableRoutes: StateFlow<List<AudioOutputDevice>> = _availableRoutes.asStateFlow()

    fun getSystemVolumeFraction(): Float {
        val max = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        if (max <= 0) return 0.75f
        val current = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        return (current.toFloat() / max.toFloat()).coerceIn(0f, 1f)
    }

    fun setSystemVolumeFraction(fraction: Float) {
        val max = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        val target = (fraction.coerceIn(0f, 1f) * max).toInt()
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, target, 0)
    }

    fun setSystemMuted(muted: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            audioManager.adjustStreamVolume(
                AudioManager.STREAM_MUSIC,
                if (muted) AudioManager.ADJUST_MUTE else AudioManager.ADJUST_UNMUTE,
                0
            )
        }
    }

    fun refreshSystemRoutes() {
        val devices = mutableListOf<AudioOutputDevice>()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val systemAudioDevices = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS)

            // Determine if BT or wired headset is connected
            val hasBluetooth = systemAudioDevices.any { it.type == AudioDeviceInfo.TYPE_BLUETOOTH_A2DP }
            val hasWired = systemAudioDevices.any {
                it.type == AudioDeviceInfo.TYPE_WIRED_HEADPHONES || it.type == AudioDeviceInfo.TYPE_WIRED_HEADSET
            }

            for (dev in systemAudioDevices) {
                // Filter out non-media output sinks (e.g. telephony earpiece)
                if (dev.type == AudioDeviceInfo.TYPE_BUILTIN_EARPIECE ||
                    dev.type == AudioDeviceInfo.TYPE_TELEPHONY) {
                    continue
                }

                val (deviceType, transport) = mapAudioDeviceInfo(dev)

                // Accurate active selection check
                val isSelected = when (dev.type) {
                    AudioDeviceInfo.TYPE_BLUETOOTH_A2DP -> hasBluetooth
                    AudioDeviceInfo.TYPE_WIRED_HEADPHONES,
                    AudioDeviceInfo.TYPE_WIRED_HEADSET -> !hasBluetooth && hasWired
                    AudioDeviceInfo.TYPE_BUILTIN_SPEAKER -> !hasBluetooth && !hasWired
                    else -> false
                }

                val deviceName = if (dev.productName.isNullOrBlank()) {
                    when (dev.type) {
                        AudioDeviceInfo.TYPE_BUILTIN_SPEAKER -> "Phone Speaker (${Build.MODEL})"
                        AudioDeviceInfo.TYPE_BLUETOOTH_A2DP -> "Bluetooth Audio Device"
                        AudioDeviceInfo.TYPE_WIRED_HEADPHONES,
                        AudioDeviceInfo.TYPE_WIRED_HEADSET -> "Wired Headphones"
                        else -> "Audio Output ${dev.id}"
                    }
                } else {
                    dev.productName.toString()
                }

                devices.add(
                    AudioOutputDevice(
                        id = "system_${dev.id}",
                        name = deviceName,
                        type = deviceType,
                        transport = transport,
                        status = if (isSelected) ConnectionStatus.PLAYING else ConnectionStatus.AUDIO_READY,
                        volume = getSystemVolumeFraction(),
                        isSelectedForPlayback = isSelected,
                        capability = AudioCapability(
                            supportsVolumeAttenuation = true,
                            supportsLatencyMeasurement = false
                        )
                    )
                )
            }
        }

        // If no devices returned, fallback to built-in speaker
        if (devices.isEmpty()) {
            devices.add(
                AudioOutputDevice(
                    id = "system_speaker",
                    name = "Phone Speaker (${Build.MODEL})",
                    type = DeviceType.SYSTEM_BUILTIN,
                    transport = TransportType.BUILTIN_SPEAKER,
                    status = ConnectionStatus.PLAYING,
                    volume = getSystemVolumeFraction(),
                    isSelectedForPlayback = true
                )
            )
        }

        _availableRoutes.value = devices
    }

    private fun mapAudioDeviceInfo(info: AudioDeviceInfo): Pair<DeviceType, TransportType> {
        return when (info.type) {
            AudioDeviceInfo.TYPE_BLUETOOTH_A2DP -> Pair(DeviceType.HEADPHONES, TransportType.BLUETOOTH_A2DP)
            AudioDeviceInfo.TYPE_WIRED_HEADPHONES,
            AudioDeviceInfo.TYPE_WIRED_HEADSET -> Pair(DeviceType.HEADPHONES, TransportType.WIRED_EXTERNAL)
            AudioDeviceInfo.TYPE_USB_DEVICE,
            AudioDeviceInfo.TYPE_USB_HEADSET -> Pair(DeviceType.HI_FI_SYSTEM, TransportType.WIRED_EXTERNAL)
            AudioDeviceInfo.TYPE_BUILTIN_SPEAKER -> Pair(DeviceType.SYSTEM_BUILTIN, TransportType.BUILTIN_SPEAKER)
            else -> Pair(DeviceType.SPEAKER, TransportType.BLUETOOTH_A2DP)
        }
    }
}
