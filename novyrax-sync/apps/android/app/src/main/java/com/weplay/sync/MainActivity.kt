package com.weplay.sync

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.weplay.sync.audio.AudioPlaybackEngine
import com.weplay.sync.audio.AudioRouteManager
import com.weplay.sync.bluetooth.BluetoothDeviceManager
import com.weplay.sync.data.DeviceGroupManager
import com.weplay.sync.shared.model.AudioCapability
import com.weplay.sync.shared.model.AudioOutputDevice
import com.weplay.sync.shared.model.AudioSharingSession
import com.weplay.sync.shared.model.CaptureSourceType
import com.weplay.sync.shared.model.CaptureStatus
import com.weplay.sync.shared.model.ConnectedListener
import com.weplay.sync.shared.model.ConnectionStatus
import com.weplay.sync.shared.model.DeviceType
import com.weplay.sync.shared.model.NearbyStream
import com.weplay.sync.shared.model.SharingTransport
import com.weplay.sync.shared.model.SyncMetrics
import com.weplay.sync.shared.model.TransportType
import com.weplay.sync.sync.SyncCoordinator
import com.weplay.sync.ui.screens.AppScreen
import com.weplay.sync.ui.screens.AudioSyncSettingsScreen
import com.weplay.sync.ui.screens.DeviceDiscoveryScreen
import com.weplay.sync.ui.screens.JoinSessionScreen
import com.weplay.sync.ui.screens.LiveSessionHostScreen
import com.weplay.sync.ui.screens.OnboardingScreen
import com.weplay.sync.ui.screens.OutputManagementScreen
import com.weplay.sync.ui.screens.SettingsCompatibilityScreen
import com.weplay.sync.ui.screens.ShareHomeScreen
import com.weplay.sync.ui.screens.SyncDiagnostics
import com.weplay.sync.ui.screens.WePlaySplashScreen
import com.weplay.sync.ui.theme.WePlayTheme

class MainActivity : ComponentActivity() {

    private lateinit var playbackEngine: AudioPlaybackEngine
    private lateinit var routeManager: AudioRouteManager
    private lateinit var bluetoothManager: BluetoothDeviceManager
    private lateinit var syncCoordinator: SyncCoordinator
    private lateinit var groupManager: DeviceGroupManager

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        // Initialize audio and sync subsystems
        playbackEngine = AudioPlaybackEngine(this)
        routeManager = AudioRouteManager(this)
        bluetoothManager = BluetoothDeviceManager(this)
        syncCoordinator = SyncCoordinator()
        groupManager = DeviceGroupManager()

        playbackEngine.initializeEngine()
        routeManager.refreshSystemRoutes()

        setContent {
            WePlayTheme {
                WePlayRootNavigator(
                    playbackEngine = playbackEngine,
                    routeManager = routeManager,
                    bluetoothManager = bluetoothManager,
                    syncCoordinator = syncCoordinator,
                    groupManager = groupManager
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        playbackEngine.release()
    }
}

@Composable
fun WePlayRootNavigator(
    playbackEngine: AudioPlaybackEngine,
    routeManager: AudioRouteManager,
    bluetoothManager: BluetoothDeviceManager,
    syncCoordinator: SyncCoordinator,
    groupManager: DeviceGroupManager
) {
    var currentScreen by remember { mutableStateOf<AppScreen>(AppScreen.SPLASH) }
    var previousScreen by remember { mutableStateOf<AppScreen>(AppScreen.SHARE_HOME) }
    var hasCompletedOnboarding by remember { mutableStateOf(false) }

    // Active Audio Sharing Session State
    var activeSession by remember {
        mutableStateOf(
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
                ),
                bufferDepthMs = 30,
                sampleRate = 48000,
                bitDepth = 16,
                rttMs = 14.2
            )
        )
    }

    // Listener Connection State
    var isConnectedAsListener by remember { mutableStateOf(false) }
    var connectedHostName by remember { mutableStateOf<String?>("Alex's Pixel 10") }
    var connectedHostCode by remember { mutableStateOf<String?>("WP-4829") }

    // Nearby Discovered Streams
    val nearbyStreams = remember {
        listOf(
            NearbyStream("s1", "Alex's Pixel 10", "Pixel 10 2134 Audio", SharingTransport.BLUETOOTH_AURACAST, 2, false, -52),
            NearbyStream("s2", "Robin's MacBook", "Robin's Audio Share (WP-4829)", SharingTransport.WIFI_RECEIVER, 3, true, -61),
            NearbyStream("s3", "Studio Auracast Hub", "Live Stage Broadcast", SharingTransport.BLUETOOTH_AURACAST, 8, false, -68)
        )
    }

