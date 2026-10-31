# Bluetooth & Audio Routing Capabilities Matrix

## Real Platform Constraints
1. **Classic Bluetooth (A2DP)**
   - Android standard system audio routes to exactly one primary A2DP sink by default.
   - Dual Audio (Samsung / specific Qualcomm flagships) uses vendor-proprietary Bluetooth HAL extensions; ordinary public Android APIs cannot forcefully clone A2DP without system permissions or vendor SDKs.
   - Bluetooth discovery (`BluetoothDevice.ACTION_FOUND` or `BluetoothLeScanner`) detects that a device is paired or in RF range. **It does not imply that an audio sink is active or connected**.
   - Volume control: A2DP supports Absolute Volume via AVRCP. If disabled, device volume is decoupled from system volume.

2. **LE Audio & Auracast**
   - Requires Android 13+ (API 33+) with `BluetoothLeBroadcast` and `BluetoothLeAudio` hardware support.
   - WePlay queries `BluetoothManager.getProfileProxy()` for `BluetoothProfile.LE_AUDIO` and `BluetoothProfile.LE_AUDIO_BROADCAST`.
   - If not supported by chipset firmware, the UI explicitly flags the route as `Unsupported: Device or firmware lacks LE Audio broadcast HAL`.

3. **Synchronized Wi-Fi Protocol (WePlay Companion)**
   - Runs UDP multicast/unicast with SNTP-based clock synchronization.
   - Enables multi-room stereo/mono output to any device running the WePlay Receiver engine.
   - Measures round-trip time (RTT), jitter, and packet loss continuously to dynamically calibrate buffer delay (15ms–80ms).

## Device States
| State | Definition | UI Treatment |
|---|---|---|
| `AVAILABLE` | Discovered via BLE/Classic or mDNS | Neutral gray border, "Available" |
| `CONNECTING` | Protocol handshake or socket initialization | Pulsing subtle teal dot, "Connecting..." |
| `CONNECTED` | Transport link established, audio pipeline pending | Solid gray/teal border, "Connected" |
| `AUDIO_READY` | Sink negotiated, audio buffers primed | Solid teal indicator, "Audio Ready" |
| `PLAYING` | Active audio frames rendering to sink | Filled teal dot + active transport glyph, "Playing" |
| `SYNC_WARNING` | Clock drift > 15ms target or packet loss > 2% | Muted amber glyph, "+24ms drift" |
| `RECONNECTING` | Temporary RF/network drop recovery | Subtle dashed amber border, "Reconnecting..." |
| `UNSUPPORTED` | Capability not present in hardware/OS | Muted disabled row with explanatory message |
