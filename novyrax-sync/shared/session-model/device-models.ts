export type DeviceType =
  | 'HEADPHONES'
  | 'SPEAKER'
  | 'NETWORK_RECEIVER'
  | 'HI_FI_SYSTEM'
  | 'SMART_TV'
  | 'CAR_AUDIO'
  | 'SYSTEM_BUILTIN';

export type TransportType =
  | 'BLUETOOTH_A2DP'
  | 'LE_AUDIO_UNICAST'
  | 'LE_AUDIO_AURACAST'
  | 'WIFI_SYNC'
  | 'WIRED_EXTERNAL'
  | 'BUILTIN_SPEAKER';

export type ConnectionStatus =
  | 'AVAILABLE'
  | 'CONNECTING'
  | 'CONNECTED'
  | 'AUDIO_READY'
  | 'PLAYING'
  | 'SYNC_WARNING'
  | 'RECONNECTING'
  | 'UNSUPPORTED';

export interface AudioCapability {
  supportedSampleRates: number[];
  supportedBitDepths: number[];
  supportsVolumeAttenuation: boolean;
  supportsLatencyMeasurement: boolean;
  supportsIndependentClockSync: boolean;
  supportedCodecs: string[];
}

export interface SyncMetrics {
  clockOffsetMs: number;
  roundTripTimeMs: number;
  jitterMs: number;
  estimatedDriftMs: number;
  packetLossPercentage: number;
  bufferHealth: number;
}

export interface AudioOutputDevice {
  id: string;
  name: string;
  type: DeviceType;
  transport: TransportType;
  status: ConnectionStatus;
  volume: number; // 0 to 1
  isMuted: boolean;
  isSelectedForPlayback: boolean;
  capability: AudioCapability;
  syncMetrics?: SyncMetrics;
  hardwareAddress?: string;
  statusMessage?: string;
}

export interface DeviceGroup {
  id: string;
  name: string;
  deviceIds: string[];
  masterVolume: number;
  isMuted: boolean;
}

export type AudioQualitySetting = 'SOURCE_PRESERVING' | 'HIGH_QUALITY' | 'LOW_BANDWIDTH';
export type SyncModeSetting = 'TIGHT_ALIGNMENT' | 'LOW_LATENCY';

export interface AudioTrack {
  id: string;
  title: string;
  artist: string;
  album: string;
  durationMs: number;
  artworkUrl?: string;
  sampleRate: number;
  bitDepth: number;
  format: 'FLAC' | 'ALAC' | 'WAV' | 'AAC' | 'MP3';
  fileUri?: string;
}
