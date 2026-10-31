# Audio Quality Pipeline & Codec Handling

## Pipeline Stages
1. **Source Decoder**:
   - FLAC, WAV, ALAC: Lossless PCM decoding up to 192 kHz / 24-bit.
   - AAC, MP3, Ogg Vorbis: High-efficiency decompression without artificial re-quantization.
2. **Internal Mixer / Resampler**:
   - Internal 32-bit floating-point audio pipeline.
   - Polyphase sinc interpolation when sample-rate conversion is strictly required by the output sink (e.g. 44.1 kHz -> 48 kHz).
   - Dynamic soft-knee limiting to eliminate digital clipping (intersample peaks).
3. **Transport Encoding**:
   - Wi-Fi Receiver: Raw 24-bit/48kHz LPCM or Opus 320 kbps (depending on user quality mode).
   - Bluetooth: LDAC / aptX HD / AAC / SBC as negotiated by system Bluetooth stack.
   - Auracast / LC3: 32 kHz / 48 kHz ISO streams.
