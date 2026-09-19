package com.smartremote.pro.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.smartremote.pro.presentation.theme.CardDark
import com.smartremote.pro.presentation.theme.CyberCyan
import com.smartremote.pro.presentation.theme.RemoteButtonBorder

@Composable
fun DpadControl(
    onUp: () -> Unit,
    onDown: () -> Unit,
    onLeft: () -> Unit,
    onRight: () -> Unit,
    onOk: () -> Unit,
    modifier: Modifier = Modifier
) {
    val outerSize = 200.dp
    val buttonSize = 48.dp

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(outerSize)
            .clip(CircleShape)
            .background(CardDark)
            .border(1.5.dp, RemoteButtonBorder, CircleShape)
    ) {
        // UP
        TactileRemoteButton(
            onClick = onUp,
            size = buttonSize,
            icon = Icons.Default.KeyboardArrowUp,
            iconTint = CyberCyan,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = 12.dp)
        )

        // DOWN
        TactileRemoteButton(
            onClick = onDown,
            size = buttonSize,
            icon = Icons.Default.KeyboardArrowDown,
            iconTint = CyberCyan,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = (-12).dp)
        )

        // LEFT
        TactileRemoteButton(
            onClick = onLeft,
            size = buttonSize,
            icon = Icons.Default.KeyboardArrowLeft,
            iconTint = CyberCyan,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .offset(x = 12.dp)
        )

        // RIGHT
        TactileRemoteButton(
            onClick = onRight,
            size = buttonSize,
            icon = Icons.Default.KeyboardArrowRight,
            iconTint = CyberCyan,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .offset(x = (-12).dp)
        )

        // CENTER OK
        TactileRemoteButton(
            onClick = onOk,
            size = 64.dp,
            text = "OK",
            textColor = CyberCyan,
            glowColor = CyberCyan,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}
