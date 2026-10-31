package com.weplay.sync.audio

import android.content.Context
import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.os.Build
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

    fun refreshSystemRoutes() {
        val devices = mutableListOf<AudioOutputDevice>()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val systemAudioDevices = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS)
            for (dev in systemAudioDevices) {
                val (deviceType, transport) = mapAudioDeviceInfo(dev)
                val isSelected = dev.isSink

                devices.add(
                    AudioOutputDevice(
                        id = "system_${dev.id}",
                        name = dev.productName.toString().ifEmpty { "Audio Output ${dev.id}" },
                        type = deviceType,
                        transport = transport,
                        status = if (isSelected) ConnectionStatus.AUDIO_READY else ConnectionStatus.AVAILABLE,
                        volume = 0.8f,
                        isSelectedForPlayback = isSelected
                    )
                )
            }
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
