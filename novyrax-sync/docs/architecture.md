# WePlay (Novyrax Sync) Architecture & Engineering Specification

## 1. Executive Product Architecture
WePlay is a high-fidelity, multi-device audio routing and playback platform designed for Android and iOS. Unlike generic single-device media players, WePlay coordinates playback across heterogeneous audio sinks:
1. **Classic Bluetooth (A2DP / AudioTrack / System Route)**: Standard stereo routing governed by the host operating system.
2. **Bluetooth LE Audio & Auracast**: Broadcast audio streams to compatible hearing devices, earbuds, and Auracast assistant receivers when supported by the hardware and Android 13+ / iOS 17+ HAL.
3. **Synchronized Wi-Fi Protocol**: Low-latency PCM/Opus streaming over local UDP/multicast to companion WePlay Receiver instances (running on secondary phones, tablets, or Raspberry Pi/desktop endpoints).
4. **Wired / External DACs (USB-C / Lightning / 3.5mm)**: Bit-perfect pass-through.

---

## 2. Visual Design System ("Hi-Fi Precision")
Inspired by Braun, Teenage Engineering, and Nagra precision acoustics:
- **Light Canvas**: `#F7F6F0` (Warm Off-White)
- **Dark Canvas**: `#121415` (Charcoal Slate)
- **Primary Text**: `#171B1D` (Graphite) / `#ECEEEA` (Off-White Dark)
- **Accent**: `#21CDB8` (Precision Cyan/Teal)
- **Muted**: `#858B89` (Neutral Gray)
- **Separators**: `#E4E3DC` (Warm Hairline) / `#282C30` (Dark Hairline)
- **Typography**: Clean neo-grotesque sans-serif with tabular figures (`tnum`) for timecodes, decibels, and drift milliseconds.
- **Design Principles**:
  - Zero generic AI patterns: No glowing gradients, no 3D floating bubbles, no card overload, no decorative fake equalizers.
  - Truthful state representation: Audio route != Bluetooth connection != Active playback.
  - Per-device volume sliders only when supported by the underlying transport.

---

## 3. WePlay Scalable Vector Identity & Splash Choreography
The WePlay mark combines:
1. **Play Wedge**: Directional triangle indicating source initiation.
2. **Waveform Array**: Five measured vertical bars representing dynamic acoustic energy.
3. **Continuous Orbit & 3 Output Nodes**: Continuous 22° tilted ellipse with 3 circular nodes representing "one source reaching multiple devices in sync".

### Motion Timeline (750–950 ms total):
- **0–150 ms**: Restrained background fade.
- **100–350 ms**: 5 waveform bars scale upward with staggered cubic-bezier ease.
- **250–500 ms**: Play wedge reveals via precise path stroke and scale.
- **400–700 ms**: Orbit line sweeps around mark; 3 output nodes illuminate sequentially (Node 1 @ 480ms, Node 2 @ 570ms, Node 3 @ 660ms).
- **600–800 ms**: "WePlay" wordmark slides upward 4dp with subtle alpha ramp.
- **700–880 ms**: Tagline "One source. In sync." settles.
- **900 ms**: Seamless crossfade to real interactive first screen.
- **Accessibility**: When `prefers-reduced-motion` is active, animations are bypassed in favor of a clean 150ms opacity reveal.
