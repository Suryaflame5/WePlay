package com.weplay.sync

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.weplay.sync.audio.AudioPlaybackEngine
import com.weplay.sync.audio.AudioReceiverEngine
import com.weplay.sync.audio.AudioRouteManager
import com.weplay.sync.audio.AudioShareService
import com.weplay.sync.audio.ReceiverState
import com.weplay.sync.bluetooth.BluetoothDeviceManager
import com.weplay.sync.data.DeviceGroupManager
import com.weplay.sync.shared.model.AudioOutputDevice
import com.weplay.sync.shared.model.AudioSharingSession
import com.weplay.sync.shared.model.CaptureSourceType
import com.weplay.sync.shared.model.CaptureStatus
import com.weplay.sync.shared.model.ConnectionStatus
import com.weplay.sync.shared.model.DeviceGroup
import com.weplay.sync.shared.model.DeviceType
import com.weplay.sync.shared.model.NearbyStream
import com.weplay.sync.shared.model.SharingTransport
import com.weplay.sync.shared.model.TransportType
import com.weplay.sync.sync.SyncCoordinator
import com.weplay.sync.ui.screens.AppScreen
import com.weplay.sync.ui.screens.AudioSyncSettingsScreen
import com.weplay.sync.ui.screens.DeviceDiscoveryScreen
import com.weplay.sync.ui.screens.HomeScreen
import com.weplay.sync.ui.screens.JoinSessionScreen
import com.weplay.sync.ui.screens.LibraryAndGroupsScreen
import com.weplay.sync.ui.screens.LibraryTrackItem
import com.weplay.sync.ui.screens.LiveSessionHostScreen
import com.weplay.sync.ui.screens.MultiOutputRoomScreen
import com.weplay.sync.ui.screens.NowPlayingScreen
import com.weplay.sync.ui.screens.OnboardingScreen
import com.weplay.sync.ui.screens.OutputManagementScreen
import com.weplay.sync.ui.screens.PlayerTrackState
import com.weplay.sync.ui.screens.SettingsCompatibilityScreen
import com.weplay.sync.ui.screens.ShareHomeScreen
import com.weplay.sync.ui.screens.SyncDiagnostics
import com.weplay.sync.ui.screens.WePlaySplashScreen
import com.weplay.sync.ui.theme.WePlayTheme
import kotlinx.coroutines.flow.MutableStateFlow

class MainActivity : ComponentActivity() {

    private lateinit var playbackEngine: AudioPlaybackEngine
    private lateinit var routeManager: AudioRouteManager
    private lateinit var bluetoothManager: BluetoothDeviceManager
    private lateinit var syncCoordinator: SyncCoordinator
    private lateinit var groupManager: DeviceGroupManager
    private lateinit var receiverEngine: AudioReceiverEngine

    private var audioShareService: AudioShareService? = null
    private var isServiceBound = false
    private val serviceSessionFlow = MutableStateFlow<AudioSharingSession?>(null)
    private val requestedScreenFlow = MutableStateFlow<AppScreen?>(null)

    // User callback after projection permission granted
    private var onProjectionApproved: (() -> Unit)? = null

