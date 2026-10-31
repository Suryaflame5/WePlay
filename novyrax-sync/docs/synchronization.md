# Multi-Device Synchronization & Latency Calibration

## Clock Synchronization Protocol
1. **Time Base**: Nanosecond monotonic clock (`System.nanoTime()` / `mach_absolute_time()`).
2. **NTP/PTP-Inspired Clock Offset Estimation**:
   - Master sends `T1` (departure timestamp).
   - Receiver logs `T2` (arrival) and sends `T3` (departure).
   - Master logs `T4` (arrival).
   - Offset $\theta = \frac{(T2 - T1) + (T3 - T4)}{2}$
   - Delay $\delta = (T4 - T1) - (T3 - T2)$
   - Exponential moving average filter with outlier rejection.
3. **Playout Scheduling**:
   - Master attaches target presentation timestamp $P_t = T_{now} + \Delta_{buffer}$ to audio chunk frames.
   - Each receiver schedules frame presentation to its local hardware sink at $P_t - \theta$.
4. **Drift Compensation**:
   - Micro-resampling / dynamic sample stuffing or dropping across zero-crossings (inaudible fractional pitch shift) when drift drifts beyond $\pm 2$ milliseconds.
5. **Bluetooth Realism**:
   - Standard Bluetooth A2DP latency varies from 100ms to 250ms depending on buffer size and codec. WePlay exposes detected route latency via `AudioTrack.getPlaybackHeadPosition()` and `AudioDeviceInfo.getEncapsulationModes()` where supported.
   - The UI never fakes "0 ms sync" for standard Bluetooth headphones.
