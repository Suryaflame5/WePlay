package com.weplay.sync.bluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.content.Context
import android.os.Build
import com.weplay.sync.shared.model.AudioCapability
import com.weplay.sync.shared.model.AudioOutputDevice
import com.weplay.sync.shared.model.ConnectionStatus
import com.weplay.sync.shared.model.DeviceType
import com.weplay.sync.shared.model.TransportType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class BluetoothDeviceManager(
    private val context: Context
) {
    private val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager
    private val bluetoothAdapter: BluetoothAdapter? = bluetoothManager?.adapter

    private val _scannedDevices = MutableStateFlow<List<AudioOutputDevice>>(emptyList())
    val scannedDevices: StateFlow<List<AudioOutputDevice>> = _scannedDevices.asStateFlow()

    private val _isScanning = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean> = _isScanning.asStateFlow()

    fun checkLeAudioSupport(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            bluetoothAdapter?.isLeAudioSupported == BluetoothStatusCodes.FEATURE_SUPPORTED
        } else {
            false
        }
    }

    fun checkBroadcastSupport(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            bluetoothAdapter?.isLeAudioBroadcastSourceSupported == BluetoothStatusCodes.FEATURE_SUPPORTED
        } else {
            false
        }
    }

    fun startScanning() {
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled) {
            _isScanning.value = false
            return
        }

        _isScanning.value = true
        loadBondedAudioDevices()
    }

    fun stopScanning() {
        _isScanning.value = false
    }

    private fun loadBondedAudioDevices() {
        val bonded = bluetoothAdapter?.bondedDevices ?: emptySet()
        val mapped = bonded.map { dev ->
            AudioOutputDevice(
                id = "bt_${dev.address}",
                name = dev.name ?: "Bluetooth Device",
                type = inferDeviceType(dev.bluetoothClass?.majorDeviceClass),
                transport = TransportType.BLUETOOTH_A2DP,
                status = ConnectionStatus.CONNECTED,
                volume = 0.8f,
                isSelectedForPlayback = false,
                capability = AudioCapability(
                    supportsVolumeAttenuation = true,
                    supportsLatencyMeasurement = false
                ),
                hardwareAddress = dev.address
            )
        }
        _scannedDevices.value = mapped
    }

    private fun inferDeviceType(majorClass: Int?): DeviceType {
        return when (majorClass) {
            android.bluetooth.BluetoothClass.Device.Major.AUDIO_VIDEO -> DeviceType.HEADPHONES
            else -> DeviceType.SPEAKER
        }
    }

    object BluetoothStatusCodes {
        const val FEATURE_SUPPORTED = 10
    }
}
