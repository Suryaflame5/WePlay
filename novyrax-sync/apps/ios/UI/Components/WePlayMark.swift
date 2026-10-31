import SwiftUI

public struct WePlayMarkView: View {
    public var size: CGFloat = 48
    public var playTint: Color = WePlayColors.textPrimaryLight
    public var accentTint: Color = WePlayColors.accentTeal
    public var waveformProgress: CGFloat = 1.0
    public var playProgress: CGFloat = 1.0
    public var orbitProgress: CGFloat = 1.0
    public var nodesIlluminated: Int = 3

    public init(
        size: CGFloat = 48,
        playTint: Color = WePlayColors.textPrimaryLight,
        accentTint: Color = WePlayColors.accentTeal,
        waveformProgress: CGFloat = 1.0,
        playProgress: CGFloat = 1.0,
        orbitProgress: CGFloat = 1.0,
        nodesIlluminated: Int = 3
    ) {
        self.size = size
        self.playTint = playTint
        self.accentTint = accentTint
        self.waveformProgress = waveformProgress
        self.playProgress = playProgress
        self.orbitProgress = orbitProgress
        self.nodesIlluminated = nodesIlluminated
    }

    public var body: some View {
        Canvas { context, canvasSize in
            let w = canvasSize.width
            let h = canvasSize.height
            let s = w / 48.0

            // 1. Continuous Orbit Ellipse (tilted -22 degrees)
            if orbitProgress > 0 {
                var orbitContext = context
                orbitContext.translateBy(x: w * 0.5, y: h * 0.5)
                orbitContext.rotate(by: Angle(degrees: -22))

                let rx = 21.0 * s
                let ry = 7.5 * s
                var path = Path()
                let steps = 60
                let maxSteps = Int(CGFloat(steps) * min(max(orbitProgress, 0), 1))

                for i in 0...maxSteps {
                    let theta = (Double(i) / Double(steps)) * 2 * .pi
                    let px = rx * cos(theta)
                    let py = ry * sin(theta)
                    if i == 0 {
                        path.move(to: CGPoint(x: px, y: py))
                    } else {
                        path.addLine(to: CGPoint(x: px, y: py))
                    }
                }

                orbitContext.stroke(
                    path,
                    with: .color(accentTint.opacity(0.85 * Double(orbitProgress))),
                    style: StrokeStyle(lineWidth: 1.6 * s, lineCap: .round)
                )

                // 3 Output Nodes
                let nodeAngles = [0.45 * .pi, 1.15 * .pi, 1.85 * .pi]
                for (index, angle) in nodeAngles.enumerated() {
                    if index < nodesIlluminated {
                        let nx = rx * cos(angle)
                        let ny = ry * sin(angle)
                        let circleRect = CGRect(x: nx - 2.4 * s, y: ny - 2.4 * s, width: 4.8 * s, height: 4.8 * s)
                        orbitContext.fill(Path(ellipseIn: circleRect), with: .color(accentTint))
                    }
                }
            }

            // 2. Play Triangle Wedge
            if playProgress > 0 {
                var playPath = Path()
                playPath.move(to: CGPoint(x: 17.0 * s, y: 13.5 * s))
                playPath.addLine(to: CGPoint(x: 35.2 * s, y: 24.0 * s))
                playPath.addLine(to: CGPoint(x: 17.0 * s, y: 34.5 * s))
                playPath.closeSubpath()

                context.fill(playPath, with: .color(playTint.opacity(Double(min(max(playProgress, 0), 1)))))
            }

            // 3. 5 Balanced Waveform Bars
            if waveformProgress > 0 {
                let bars: [(CGFloat, CGFloat)] = [
                    (21.0, 6.0),
                    (24.0, 12.0),
                    (27.0, 16.0),
                    (30.0, 12.0),
                    (33.0, 6.0)
                ]

                for (x, maxH) in bars {
                    let curH = maxH * waveformProgress
                    let cy: CGFloat = 24.0
                    var barPath = Path()
                    barPath.move(to: CGPoint(x: x * s, y: (cy - curH * 0.5) * s))
                    barPath.addLine(to: CGPoint(x: x * s, y: (cy + curH * 0.5) * s))
                    context.stroke(
                        barPath,
                        with: .color(accentTint),
                        style: StrokeStyle(lineWidth: 1.8 * s, lineCap: .round)
                    )
                }
            }
        }
        .frame(width: size, height: size)
    }
}
