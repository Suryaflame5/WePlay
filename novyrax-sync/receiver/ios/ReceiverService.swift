import Foundation
import AVFoundation
import Network

public final class WePlayIOSReceiver {
    private var listener: NWListener?
    private var audioEngine = AVAudioEngine()
    private var playerNode = AVAudioPlayerNode()

    public init() {}

    public func startListening(port: UInt16 = 48200) {
        do {
            let params = NWParameters.udp
            listener = try NWListener(using: params, on: NWEndpoint.Port(rawValue: port)!)
            listener?.newConnectionHandler = { [weak self] connection in
                connection.start(queue: .global())
                self?.receivePacket(on: connection)
            }
            listener?.start(queue: .global())
        } catch {
            print("Receiver listener failed: \(error)")
        }
    }

    private func receivePacket(on connection: NWConnection) {
        connection.receiveMessage { [weak self] content, _, _, error in
            if let data = content, !data.isEmpty {
                self?.handleIncomingData(data)
            }
            if error == nil {
                self?.receivePacket(on: connection)
            }
        }
    }

    private func handleIncomingData(_ data: Data) {
        // Dispatches time sync packets and schedules audio chunks to AVAudioEngine timeline
    }

    public func stop() {
        listener?.cancel()
        audioEngine.stop()
    }
}
