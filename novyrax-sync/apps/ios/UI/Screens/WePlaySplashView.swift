import SwiftUI

public struct WePlaySplashView: View {
    public var onComplete: () -> Void

    @Environment(\.colorScheme) var colorScheme
    @Environment(\.accessibilityReduceMotion) var reduceMotion

    @State private var bgOpacity: Double = 0.0
    @State private var waveformProgress: CGFloat = 0.0
    @State private var playProgress: CGFloat = 0.0
    @State private var orbitProgress: CGFloat = 0.0
    @State private var nodesCount: Int = 0
    @State private var wordmarkOpacity: Double = 0.0
    @State private var wordmarkOffset: CGFloat = 6.0
    @State private var taglineOpacity: Double = 0.0

    public init(onComplete: @escaping () -> Void) {
        self.onComplete = onComplete
    }

    public var body: some View {
        ZStack {
            (colorScheme == .dark ? WePlayColors.canvasDark : WePlayColors.canvasLight)
                .ignoresSafeArea()
                .opacity(bgOpacity)

            VStack(spacing: 0) {
                WePlayMarkView(
                    size: 72,
                    playTint: colorScheme == .dark ? WePlayColors.textPrimaryDark : WePlayColors.textPrimaryLight,
                    accentTint: WePlayColors.accentTeal,
                    waveformProgress: waveformProgress,
                    playProgress: playProgress,
                    orbitProgress: orbitProgress,
                    nodesIlluminated: nodesCount
                )

                Spacer().frame(height: 20)

                Text("WePlay")
                    .font(.system(size: 32, weight: .semibold, design: .default))
                    .tracking(-0.8)
                    .foregroundColor(colorScheme == .dark ? WePlayColors.textPrimaryDark : WePlayColors.textPrimaryLight)
                    .offset(y: wordmarkOffset)
                    .opacity(wordmarkOpacity)

                Spacer().frame(height: 6)

                Text("One source. In sync.")
                    .font(.system(size: 14, weight: .regular))
                    .tracking(0.3)
                    .foregroundColor(colorScheme == .dark ? WePlayColors.textSecondaryDark : WePlayColors.textSecondaryLight)
                    .opacity(taglineOpacity)
            }
        }
        .onAppear {
            runAnimationSequence()
        }
    }

    private fun runAnimationSequence() {
        if reduceMotion {
            bgOpacity = 1.0
            waveformProgress = 1.0
            playProgress = 1.0
            orbitProgress = 1.0
            nodesCount = 3
            wordmarkOpacity = 1.0
            wordmarkOffset = 0.0
            taglineOpacity = 1.0
            DispatchQueue.main.asyncAfter(deadline: .now() + 0.15) {
                onComplete()
            }
            return
        }

        // 1. Reveal Background (0 - 150ms)
        withAnimation(.easeOut(duration: 0.15)) {
            bgOpacity = 1.0
        }

        // 2. Waveform bars upward (80 - 280ms)
        DispatchQueue.main.asyncAfter(deadline: .now() + 0.08) {
            withAnimation(.easeOut(duration: 0.20)) {
                waveformProgress = 1.0
            }
        }

        // 3. Play gesture reveal (180 - 450ms)
        DispatchQueue.main.asyncAfter(deadline: .now() + 0.18) {
            withAnimation(.easeInOut(duration: 0.27)) {
                playProgress = 1.0
            }
        }

        // 4. Orbit line & nodes sequence (320 - 640ms)
        DispatchQueue.main.asyncAfter(deadline: .now() + 0.32) {
            withAnimation(.easeOut(duration: 0.32)) {
                orbitProgress = 1.0
            }
        }
        DispatchQueue.main.asyncAfter(deadline: .now() + 0.42) { nodesCount = 1 }
        DispatchQueue.main.asyncAfter(deadline: .now() + 0.51) { nodesCount = 2 }
        DispatchQueue.main.asyncAfter(deadline: .now() + 0.60) { nodesCount = 3 }

        // 5. Wordmark reveal (520 - 740ms)
        DispatchQueue.main.asyncAfter(deadline: .now() + 0.52) {
            withAnimation(.easeOut(duration: 0.22)) {
                wordmarkOpacity = 1.0
                wordmarkOffset = 0.0
            }
        }

        // 6. Tagline reveal (640 - 820ms)
        DispatchQueue.main.asyncAfter(deadline: .now() + 0.64) {
            withAnimation(.easeOut(duration: 0.18)) {
                taglineOpacity = 1.0
            }
        }

        // 7. Transition to application (~880ms)
        DispatchQueue.main.asyncAfter(deadline: .now() + 0.88) {
            onComplete()
        }
    }
}