    private val projectionLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            audioShareService?.startSharingWithResult(result.resultCode, result.data!!)
            onProjectionApproved?.invoke()
            onProjectionApproved = null
        } else {
            Toast.makeText(this, "Audio capture permission denied. WePlay cannot broadcast audio.", Toast.LENGTH_SHORT).show()
        }
    }

    private val bluetoothPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val connectGranted = permissions[android.Manifest.permission.BLUETOOTH_CONNECT] ?: true
        if (connectGranted) {
            bluetoothManager.loadBondedAudioDevices()
            routeManager.refreshSystemRoutes()
        }
    }

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as AudioShareService.LocalBinder
            audioShareService = binder.getService()
            isServiceBound = true
            serviceSessionFlow.value = audioShareService?.session?.value
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            audioShareService = null
            isServiceBound = false
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleScreenIntent(intent)
    }

    private fun handleScreenIntent(intent: Intent?) {
        intent?.getStringExtra("screen")?.let { name ->
            runCatching { AppScreen.valueOf(name.uppercase()) }.getOrNull()?.let {
                requestedScreenFlow.value = it
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        handleScreenIntent(intent)

        // Initialize core engines
        playbackEngine = AudioPlaybackEngine(this)
        routeManager = AudioRouteManager(this)
        bluetoothManager = BluetoothDeviceManager(this)
        syncCoordinator = SyncCoordinator()
        groupManager = DeviceGroupManager()
        receiverEngine = AudioReceiverEngine(this)

        playbackEngine.initializeEngine()
        routeManager.refreshSystemRoutes()
        bluetoothManager.loadBondedAudioDevices()

        // Bind AudioShareService
        val serviceIntent = Intent(this, AudioShareService::class.java)
        startService(serviceIntent)
        bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE)

        setContent {
            WePlayTheme {
                val liveSessionState = audioShareService?.session?.collectAsState()?.value
                    ?: serviceSessionFlow.collectAsState().value

                WePlayRootNavigator(
                    playbackEngine = playbackEngine,
                    routeManager = routeManager,
                    bluetoothManager = bluetoothManager,
                    syncCoordinator = syncCoordinator,
                    groupManager = groupManager,
                    receiverEngine = receiverEngine,
                    liveHostSession = liveSessionState,
                    requestedScreenFlow = requestedScreenFlow,
                    onRequestMediaProjection = { onApproved ->
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            onProjectionApproved = onApproved
                            val mpManager = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
                            projectionLauncher.launch(mpManager.createScreenCaptureIntent())
                        } else {
                            audioShareService?.startSharing(null)
                            onApproved()
                        }
                    },
                    onRequestBluetoothPermission = {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            bluetoothPermissionLauncher.launch(
                                arrayOf(
                                    android.Manifest.permission.BLUETOOTH_CONNECT,
                                    android.Manifest.permission.BLUETOOTH_SCAN
                                )
                            )
                        }
                    },
                    onOpenBluetoothSettings = {
                        try {
                            startActivity(Intent(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS))
                        } catch (e: Exception) {
                            startActivity(Intent(android.provider.Settings.ACTION_SETTINGS))
                        }
                    },
                    onToggleMute = { audioShareService?.toggleMute() },
                    onStopSharing = { audioShareService?.stopSharing() },
                    onRemoveListener = { id -> audioShareService?.session?.value?.listeners?.find { it.id == id } }
                )
            }
        }
    }

    override fun onDestroy() {
        if (isServiceBound) {
            unbindService(serviceConnection)
            isServiceBound = false
        }
        receiverEngine.disconnect()
        playbackEngine.release()
        super.onDestroy()
    }
}

