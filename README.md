# WePlay — Universal Audio Sharing & Distribution Platform

> **One phone. One audio source. Everyone listens.**

WePlay is a modern multi-device audio distribution system built for real-time synchronized listening across Bluetooth LE Audio (Auracast), local Wi-Fi receiver companions, and system audio endpoints. 

Instead of locking users into a single proprietary streaming silo, WePlay transforms your device into an open audio broadcast host for any playback source—YouTube, Spotify, games, podcasts, or local media.

---

## 🎧 Key Capabilities

* **System-Wide Audio Capture**: Leverages Android's `AudioPlaybackCapture` API with user-authorized `MediaProjection` background services to capture non-DRM media from any compatible app.
* **Instant QR & Code Pairing**: Guests scan an on-screen QR code (inspired by Google Pixel Auracast pairing) or enter a 6-character session PIN (`WP-XXXX`) to synchronize with the session.
* **Radial Audio Topology**: Interactive visual hub showing broadcast distribution, connected listener nodes, and active audio energy in real time.
* **Acoustic Latency & Lip-Sync Alignment**: Precision delay controls (+0 ms to +45 ms) for video playback synchronization, eliminating lip-sync lag when watching movies together.
* **Multi-Transport Routing Matrix**:
  * **Auracast / Bluetooth LE Audio Broadcast**: Hardware-level broadcast to unlimited compatible LE Audio earbuds/receivers.
  * **Wi-Fi Companion Streaming**: Ultra-low-latency local network audio distribution to companion phones and speakers running the WePlay Receiver client.
  * **Direct System Output**: Fallback A2DP / Classic Bluetooth and wired outputs with distinct telemetry and capability reporting.

---

## 📐 Architecture & Repository Structure

```
WePlay/
├── novyrax-sync/
│   ├── apps/
│   │   ├── android/             # Android Host Application (Kotlin, Jetpack Compose, Material 3)
│   │   │   ├── app/src/main/    # LiveSessionHost, ShareHome, JoinSession, AudioShareService
│   │   │   └── build.gradle.kts
│   │   └── ios/                 # iOS Client Prototype
│   ├── receiver/                # Companion Wi-Fi Receiver engines (Android / iOS)
│   ├── services/                # Compatibility catalogs, account & diagnostics APIs
│   └── shared/                  # Audio timeline math, session models, protocol definitions
└── apps/
    └── web/                     # Web preview runner & verification test suite
```

---

## 🛠️ Building & Running

### Prerequisites
* **Android SDK**: API level 34+ (Android 14+ recommended for Auracast / AudioPlaybackCapture).
* **JDK**: OpenJDK 17 or 21.
* **Gradle**: 8.2+ (bundled via `gradlew`).

### Build Debug APK
```bash
cd novyrax-sync/apps/android
./gradlew assembleDebug
```
The compiled output is located at:
`novyrax-sync/apps/android/app/build/outputs/apk/debug/app-debug.apk`

### Install to Connected Android Device via ADB
```bash
adb install -r novyrax-sync/apps/android/app/build/outputs/apk/debug/app-debug.apk
adb shell am start -n com.weplay.sync/.MainActivity
```

---

## 🛡️ Audio Capture Compatibility & Privacy

Android's `AudioPlaybackCapture` operates under strict OS privacy boundaries:
* **User Consent**: Explicit `MediaProjection` permission is granted by the user at session start.
* **Supported Apps**: Applications that allow audio capture (e.g. YouTube, Spotify, Chrome, games) with `USAGE_MEDIA`, `USAGE_GAME`, or `USAGE_UNKNOWN`.
* **DRM / Protected Content**: Audio protected under Widevine L1 / `FLAG_SECURE` policies cannot be captured at the OS level and is handled transparently with clear user guidance.

---

## 📄 License
Apache 2.0 / Proprietary (See individual module headers).
