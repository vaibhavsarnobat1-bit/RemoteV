package com.smartremote.pro.presentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.smartremote.pro.presentation.theme.CyberCyan
import com.smartremote.pro.presentation.theme.ElectricViolet

@Composable
fun AudioWaveform(
    normalizedAudioLevel: Float, // 0.0f to 1.0f
    isListening: Boolean,
    modifier: Modifier = Modifier
) {
    val barCount = 19
    val barWidth = 4.dp
    val maxHeight = 60.dp
    val minHeight = 8.dp

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(maxHeight),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 0 until barCount) {
            // Calculate a wave distribution factor (peak in center)
            val distFromCenter = kotlin.math.abs(i - (barCount / 2)).toFloat() / (barCount / 2).toFloat()
            val scale = (1.0f - distFromCenter * 0.65f)

            val animatedHeightFraction by animateFloatAsState(
                targetValue = if (isListening) {
                    ((normalizedAudioLevel * scale) + 0.15f).coerceIn(0.1f, 1.0f)
                } else {
                    0.1f
                },
                animationSpec = tween(durationMillis = 100),
                label = "waveform_bar_$i"
            )

            val barHeight = minHeight + (maxHeight - minHeight) * animatedHeightFraction
            val color = if (i % 2 == 0) CyberCyan else ElectricViolet

            Box(
                modifier = Modifier
                    .padding(horizontal = 2.5.dp)
                    .width(barWidth)
                    .height(barHeight)
                    .clip(RoundedCornerShape(2.dp))
                    .background(color)
            )
        }
    }
}