    // Devices & routing state
    var devices by remember {
        mutableStateOf(
            listOf(
                AudioOutputDevice(
                    id = "dev_arc_living",
                    name = "Living Room Arc",
                    type = DeviceType.HI_FI_SYSTEM,
                    transport = TransportType.WIFI_SYNC,
                    status = ConnectionStatus.PLAYING,
                    volume = 0.75f,
                    isSelectedForPlayback = true,
                    capability = AudioCapability(
                        supportedSampleRates = listOf(48000),
                        supportsVolumeAttenuation = true,
                        supportsLatencyMeasurement = true
                    ),
                    syncMetrics = SyncMetrics(
                        clockOffsetMs = -1.2,
                        roundTripTimeMs = 14.2,
                        jitterMs = 0.8,
                        estimatedDriftMs = 2.4,
                        packetLossPercentage = 0.0,
                        bufferHealth = 1.0
                    )
                ),
                AudioOutputDevice(
                    id = "dev_move_kitchen",
                    name = "Kitchen Move",
                    type = DeviceType.SPEAKER,
                    transport = TransportType.WIFI_SYNC,
                    status = ConnectionStatus.PLAYING,
                    volume = 0.50f,
                    isSelectedForPlayback = true,
                    capability = AudioCapability(
                        supportedSampleRates = listOf(48000),
                        supportsVolumeAttenuation = true,
                        supportsLatencyMeasurement = true
                    ),
                    syncMetrics = SyncMetrics(
                        clockOffsetMs = 3.4,
                        roundTripTimeMs = 18.0,
                        jitterMs = 1.1,
                        estimatedDriftMs = 4.1,
                        packetLossPercentage = 0.0,
                        bufferHealth = 0.98
                    )
                ),
                AudioOutputDevice(
                    id = "dev_sony_xm5",
                    name = "WH-1000XM5",
                    type = DeviceType.HEADPHONES,
                    transport = TransportType.BLUETOOTH_A2DP,
                    status = ConnectionStatus.CONNECTED,
                    volume = 0.85f,
                    isSelectedForPlayback = false,
                    capability = AudioCapability(
                        supportedSampleRates = listOf(44100, 48000),
                        supportsVolumeAttenuation = true,
                        supportsLatencyMeasurement = false
                    ),
                    syncMetrics = SyncMetrics(
                        clockOffsetMs = 0.0,
                        roundTripTimeMs = 45.0,
                        jitterMs = 4.2,
                        estimatedDriftMs = 0.0,
                        packetLossPercentage = 0.0,
                        bufferHealth = 1.0
                    )
                ),
                AudioOutputDevice(
                    id = "dev_car_audio",
                    name = "Legacy Car Audio",
                    type = DeviceType.CAR_AUDIO,
                    transport = TransportType.BLUETOOTH_A2DP,
                    status = ConnectionStatus.UNSUPPORTED,
                    volume = 0.0f,
                    isSelectedForPlayback = false,
                    statusMessage = "Unsupported: Device does not support multi-stream or independent sync clock."
                )
            )
        )
    }

    var masterVolume by remember { mutableFloatStateOf(0.75f) }
    var isMasterMuted by remember { mutableStateOf(false) }

