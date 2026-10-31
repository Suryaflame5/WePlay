package com.weplay.sync.shared.timeline

class AudioTimeline(
    val referenceSampleRate: Int = 48000
) {
    private var timelineOriginNanoTime: Long = 0L
    private var currentPositionFrames: Long = 0L
    private var isPlaying: Boolean = false

    fun start(originNanoTime: Long = System.nanoTime(), initialFrame: Long = 0L) {
        timelineOriginNanoTime = originNanoTime
        currentPositionFrames = initialFrame
        isPlaying = true
    }

    fun pause() {
        isPlaying = false
    }

    fun seekToFrame(targetFrame: Long, originNanoTime: Long = System.nanoTime()) {
        currentPositionFrames = targetFrame
        timelineOriginNanoTime = originNanoTime
    }

    fun getCurrentFrame(currentNanoTime: Long = System.nanoTime()): Long {
        if (!isPlaying) return currentPositionFrames
        val elapsedNanos = currentNanoTime - timelineOriginNanoTime
        val elapsedSeconds = elapsedNanos.toDouble() / 1_000_000_000.0
        return currentPositionFrames + (elapsedSeconds * referenceSampleRate).toLong()
    }

    fun frameToDurationMs(frame: Long): Long {
        return (frame * 1000L) / referenceSampleRate
    }

    fun msToFrame(ms: Long): Long {
        return (ms * referenceSampleRate) / 1000L
    }
}
