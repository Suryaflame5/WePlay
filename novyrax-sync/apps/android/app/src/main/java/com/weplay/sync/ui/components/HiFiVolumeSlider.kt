package com.weplay.sync.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.weplay.sync.ui.theme.WePlayTheme
import kotlin.math.roundToInt

@Composable
fun HiFiVolumeSlider(
    value: Float, // 0.0 to 1.0
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    unsupportedReason: String? = null
) {
    var widthPx by remember { mutableFloatStateOf(1f) }

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (enabled) "${(value * 100).roundToInt()}%" else "LINKED",
                style = WePlayTheme.typography.tabularMetric,
                color = if (enabled) WePlayTheme.colors.textPrimary else WePlayTheme.colors.textSecondary
            )
            Spacer(modifier = Modifier.weight(1f))
            if (!enabled && unsupportedReason != null) {
                Text(
                    text = unsupportedReason,
                    style = WePlayTheme.typography.bodyMedium.copy(fontSize = 11.sp),
                    color = WePlayTheme.colors.textSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(28.dp)
                .onSizeChanged { widthPx = it.width.toFloat().coerceAtLeast(1f) }
                .pointerInput(enabled) {
                    if (!enabled) return@pointerInput
                    detectTapGestures { offset ->
                        val newV = (offset.x / widthPx).coerceIn(0f, 1f)
                        onValueChange(newV)
                    }
                }
                .pointerInput(enabled) {
                    if (!enabled) return@pointerInput
                    detectHorizontalDragGestures { change, _ ->
                        val newV = (change.position.x / widthPx).coerceIn(0f, 1f)
                        onValueChange(newV)
                    }
                },
            contentAlignment = Alignment.CenterStart
        ) {
            // Track Background
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .clip(CircleShape)
                    .background(
                        if (enabled) WePlayTheme.colors.separator else WePlayTheme.colors.separator.copy(alpha = 0.5f)
                    )
            )

            // Active Track Fill
            if (enabled) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(fraction = value.coerceIn(0f, 1f))
                        .height(3.dp)
                        .clip(CircleShape)
                        .background(WePlayTheme.colors.accent)
                )

                // Precise Physical-Inspired Thumb (Circular hairline node, not bloated bulb)
                Box(
                    modifier = Modifier
                        .padding(start = ((widthPx * value.coerceIn(0f, 1f)) - 7f).coerceAtLeast(0f).dp)
                        .size(14.dp)
                        .clip(CircleShape)
                        .background(WePlayTheme.colors.surface)
                        .padding(2.dp)
                        .clip(CircleShape)
                        .background(WePlayTheme.colors.accent)
                )
            }
        }
    }
}
