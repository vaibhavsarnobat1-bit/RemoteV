package com.smartremote.pro.presentation.ui.remote

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ModeFanOff
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.RotateRight
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartremote.pro.domain.models.Device
import com.smartremote.pro.presentation.components.TactileRemoteButton
import com.smartremote.pro.presentation.theme.CardDark
import com.smartremote.pro.presentation.theme.CyberCyan
import com.smartremote.pro.presentation.theme.DarkBackground
import com.smartremote.pro.presentation.theme.NeonAmber
import com.smartremote.pro.presentation.theme.PowerRed
import com.smartremote.pro.presentation.theme.RemoteButtonBorder
import com.smartremote.pro.presentation.theme.TextPrimary
import com.smartremote.pro.presentation.theme.TextSecondary

@Composable
fun AcRemoteLayout(
    device: Device,
    temperature: Int,
    onAdjustTemp: (Int) -> Unit,
    onCommand: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Power Toggle
        TactileRemoteButton(
            onClick = { onCommand("power") },
            size = 64.dp,
            icon = Icons.Default.PowerSettingsNew,
            iconTint = PowerRed,
            glowColor = PowerRed
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Circular Temperature Display & Stepper
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(200.dp)
                .clip(CircleShape)
                .background(CardDark)
                .border(2.dp, CyberCyan, CircleShape)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "$temperature°C",
                    color = CyberCyan,
                    fontSize = 44.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = "TARGET TEMP",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Temp Adjustment Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            TactileRemoteButton(
                onClick = { onAdjustTemp(-1) },
                size = 56.dp,
                icon = Icons.Default.Remove,
                iconTint = CyberCyan
            )
            TactileRemoteButton(
                onClick = { onAdjustTemp(1) },
                size = 56.dp,
                icon = Icons.Default.Add,
                iconTint = CyberCyan
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        // AC Mode Selection
        Text(
            text = "OPERATION MODE",
            color = TextSecondary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            listOf("Cool" to "modeCool", "Heat" to "modeHeat", "Fan" to "modeFan").forEach { (label, cmd) ->
                FilterChip(
                    selected = label == "Cool",
                    onClick = { onCommand(cmd) },
                    label = { Text(label, fontWeight = FontWeight.Bold) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = CyberCyan,
                        selectedLabelColor = DarkBackground,
                        containerColor = CardDark,
                        labelColor = TextPrimary
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Fan Speed and Swing Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            TactileRemoteButton(
                onClick = { onCommand("fanSpeed") },
                size = 54.dp,
                icon = Icons.Default.ModeFanOff,
                iconTint = NeonAmber
            )
            TactileRemoteButton(
                onClick = { onCommand("swing") },
                size = 54.dp,
                icon = Icons.Default.RotateRight,
                iconTint = NeonAmber
            )
        }
    }
}
