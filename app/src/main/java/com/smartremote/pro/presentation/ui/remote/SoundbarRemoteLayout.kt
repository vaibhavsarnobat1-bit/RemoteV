package com.smartremote.pro.presentation.ui.remote

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartremote.pro.domain.models.Device
import com.smartremote.pro.presentation.components.RockerControl
import com.smartremote.pro.presentation.components.TactileRemoteButton
import com.smartremote.pro.presentation.theme.CardDark
import com.smartremote.pro.presentation.theme.CyberCyan
import com.smartremote.pro.presentation.theme.DarkBackground
import com.smartremote.pro.presentation.theme.ElectricViolet
import com.smartremote.pro.presentation.theme.PowerRed
import com.smartremote.pro.presentation.theme.TextPrimary
import com.smartremote.pro.presentation.theme.TextSecondary

@Composable
fun SoundbarRemoteLayout(
    device: Device,
    onCommand: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Power and Mute
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TactileRemoteButton(
                onClick = { onCommand("power") },
                size = 56.dp,
                icon = Icons.Default.PowerSettingsNew,
                iconTint = PowerRed,
                glowColor = PowerRed
            )
            TactileRemoteButton(
                onClick = { onCommand("mute") },
                size = 56.dp,
                icon = Icons.Default.VolumeOff,
                iconTint = CyberCyan
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Master Volume Rocker
        RockerControl(
            title = "VOLUME",
            onUpClick = { onCommand("volUp") },
            onDownClick = { onCommand("volDown") }
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Audio Inputs
        Text(
            text = "AUDIO SOURCE",
            color = TextSecondary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            listOf("Bluetooth" to "bluetooth", "Optical" to "optical", "HDMI ARC" to "tvAudio").forEach { (label, cmd) ->
                FilterChip(
                    selected = label == "Bluetooth",
                    onClick = { onCommand(cmd) },
                    label = { Text(label, fontWeight = FontWeight.Bold) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = ElectricViolet,
                        selectedLabelColor = DarkBackground,
                        containerColor = CardDark,
                        labelColor = TextPrimary
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Bass Boost
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            TactileRemoteButton(
                onClick = { onCommand("bassUp") },
                size = 54.dp,
                icon = Icons.Default.GraphicEq,
                iconTint = CyberCyan
            )
            TactileRemoteButton(
                onClick = { onCommand("bluetooth") },
                size = 54.dp,
                icon = Icons.Default.Bluetooth,
                iconTint = ElectricViolet
            )
        }
    }
}
