package com.weplay.sync.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.weplay.sync.shared.model.AudioOutputDevice

/**
 * OutputManagementScreen maps directly to the Master Multi-Room Control Screen.
 */
@Composable
fun OutputManagementScreen(
    outputs: List<AudioOutputDevice>,
    masterVolume: Float,
    isMasterMuted: Boolean,
    onMasterVolumeChange: (Float) -> Unit,
    onMasterMuteToggle: () -> Unit,
    onDeviceVolumeChange: (String, Float) -> Unit,
    onToggleDevice: (String) -> Unit,
    onScanDevices: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    MultiOutputRoomScreen(
        activeOutputs = outputs,
        masterVolume = masterVolume,
        isMasterMuted = isMasterMuted,
        onMasterVolumeChange = onMasterVolumeChange,
        onMasterMuteToggle = onMasterMuteToggle,
        onDeviceVolumeChange = onDeviceVolumeChange,
        onToggleDevice = onToggleDevice,
        onAddDeviceClick = onScanDevices,
        onClose = onBack,
        modifier = modifier
    )
}
