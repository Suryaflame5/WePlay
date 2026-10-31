# WePlay Low-Latency Audio Distribution Protocol (WADP) Specification v1.0

## 1. Overview & Transport Selection
The WePlay Audio Distribution Protocol delivers real-time synchronized audio from an Android host capturing system audio to multiple companion listener devices on local networks.

| Parameter | Specification | Engineering Rationale |
| :--- | :--- | :--- |
| **Active Codec** | **LPCM (Linear Pulse Code Modulation)** | Zero algorithmic encoding latency, lossless bit-perfection, deterministic DSP pipeline. |
| **Transport Layer** | **UDP (Unicast / Directed Subnet Broadcast)** | Head-of-line blocking elimination over lossy 2.4GHz / 5GHz Wi-Fi. TCP control channel for handshakes. |
| **Sample Rate** | **48,000 Hz (48 kHz)** | Matches standard Android HAL mixer frequency, avoiding hardware resampling distortion. |
| **Channel Count** | **2 (Stereo - Left & Right interleaved)** | Full spatial stereo fidelity. |
| **Bit Depth** | **16-bit signed integer (PCM_16BIT)** | Standard Android `AudioFormat.ENCODING_PCM_16BIT` format (Little-Endian samples over big-endian packet framing). |
| **Packet Duration** | **5.0 milliseconds (5 ms)** | Exactly 240 stereo frames (960 bytes audio payload). |
| **Bitrate** | **1,536 kbps (1.536 Mbps)** | $48,000 \text{ frames/sec} \times 2 \text{ channels} \times 2 \text{ bytes/sample} \times 8 = 1,536,000 \text{ bps}$. |
| **Total Packet Size** | **985 bytes** | 25-byte header + 960-byte PCM payload. Safely below 1472-byte UDP MTU threshold (zero IP fragmentation). |

---

## 2. Packet Binary Wire Framing

All multi-byte numerical fields in the protocol header are transmitted in **Network Byte Order (Big-Endian)**.

### 2.1 Audio Payload Chunk (Type `0x04`)
```
 0                   1                   2                   3
 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
|       Magic: 'W' 'P'          | Packet Type (4)|              |
+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+               +
|                  Session Auth Token (32 bits)                 |
+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
|                                                               |
+                 Chunk Sequence Number (64 bits)               +
|                                                               |
+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
|                                                               |
+             Presentation Timestamp (PTS - 64 bits)            +
|                                                               |
+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
|       Payload Size (16 bits)  |   Raw LPCM Stereo Payload...  |
+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+                               |
|                     (960 bytes PCM data)                      |
|                                                               |
+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
```

* **Magic (16 bits)**: `0x5750` (`'W'`, `'P'`). Drops garbage network traffic immediately.
* **Packet Type (8 bits)**: `0x04` (`AUDIO_PAYLOAD_CHUNK`).
* **Session Auth Token (32 bits)**: Unpredictable session credential established during handshake. Protects against packet injection or cross-talk from different sessions on the same subnet.
* **Chunk Sequence Number (64 bits)**: Monotonically increasing counter per 5ms slice. Used by receiver jitter buffer for loss calculation and frame reordering.
* **Presentation Timestamp (PTS - 64 bits)**: Nanosecond timeline timestamp (`System.nanoTime()`) representing capture origin time.
* **Payload Size (16 bits)**: Length of PCM audio bytes (`0x03C0` = 960 bytes).

---

## 3. Session Security & Handshake Negotiation

Local network audio streams are not exposed in cleartext to unauthorized listeners.

1. **Host Generation**:
   * Generates a 6-character human-readable code: `WP-XXXX` (e.g. `WP-7429`).
   * Derives a cryptographic 32-bit session token using `SecureRandom`.
2. **Handshake Request (`0x07`)**:
   * Listener initiates UDP connection sending `HandshakeRequestPacket` containing `sessionCode` and listener device name (`Build.MODEL`).
3. **Handshake Validation & Accept (`0x08`)**:
   * Host validates `sessionCode`. If accepted, host registers listener's IP address and UDP port in `activeListeners` map and replies with `HandshakeAcceptPacket` delivering the 32-bit `sessionToken` and negotiated parameters (48kHz, stereo, 16-bit, 30ms target buffer).
4. **Heartbeat & Telemetry Tracking**:
   * Listener sends periodic `TimeSyncPacket` every 2 seconds.
   * Host tracks `lastHeartbeatNs`. Listeners that fail to reply within 8 seconds are pruned automatically.

---

## 4. Jitter Buffering, Lip-Sync & Playout

* **Jitter Buffer Architecture**:
  * Concurrent skip-list queue storing frames keyed by `chunkSequence`.
  * Depth: Default 30ms (6 chunks).
  * Late packet handling: Packets arriving with PTS older than current playback playhead are discarded.
  * Gap concealment: If a packet is lost in transit, the playout loop injects interpolated silence (zero-padding) rather than stalling `AudioTrack`, avoiding audible clicks.
* **Lip-Sync Video Alignment Queue**:
  * Optional user-configurable offset (`0ms`, `15ms`, `30ms`, `45ms`) implemented inside receiver jitter buffer before writing to `AudioTrack`.
  * Compares display rendering latency of host video against local Bluetooth A2DP latency.

---

## 5. Audio Playout to Bluetooth Endpoints

On the receiver phone, decoded PCM frames are written to an active Android `AudioTrack`:
* **Attributes**: `USAGE_MEDIA`, `CONTENT_TYPE_MUSIC`.
* **Transfer Mode**: `MODE_STREAM`.
* **Hardware Routing**: Android OS automatically directs `AudioTrack` output to the listener's connected Bluetooth headphones (e.g. Sony WH-1000XM5, Galaxy Buds, Pixel Buds Pro) or 3.5mm/USB-C audio interface.
