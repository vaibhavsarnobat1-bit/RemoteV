package com.smartremote.pro.presentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartremote.pro.presentation.theme.CyberCyan
import com.smartremote.pro.presentation.theme.RemoteButtonBg
import com.smartremote.pro.presentation.theme.RemoteButtonBorder
import com.smartremote.pro.presentation.theme.TextPrimary

@Composable
fun TactileRemoteButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 56.dp,
    shape: Shape = CircleShape,
    icon: ImageVector? = null,
    text: String? = null,
    iconTint: Color = CyberCyan,
    textColor: Color = TextPrimary,
    backgroundColor: Color = RemoteButtonBg,
    borderColor: Color = RemoteButtonBorder,
    glowColor: Color = CyberCyan
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1.0f,
        label = "button_scale"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(size)
            .scale(scale)
            .clip(shape)
            .background(if (isPressed) glowColor.copy(alpha = 0.25f) else backgroundColor)
            .border(
                width = if (isPressed) 2.dp else 1.dp,
                color = if (isPressed) glowColor else borderColor,
                shape = shape
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                tint = if (isPressed) glowColor else iconTint,
                modifier = Modifier.size(size * 0.45f)
            )
        } else if (text != null) {
            Text(
                text = text,
                color = if (isPressed) glowColor else textColor,
                fontSize = if (text.length > 3) 12.sp else 15.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
