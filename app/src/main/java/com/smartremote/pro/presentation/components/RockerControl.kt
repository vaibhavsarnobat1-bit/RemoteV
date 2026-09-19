package com.smartremote.pro.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartremote.pro.presentation.theme.CyberCyan
import com.smartremote.pro.presentation.theme.RemoteButtonBg
import com.smartremote.pro.presentation.theme.RemoteButtonBorder
import com.smartremote.pro.presentation.theme.TextSecondary

@Composable
fun RockerControl(
    title: String,
    onUpClick: () -> Unit,
    onDownClick: () -> Unit,
    modifier: Modifier = Modifier,
    isChannelRocker: Boolean = false
) {
    val rockerShape = RoundedCornerShape(28.dp)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .width(62.dp)
            .height(140.dp)
            .clip(rockerShape)
            .background(RemoteButtonBg)
            .border(1.dp, RemoteButtonBorder, rockerShape)
            .padding(vertical = 4.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Upper button (+ / Up arrow)
        TactileRemoteButton(
            onClick = onUpClick,
            size = 46.dp,
            icon = if (isChannelRocker) Icons.Default.KeyboardArrowUp else Icons.Default.Add,
            iconTint = CyberCyan
        )

        // Center Label (VOL / CH)
        Text(
            text = title,
            color = TextSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )

        // Lower button (- / Down arrow)
        TactileRemoteButton(
            onClick = onDownClick,
            size = 46.dp,
            icon = if (isChannelRocker) Icons.Default.KeyboardArrowDown else Icons.Default.Remove,
            iconTint = CyberCyan
        )
    }
}
