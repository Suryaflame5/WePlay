package com.weplay.sync.ui.components

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel

/**
 * 100% Genuine ISO/IEC 18004 Standard QR Code Component.
 * Produces authentic scannable QR matrices compatible with Google Lens, iOS Camera,
 * and any physical QR code scanner.
 */
@Composable
fun RealQRCode(
    content: String,
    modifier: Modifier = Modifier,
    size: Dp = 180.dp
) {
    val bitmap = remember(content) {
        generateRealQrBitmap(content, 512, 512)
    }

    Box(modifier = modifier.size(size)) {
        if (bitmap != null) {
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "Scannable QR Code: $content",
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

fun generateRealQrBitmap(content: String, width: Int, height: Int): Bitmap? {
    return try {
        val hints = mapOf(
            EncodeHintType.CHARACTER_SET to "UTF-8",
            EncodeHintType.ERROR_CORRECTION to ErrorCorrectionLevel.M,
            EncodeHintType.MARGIN to 1
        )
        val bitMatrix = QRCodeWriter().encode(
            content.ifEmpty { "WEPLAY:WP-4829" },
            BarcodeFormat.QR_CODE,
            width,
            height,
            hints
        )
        val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        for (x in 0 until width) {
            for (y in 0 until height) {
                bmp.setPixel(
                    x,
                    y,
                    if (bitMatrix.get(x, y)) android.graphics.Color.BLACK else android.graphics.Color.WHITE
                )
            }
        }
        bmp
    } catch (e: Exception) {
        null
    }
}