    when (currentScreen) {
        AppScreen.SPLASH -> {
            WePlaySplashScreen(
                onAnimationComplete = {
                    currentScreen = if (hasCompletedOnboarding) AppScreen.SHARE_HOME else AppScreen.ONBOARDING
                }
            )
        }

        AppScreen.ONBOARDING -> {
            OnboardingScreen(
                onComplete = { quality, sync ->
                    playbackEngine.setQuality(quality)
                    syncCoordinator.setSyncMode(sync)
                    hasCompletedOnboarding = true
                    currentScreen = AppScreen.SHARE_HOME
                }
            )
        }

        AppScreen.SHARE_HOME -> {
            ShareHomeScreen(
                activeSession = activeSession,
                onStartSession = {
                    activeSession = activeSession.copy(status = CaptureStatus.CAPTURING_ACTIVE)
                    previousScreen = AppScreen.SHARE_HOME
                    currentScreen = AppScreen.HOST_SESSION
                },
                onJoinSession = {
                    previousScreen = AppScreen.SHARE_HOME
                    currentScreen = AppScreen.JOIN_SESSION
                },
                onOpenDevices = {
                    previousScreen = AppScreen.SHARE_HOME
                    currentScreen = AppScreen.OUTPUT_MANAGEMENT
                },
                onOpenSettings = {
                    previousScreen = AppScreen.SHARE_HOME
                    currentScreen = AppScreen.SETTINGS
                },
                onOpenLiveSession = {
                    previousScreen = AppScreen.SHARE_HOME
                    currentScreen = AppScreen.HOST_SESSION
                }
            )
        }

        AppScreen.HOST_SESSION -> {
            LiveSessionHostScreen(
                session = activeSession,
                onToggleMute = {
                    val newStatus = if (activeSession.status == CaptureStatus.MUTED) CaptureStatus.CAPTURING_ACTIVE else CaptureStatus.MUTED
                    activeSession = activeSession.copy(status = newStatus)
                },
                onStopSharing = {
                    activeSession = activeSession.copy(status = CaptureStatus.STOPPED)
                    currentScreen = AppScreen.SHARE_HOME
                },
                onRemoveListener = { id ->
                    activeSession = activeSession.copy(
                        listeners = activeSession.listeners.filterNot { it.id == id }
                    )
                },
                onOpenDiagnostics = {
                    previousScreen = AppScreen.HOST_SESSION
                    currentScreen = AppScreen.DIAGNOSTICS
                },
                onBack = { currentScreen = AppScreen.SHARE_HOME }
            )
        }

        AppScreen.JOIN_SESSION -> {
            JoinSessionScreen(
                isConnectedToHost = isConnectedAsListener,
                connectedHostName = connectedHostName,
                connectedSessionCode = connectedHostCode,
                nearbyStreams = nearbyStreams,
                onConnectByCode = { code ->
                    connectedHostCode = code
                    connectedHostName = "Host (${code})"
                    isConnectedAsListener = true
                },
                onConnectToStream = { stream ->
                    connectedHostCode = "WP-4829"
                    connectedHostName = stream.hostName
                    isConnectedAsListener = true
                },
                onLeaveSession = {
                    isConnectedAsListener = false
                },
                onBack = { currentScreen = AppScreen.SHARE_HOME }
            )
        }

        AppScreen.OUTPUT_MANAGEMENT -> {
            OutputManagementScreen(
                outputs = devices,
                masterVolume = masterVolume,
                isMasterMuted = isMasterMuted,
                onMasterVolumeChange = { v -> masterVolume = v },
                onMasterMuteToggle = { isMasterMuted = !isMasterMuted },
                onDeviceVolumeChange = { id, vol ->
                    devices = devices.map { if (it.id == id) it.copy(volume = vol) else it }
                },
                onToggleDevice = { id ->
                    devices = devices.map {
                        if (it.id == id) {
                            val newSelected = !it.isSelectedForPlayback
                            it.copy(
                                isSelectedForPlayback = newSelected,
                                status = if (newSelected) ConnectionStatus.PLAYING else ConnectionStatus.CONNECTED
                            )
                        } else it
                    }
                },
                onScanDevices = { currentScreen = AppScreen.OUTPUT_MANAGEMENT },
                onBack = { currentScreen = previousScreen }
            )
        }

        AppScreen.DIAGNOSTICS -> {
            val qualitySetting by playbackEngine.qualitySetting.collectAsState()
            val syncMode by syncCoordinator.syncMode.collectAsState()

            AudioSyncSettingsScreen(
                currentQuality = qualitySetting,
                currentSyncMode = syncMode,
                diagnostics = SyncDiagnostics(
                    measuredRttMs = 14.2,
                    clockOffsetMs = -1.2,
                    maxDriftMs = 3.8,
                    droppedFramesCount = 0,
                    underrunEvents = 0,
                    activeTransportSummary = "WePlay Wi-Fi (LPCM 48kHz) + Auracast (LC3)"
                ),
                onQualityChange = { playbackEngine.setQuality(it) },
                onSyncModeChange = { syncCoordinator.setSyncMode(it) },
                onCalibrateClocks = { /* trigger clock sync packet exchange */ },
                onBack = { currentScreen = previousScreen }
            )
        }

        AppScreen.SETTINGS -> {
            SettingsCompatibilityScreen(
                onOpenDiagnostics = {
                    previousScreen = AppScreen.SETTINGS
                    currentScreen = AppScreen.DIAGNOSTICS
                },
                onBack = { currentScreen = AppScreen.SHARE_HOME }
            )
        }
    }
}
