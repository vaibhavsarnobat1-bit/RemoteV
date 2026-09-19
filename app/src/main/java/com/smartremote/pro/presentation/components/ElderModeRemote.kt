package com.smartremote.pro.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartremote.pro.presentation.theme.DarkBackground
import com.smartremote.pro.presentation.theme.NeonAmber
import com.smartremote.pro.presentation.theme.PowerRed
import com.smartremote.pro.presentation.theme.TextPrimary

@Composable
fun ElderModeRemote(
    onPower: () -> Unit,
    onVolUp: () -> Unit,
    onVolDown: () -> Unit,
    onChUp: () -> Unit,
    onChDown: () -> Unit,
    onMute: () -> Unit,
    onHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    val buttonSize = 84.dp
    val buttonShape = RoundedCornerShape(20.dp)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Text(
            text = "SIMPLE REMOTE (ELDER MODE)",
            color = NeonAmber,
            fontSize = 18.sp,
            fontWeight = FontWeight.Black
        )

        // Huge Power Button
        TactileRemoteButton(
            onClick = onPower,
            size = 96.dp,
            shape = buttonShape,
            icon = Icons.Default.PowerSettingsNew,
            iconTint = Color.White,
            backgroundColor = PowerRed,
            borderColor = Color.White,
            glowColor = PowerRed
        )

        // Volume Controls Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                TactileRemoteButton(
                    onClick = onVolUp,
                    size = buttonSize,
                    shape = buttonShape,
                    text = "VOL +",
                    textColor = Color.Black,
                    backgroundColor = Color.White,
                    borderColor = NeonAmber
                )
                Spacer(modifier = Modifier.height(12.dp))
                TactileRemoteButton(
                    onClick = onVolDown,
                    size = buttonSize,
                    shape = buttonShape,
                    text = "VOL -",
                    textColor = Color.Black,
                    backgroundColor = Color.White,
                    borderColor = NeonAmber
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                TactileRemoteButton(
                    onClick = onChUp,
                    size = buttonSize,
                    shape = buttonShape,
                    text = "CH +",
                    textColor = Color.Black,
                    backgroundColor = NeonAmber,
                    borderColor = Color.White
                )
                Spacer(modifier = Modifier.height(12.dp))
                TactileRemoteButton(
                    onClick = onChDown,
                    size = buttonSize,
                    shape = buttonShape,
                    text = "CH -",
                    textColor = Color.Black,
                    backgroundColor = NeonAmber,
                    borderColor = Color.White
                )
            }
        }

        // Bottom Controls (Mute, Home)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            TactileRemoteButton(
                onClick = onMute,
                size = 72.dp,
                shape = buttonShape,
                icon = Icons.Default.VolumeOff,
                iconTint = Color.White,
                backgroundColor = Color(0xFF374151)
            )

            TactileRemoteButton(
                onClick = onHome,
                size = 72.dp,
                shape = buttonShape,
                icon = Icons.Default.Home,
                iconTint = Color.White,
                backgroundColor = Color(0xFF374151)
            )
        }
    }
}
