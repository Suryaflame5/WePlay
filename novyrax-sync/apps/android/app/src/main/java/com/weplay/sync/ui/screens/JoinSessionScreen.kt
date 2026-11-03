package com.weplay.sync.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.weplay.sync.shared.model.NearbyStream
import com.weplay.sync.ui.components.FlowingCopperRibbon
import com.weplay.sync.ui.components.WePlayIcon
import com.weplay.sync.ui.components.WePlayIconType
import com.weplay.sync.ui.components.WePlayStreamQRCode
import com.weplay.sync.ui.theme.WePlayBorder
import com.weplay.sync.ui.theme.WePlayBorderSubtle
import com.weplay.sync.ui.theme.WePlayObsidian
import com.weplay.sync.ui.theme.WePlayOrangeGlow
import com.weplay.sync.ui.theme.WePlayOrangePrimary
import com.weplay.sync.ui.theme.WePlayTextMuted
import com.weplay.sync.ui.theme.WePlayTextPrimary
import com.weplay.sync.ui.theme.WePlayTextSecondary

/**
 * Screen 4: Master Join Session Screen ("Join WePlay Session").
 * Exact visual match to Screen 4 in the design reference:
 * - Top Bar: Back arrow "<" + Scanner reticle "[ ]"
 * - "Join WePlay Session" header + "Scan QR or enter PIN"
 * - Large crisp QR code framed by 4 glowing orange corner registration brackets "[ ]"
 * - Subtle "OR" horizontal divider
 * - Pill capsule PIN input field with clear "(X)" button
 * - Radiant molten orange pill button: "Join"
 * - Helper tip: Headphone icon + "Adjust volume after joining"
 * - Flowing metallic copper ribbon filament in the bottom backdrop
 */
@Composable
fun JoinSessionScreen(
    isConnectedToHost: Boolean,
    connectedHostName: String?,
    connectedSessionCode: String?,
    nearbyStreams: List<NearbyStream>,
    onConnectByCode: (String) -> Unit,
    onConnectToStream: (NearbyStream) -> Unit,
    onLeaveSession: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var enteredPin by remember { mutableStateOf("WP-4829") }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(WePlayObsidian)
    ) {
        // Bottom Flowing Copper Ribbon
        FlowingCopperRibbon(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .alpha(0.75f),
            height = 180.dp
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 36.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. Top Bar: Back Arrow & QR Scanner Icon
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 22.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = WePlayTextPrimary,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { onBack() }
                )

                WePlayIcon(
                    type = WePlayIconType.CROP_RETICLE,
                    tint = WePlayTextPrimary,
                    size = 20.dp,
                    modifier = Modifier.clickable { /* trigger scanner */ }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 2. Header & Subtitle
            Text(
                text = "Join WePlay Session",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = WePlayTextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Scan QR or enter PIN",
                fontSize = 13.sp,
                color = WePlayTextSecondary
            )

            Spacer(modifier = Modifier.height(26.dp))

            // 3. Central QR Code with 4 Glowing Orange Corner Reticles
            Box(
                modifier = Modifier
                    .size(210.dp),
                contentAlignment = Alignment.Center
            ) {
                // Canvas drawing the 4 glowing corner brackets
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = this.size.width
                    val h = this.size.height
                    val bracketLen = 28.dp.toPx()
                    val stroke = 3.dp.toPx()
                    val bracketColor = WePlayOrangeGlow

                    // Top-Left bracket
                    drawLine(bracketColor, Offset(0f, 0f), Offset(bracketLen, 0f), stroke, cap = StrokeCap.Round)
                    drawLine(bracketColor, Offset(0f, 0f), Offset(0f, bracketLen), stroke, cap = StrokeCap.Round)

                    // Top-Right bracket
                    drawLine(bracketColor, Offset(w, 0f), Offset(w - bracketLen, 0f), stroke, cap = StrokeCap.Round)
                    drawLine(bracketColor, Offset(w, 0f), Offset(w, bracketLen), stroke, cap = StrokeCap.Round)

                    // Bottom-Left bracket
                    drawLine(bracketColor, Offset(0f, h), Offset(bracketLen, h), stroke, cap = StrokeCap.Round)
                    drawLine(bracketColor, Offset(0f, h), Offset(0f, h - bracketLen), stroke, cap = StrokeCap.Round)

                    // Bottom-Right bracket
                    drawLine(bracketColor, Offset(w, h), Offset(w - bracketLen, h), stroke, cap = StrokeCap.Round)
                    drawLine(bracketColor, Offset(w, h), Offset(w, h - bracketLen), stroke, cap = StrokeCap.Round)
                }

                // QR Code Container
                Box(
                    modifier = Modifier
                        .size(175.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White)
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    WePlayStreamQRCode(sessionCode = "WP-4829")
                }
            }

            Spacer(modifier = Modifier.height(26.dp))

            // 4. "OR" Divider
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 48.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = WePlayBorderSubtle,
                    thickness = 1.dp
                )
                Text(
                    text = "OR",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = WePlayTextMuted,
                    modifier = Modifier.padding(horizontal = 14.dp)
                )
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = WePlayBorderSubtle,
                    thickness = 1.dp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 5. Capsule PIN Input Field (Pill container with "WP-4829" + (X) clear icon)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
                    .height(52.dp)
                    .clip(RoundedCornerShape(26.dp))
                    .background(Color(0xFF101520))
                    .border(1.dp, WePlayBorder, RoundedCornerShape(26.dp))
                    .padding(horizontal = 20.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    BasicTextField(
                        value = enteredPin,
                        onValueChange = { enteredPin = it.uppercase() },
                        textStyle = TextStyle(
                            color = WePlayTextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.5.sp,
                            textAlign = TextAlign.Center
                        ),
                        singleLine = true,
                        cursorBrush = SolidColor(WePlayOrangePrimary),
                        modifier = Modifier.weight(1f)
                    )

                    if (enteredPin.isNotEmpty()) {
                        WePlayIcon(
                            type = WePlayIconType.CANCEL,
                            tint = WePlayTextMuted,
                            size = 18.dp,
                            modifier = Modifier.clickable { enteredPin = "" }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 6. Primary Action Button: "Join" (Radiant Molten Orange Pill)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
                    .height(54.dp)
                    .clip(RoundedCornerShape(27.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                WePlayOrangePrimary,
                                WePlayOrangeGlow
                            )
                        )
                    )
                    .clickable {
                        if (enteredPin.isNotBlank()) onConnectByCode(enteredPin)
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Join",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    letterSpacing = 0.5.sp
                )
            }

            Spacer(modifier = Modifier.height(22.dp))

            // 7. Helper Tip: Headphone Icon + "Adjust volume after joining"
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                WePlayIcon(
                    type = WePlayIconType.HEADPHONES,
                    tint = WePlayOrangeGlow.copy(alpha = 0.85f),
                    size = 14.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Adjust volume after joining",
                    fontSize = 12.sp,
                    color = WePlayTextSecondary
                )
            }
        }
    }
}
