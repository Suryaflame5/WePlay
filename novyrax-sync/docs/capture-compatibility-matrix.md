# WePlay Audio Capture & Hardware Compatibility Audit

## 1. Executive Summary

Android's `AudioPlaybackCapture` API provides standard, unrooted operating-system-level audio capture of eligible playback streams. However, it is not an unrestricted sound grabber. 

The source application, its manifest configuration, runtime audio attributes, and operating system security policies strictly govern whether audio buffers are populated or silenced with zeros.

---

## 2. Per-Application Compatibility Matrix

| Source Application | Compatibility Status | Technical Requirement & Operating Boundary | Failure / Mute Condition |
| :--- | :--- | :--- | :--- |
| **YouTube (Free / Standard)** | **CONDITIONAL** | Standard video playback uses `USAGE_MEDIA` and respects `allowAudioPlaybackCapture="true"`. | User rental movies, paid purchases, and Widevine DRM video tracks enforce `FLAG_SECURE`, muting the capture buffer. |
| **YouTube Music** | **CONDITIONAL** | Audio streams with standard compression are captured when MediaProjection is active. | Certain offline cached tracks or protected music videos disable third-party playback capture at HAL layer. |
| **Spotify** | **CONDITIONAL** | Standard streaming playback is captured on devices running Android 10+ where Spotify does not enforce `CAPTURE_POLICY_NONE`. | Varies across OEM firmware adaptations; DRM-wrapped offline downloads may be silenced. |
| **Google Chrome / Chromium Browsers** | **CONDITIONAL** | HTML5 `<video>` and `<audio>` tags default to system media routing and allow playback capture. | Web video players utilizing Widevine DRM (e.g. streaming web players) are silenced by Android HAL. |
| **Mobile Games (Unity / Unreal)** | **CONDITIONAL** | Audio mixing using Android `AudioTrack` or standard Oboe/AAudio with `USAGE_GAME` is capturable. | Games using direct exclusive OpenSL ES streams or custom low-latency hardware MMAP bypass the Android audio server capture. |
| **Podcast Apps (Pocket Casts, AntennaPod)** | **SUPPORTED** | Open RSS audio feeds utilize standard Android MediaPlayer or ExoPlayer with `USAGE_MEDIA`. | Very high reliability; no DRM restrictions. |
| **Netflix, Disney+, Amazon Prime Video** | **UNSUPPORTED (BY DESIGN)** | Android OS strictly enforces Widevine L1 encryption and `FLAG_SECURE`. | Audio buffers are zeroed out at the hardware abstraction layer (HAL). Cannot be bypassed by third-party software. |
| **VoIP & Cellular Calls (WhatsApp, Zoom, Phone)** | **UNSUPPORTED (BY DESIGN)** | Android security policy strictly bars `USAGE_VOICE_COMMUNICATION` from `AudioPlaybackCapture`. | Capture throws security exclusion or zeros buffer to prevent surreptitious wiretapping. |

---

## 3. Bluetooth Auracast (LE Audio Broadcast) Technical Audit

### 3.1 Hardware Probe vs. Runtime Implementation
* `BluetoothAdapter.isLeAudioBroadcastSourceSupported()`:
  * **What it does**: Checks whether the device's Bluetooth controller hardware and firmware stack support the Bluetooth 5.2+ Isochronous Broadcast Channel (BIG/BIS).
  * **What it does NOT do**: It does **not** create, configure, or transmit an Auracast broadcast.

### 3.2 Public Android SDK Boundary
In Android 13 (Tiramisu), 14 (UpsideDownCake), 15 (VanillaIceCream), and 16, the APIs required to instantiate an Auracast broadcast stream (`android.bluetooth.BluetoothLeBroadcast`, `BluetoothLeAudioContentMetadata`, `BluetoothLeBroadcastAssistant`) are explicitly annotated with:
```java
@SystemApi
@RequiresPermission(android.permission.MANAGE_BLUETOOTH_LE_BROADCAST_SOURCE)
```
* **Protection Level**: `signature|privileged`.
* **Impact**: Standard third-party applications installed via APK or Google Play **cannot** obtain `MANAGE_BLUETOOTH_LE_BROADCAST_SOURCE`. Only apps signed with the device manufacturer's platform key or placed in `/system/priv-app/` can invoke these methods.

### 3.3 WePlay Architectural Solution
1. **Hardware Capability Detection**: WePlay probes `isLeAudioBroadcastSourceSupported()`.
2. **System Delegation**: For Auracast-capable devices, WePlay prompts and deep-links the user to native Android System Audio Sharing (Settings -> Connected devices -> Audio sharing), which possesses platform signature privileges.
3. **Universal Wi-Fi Distribution**: For all other companion devices (phones, tablets, PCs, smart speakers), WePlay distributes 48kHz LPCM audio over local Wi-Fi with synchronized playout directly to each receiver's Bluetooth headphones.