@Composable
fun WePlayRootNavigator(
    playbackEngine: AudioPlaybackEngine,
    routeManager: AudioRouteManager,
    bluetoothManager: BluetoothDeviceManager,
    syncCoordinator: SyncCoordinator,
    groupManager: DeviceGroupManager,
    receiverEngine: AudioReceiverEngine,
    liveHostSession: AudioSharingSession?,
    requestedScreenFlow: MutableStateFlow<AppScreen?>,
    onRequestMediaProjection: (onApproved: () -> Unit) -> Unit,
    onRequestBluetoothPermission: () -> Unit,
    onOpenBluetoothSettings: () -> Unit,
    onToggleMute: () -> Unit,
    onStopSharing: () -> Unit,
    onRemoveListener: (String) -> Unit
) {
    val requestedByIntent by requestedScreenFlow.collectAsState()
    var currentScreen by remember { mutableStateOf<AppScreen>(requestedByIntent ?: AppScreen.SPLASH) }
    var previousScreen by remember { mutableStateOf<AppScreen>(AppScreen.SHARE_HOME) }
    val context = androidx.compose.ui.platform.LocalContext.current
    val prefs = remember { context.getSharedPreferences("weplay_prefs", android.content.Context.MODE_PRIVATE) }
    var hasCompletedOnboarding by remember {
        mutableStateOf(prefs.getBoolean("has_completed_onboarding", false))
    }

    LaunchedEffect(requestedByIntent) {
        requestedByIntent?.let {
            currentScreen = it
        }
    }

    // Live session state from service
    val activeSession = liveHostSession ?: AudioSharingSession(
        sessionCode = "WP-4829",
        sessionName = "My Device Audio",
        isHost = true,
        sourceType = CaptureSourceType.DEVICE_MEDIA_PLAYBACK,
        status = CaptureStatus.READY_TO_START,
        transport = SharingTransport.WIFI_RECEIVER,
        listeners = emptyList(),
        bufferDepthMs = 30,
        sampleRate = 48000,
        bitDepth = 16,
        rttMs = 0.0
    )

    // Listener Connection State
    val receiverTelemetry by receiverEngine.telemetry.collectAsState()
    val isConnectedAsListener = receiverTelemetry.state == ReceiverState.STREAMING || receiverTelemetry.state == ReceiverState.HANDSHAKE_ACCEPTED
    var connectedHostName by remember { mutableStateOf<String?>("WePlay Audio Host") }
    var connectedHostCode by remember { mutableStateOf<String?>("WP-4829") }
    val nearbyStreams = remember { emptyList<NearbyStream>() }

    // System audio output routes & real Bluetooth devices
    val availableRoutes by routeManager.availableRoutes.collectAsState()
    val scannedBtDevices by bluetoothManager.scannedDevices.collectAsState()

    val devicesState = remember { mutableStateOf<List<AudioOutputDevice>>(emptyList()) }
    LaunchedEffect(availableRoutes, scannedBtDevices) {
        val list = mutableListOf<AudioOutputDevice>()
        list.addAll(availableRoutes)
        for (bt in scannedBtDevices) {
            if (list.none { it.hardwareAddress == bt.hardwareAddress || it.name == bt.name }) {
                list.add(bt)
            }
        }
        devicesState.value = list.map { dev ->
            val prev = devicesState.value.find { it.id == dev.id }
            if (prev != null) dev.copy(isSelectedForPlayback = prev.isSelectedForPlayback, volume = prev.volume) else dev
        }
    }
    var devices by devicesState

    // Real system volume
    var masterVolume by remember { mutableFloatStateOf(routeManager.getSystemVolumeFraction()) }
    var isMasterMuted by remember { mutableStateOf(false) }

    // Real Audio Playback Tracking
    val isEnginePlaying by playbackEngine.isPlaying.collectAsState()
    val enginePositionMs by playbackEngine.currentPositionMs.collectAsState()

    var trackState by remember {
        mutableStateOf(
            PlayerTrackState(
                title = "Midnight Horizons",
                artist = "Ambient Atlas",
                album = "Charcoal",
                durationMs = 204000L,
                currentPositionMs = 0L,
                isPlaying = false,
                format = "FLAC",
                sampleRate = 48000,
                bitDepth = 24
            )
        )
    }

    LaunchedEffect(isEnginePlaying, enginePositionMs) {
        trackState = trackState.copy(
            isPlaying = isEnginePlaying,
            currentPositionMs = enginePositionMs
        )
    }

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
                    prefs.edit().putBoolean("has_completed_onboarding", true).apply()
                    hasCompletedOnboarding = true
                    currentScreen = AppScreen.SHARE_HOME
                }
            )
        }

        AppScreen.SHARE_HOME -> {
            ShareHomeScreen(
                activeSession = activeSession,
                onStartSession = {
                    onRequestMediaProjection {
                        previousScreen = AppScreen.SHARE_HOME
                        currentScreen = AppScreen.HOST_SESSION
                    }
                },
                onJoinSession = {
                    previousScreen = AppScreen.SHARE_HOME
                    currentScreen = AppScreen.JOIN_SESSION
                },
                onOpenLibrary = {
                    previousScreen = AppScreen.SHARE_HOME
                    currentScreen = AppScreen.LIBRARY
                },
                onOpenRooms = {
                    previousScreen = AppScreen.SHARE_HOME
                    currentScreen = AppScreen.MULTI_OUTPUT_ROOM
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
                onToggleMute = onToggleMute,
                onStopSharing = {
                    onStopSharing()
                    currentScreen = AppScreen.SHARE_HOME
                },
                onRemoveListener = onRemoveListener,
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
                    val hostIp = if (code.contains(".")) code.substringBefore(":") else "127.0.0.1"
                    connectedHostName = "Host ($code)"
                    receiverEngine.connect(hostIp, code)
                },
                onConnectToStream = { stream ->
                    connectedHostCode = "WP-4829"
                    connectedHostName = stream.hostName
                    receiverEngine.connect("127.0.0.1", "WP-4829")
                },
                onLeaveSession = {
                    receiverEngine.disconnect()
                },
                onBack = { currentScreen = AppScreen.SHARE_HOME }
            )
        }

        AppScreen.OUTPUT_MANAGEMENT -> {
            OutputManagementScreen(
                outputs = devices,
                masterVolume = masterVolume,
                isMasterMuted = isMasterMuted,
                onMasterVolumeChange = { v ->
                    masterVolume = v
                    routeManager.setSystemVolumeFraction(v)
                },
                onMasterMuteToggle = {
                    isMasterMuted = !isMasterMuted
                    routeManager.setSystemMuted(isMasterMuted)
                },
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
                onScanDevices = {
                    currentScreen = AppScreen.DEVICE_DISCOVERY
                },
                onBack = { currentScreen = previousScreen }
            )
        }

        AppScreen.DIAGNOSTICS -> {
            val qualitySetting by playbackEngine.qualitySetting.collectAsState()
            val syncMode by syncCoordinator.syncMode.collectAsState()

            val isHost = activeSession.status == CaptureStatus.CAPTURING_ACTIVE
            val measuredRtt = if (isHost) activeSession.rttMs else receiverTelemetry.rttMs
            val measuredJitter = if (isHost) 0.8 else receiverTelemetry.jitterMs
            val droppedCount = if (isHost) 0L else receiverTelemetry.packetsDropped

            AudioSyncSettingsScreen(
                currentQuality = qualitySetting,
                currentSyncMode = syncMode,
                diagnostics = SyncDiagnostics(
                    measuredRttMs = measuredRtt,
                    clockOffsetMs = 0.0,
                    maxDriftMs = 0.0,
                    droppedFramesCount = droppedCount,
                    underrunEvents = 0,
                    activeTransportSummary = if (isHost) "Host: LPCM 48kHz Stereo @ UDP 48200" else "Listener: ${receiverTelemetry.state.name}"
                ),
                onQualityChange = { playbackEngine.setQuality(it) },
                onSyncModeChange = { syncCoordinator.setSyncMode(it) },
                onCalibrateClocks = { },
                onBack = { currentScreen = previousScreen }
            )
        }

        AppScreen.SETTINGS -> {
            SettingsCompatibilityScreen(
                onOpenDiagnostics = {
                    previousScreen = AppScreen.SETTINGS
                    currentScreen = AppScreen.DIAGNOSTICS
                },
                onBack = { currentScreen = AppScreen.SHARE_HOME },
                onNavigateToTab = { currentScreen = it }
            )
        }

        AppScreen.MUSIC_HOME -> {
            HomeScreen(
                trackState = trackState,
                activeDevices = devices,
                isPlaying = isEnginePlaying,
                onTrackSelect = { title, artist, album ->
                    trackState = trackState.copy(title = title, artist = artist, album = album)
                    playbackEngine.play()
                },
                onPlayPauseToggle = {
                    if (isEnginePlaying) {
                        playbackEngine.pause()
                    } else {
                        playbackEngine.play()
                    }
                },
                onOpenPlayer = { currentScreen = AppScreen.NOW_PLAYING },
                onOpenAudioRoom = { currentScreen = AppScreen.MULTI_OUTPUT_ROOM },
                onOpenLibrary = { currentScreen = AppScreen.LIBRARY },
                onOpenSettings = { currentScreen = AppScreen.SETTINGS }
            )
        }

        AppScreen.NOW_PLAYING -> {
            NowPlayingScreen(
                trackState = trackState,
                activeDevices = devices,
                onPlayPauseToggle = {
                    if (isEnginePlaying) {
                        playbackEngine.pause()
                    } else {
                        playbackEngine.play()
                    }
                },
                onSeek = { targetMs ->
                    playbackEngine.seekTo(targetMs)
                },
                onNextTrack = { },
                onPreviousTrack = { },
                onOpenOutputSelector = { currentScreen = AppScreen.DEVICE_DISCOVERY },
                onOpenQueue = { currentScreen = AppScreen.LIBRARY },
                onBack = { currentScreen = previousScreen }
            )
        }

        AppScreen.DEVICE_DISCOVERY -> {
            var isScanning by remember { mutableStateOf(false) }

            LaunchedEffect(Unit) {
                bluetoothManager.loadBondedAudioDevices()
                routeManager.refreshSystemRoutes()
            }

            DeviceDiscoveryScreen(
                devices = devices,
                isBluetoothEnabled = bluetoothManager.isBluetoothEnabled,
                hasBluetoothPermission = bluetoothManager.hasBluetoothPermission(),
                isScanning = isScanning,
                onStartScan = {
                    isScanning = true
                    bluetoothManager.startScanning()
                    routeManager.refreshSystemRoutes()
                },
                onStopScan = {
                    isScanning = false
                    bluetoothManager.stopScanning()
                },
                onToggleSelect = { id ->
                    devices = devices.map {
                        if (it.id == id) {
                            it.copy(isSelectedForPlayback = !it.isSelectedForPlayback)
                        } else {
                            it
                        }
                    }
                },
                onVolumeChange = { id, v ->
                    devices = devices.map { if (it.id == id) it.copy(volume = v) else it }
                },
                onOpenBluetoothSettings = onOpenBluetoothSettings,
                onRequestPermission = onRequestBluetoothPermission,
                onBack = { currentScreen = previousScreen }
            )
        }

        AppScreen.MULTI_OUTPUT_ROOM -> {
            MultiOutputRoomScreen(
                activeOutputs = devices,
                listeners = activeSession.listeners,
                masterVolume = masterVolume,
                isMasterMuted = isMasterMuted,
                onMasterVolumeChange = { v ->
                    masterVolume = v
                    routeManager.setSystemVolumeFraction(v)
                },
                onMasterMuteToggle = {
                    isMasterMuted = !isMasterMuted
                    routeManager.setSystemMuted(isMasterMuted)
                },
                onDeviceVolumeChange = { id, v ->
                    devices = devices.map { if (it.id == id) it.copy(volume = v) else it }
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
                onAddDeviceClick = { currentScreen = AppScreen.DEVICE_DISCOVERY },
                onClose = { currentScreen = previousScreen },
                onNavigateToTab = { currentScreen = it }
            )
        }

        AppScreen.LIBRARY -> {
            val libraryTracks = remember {
                listOf(
                    LibraryTrackItem("1", "Midnight Horizons", "Ambient Atlas", "Atlas", "3:24", "FLAC 24-bit / 48k"),
                    LibraryTrackItem("2", "To Speak Of Solitude", "Brambles", "Charcoal", "4:21", "FLAC 24-bit / 48k"),
                    LibraryTrackItem("3", "Unsayable", "Brambles", "Charcoal", "2:52", "FLAC 24-bit / 48k"),
                    LibraryTrackItem("4", "Ambient Flow", "Atlas", "Ambient", "3:40", "FLAC 24-bit / 48k"),
                    LibraryTrackItem("5", "Deep Pulse", "Nova", "Focus", "4:12", "FLAC 24-bit / 48k")
                )
            }
            val groups = remember {
                listOf(
                    DeviceGroup(
                        id = "grp_all",
                        name = "Whole House Audio",
                        deviceIds = listOf("system_speaker"),
                        masterVolume = 0.8f,
                        isMuted = false
                    )
                )
            }
            LibraryAndGroupsScreen(
                libraryTracks = libraryTracks,
                savedGroups = groups,
                onTrackSelect = { item ->
                    trackState = trackState.copy(title = item.title, artist = item.artist, album = item.album)
                    playbackEngine.play()
                    currentScreen = AppScreen.NOW_PLAYING
                },
                onApplyGroup = { },
                onSaveCurrentGroup = { },
                onBack = { currentScreen = previousScreen },
                onNavigateToTab = { currentScreen = it }
            )
        }
    }
}
