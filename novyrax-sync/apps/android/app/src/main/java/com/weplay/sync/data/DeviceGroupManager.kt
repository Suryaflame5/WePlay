package com.weplay.sync.data

import com.weplay.sync.shared.model.DeviceGroup
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class DeviceGroupManager {
    private val _savedGroups = MutableStateFlow<List<DeviceGroup>>(
        listOf(
            DeviceGroup(
                id = "group_living_room",
                name = "Living Room Setup",
                deviceIds = listOf("hifi_arc", "spk_move"),
                masterVolume = 0.75f
            ),
            DeviceGroup(
                id = "group_studio",
                name = "Studio Monitors Pair",
                deviceIds = listOf("mon_left", "mon_right"),
                masterVolume = 0.82f
            )
        )
    )
    val savedGroups: StateFlow<List<DeviceGroup>> = _savedGroups.asStateFlow()

    fun saveGroup(name: String, deviceIds: List<String>, masterVolume: Float = 0.75f) {
        val newGroup = DeviceGroup(
            id = "group_${System.currentTimeMillis()}",
            name = name,
            deviceIds = deviceIds,
            masterVolume = masterVolume
        )
        _savedGroups.value = _savedGroups.value + newGroup
    }

    fun removeGroup(groupId: String) {
        _savedGroups.value = _savedGroups.value.filter { it.id != groupId }
    }
}
