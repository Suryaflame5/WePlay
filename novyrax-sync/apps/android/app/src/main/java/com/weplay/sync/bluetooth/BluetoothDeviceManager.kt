package com.weplay.sync.bluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
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
    val bluetoothAdapter: BluetoothAdapter? = bluetoothManager?.adapter

    private val _scannedDevices = MutableStateFlow<List<AudioOutputDevice>>(emptyList())
    val scannedDevices: StateFlow<List<AudioOutputDevice>> = _scannedDevices.asStateFlow()

    private val _isScanning = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean> = _isScanning.asStateFlow()

    val isBluetoothEnabled: Boolean
        get() = bluetoothAdapter?.isEnabled == true

    fun hasBluetoothPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.BLUETOOTH_CONNECT
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

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
            _scannedDevices.value = emptyList()
            return
        }

        _isScanning.value = true
        loadBondedAudioDevices()
    }

    fun stopScanning() {
        _isScanning.value = false
    }

    fun loadBondedAudioDevices() {
        if (!isBluetoothEnabled) {
            _scannedDevices.value = emptyList()
            return
        }

        if (!hasBluetoothPermission()) {
            _scannedDevices.value = emptyList()
            return
        }

        try {
            val bonded = bluetoothAdapter?.bondedDevices ?: emptySet()
            val mapped = bonded.map { dev ->
                val devName = try { dev.name } catch (e: SecurityException) { null } ?: "Bluetooth Device"
                val devAddress = dev.address ?: "00:00:00:00:00:00"
                val devClass = try { dev.bluetoothClass?.majorDeviceClass } catch (e: SecurityException) { null }

                AudioOutputDevice(
                    id = "bt_${devAddress}",
                    name = devName,
                    type = inferDeviceType(devClass),
                    transport = TransportType.BLUETOOTH_A2DP,
                    status = ConnectionStatus.CONNECTED,
                    volume = 0.85f,
                    isSelectedForPlayback = false,
                    capability = AudioCapability(
                        supportsVolumeAttenuation = true,
                        supportsLatencyMeasurement = false
                    ),
                    hardwareAddress = devAddress
                )
            }
            _scannedDevices.value = mapped
        } catch (e: SecurityException) {
            _scannedDevices.value = emptyList()
        }
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
