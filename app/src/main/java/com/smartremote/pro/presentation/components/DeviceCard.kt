package com.smartremote.pro.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.SettingsInputAntenna
import androidx.compose.material.icons.filled.Speaker
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartremote.pro.domain.models.ConnectionType
import com.smartremote.pro.domain.models.Device
import com.smartremote.pro.domain.models.DeviceType
import com.smartremote.pro.presentation.theme.CardDark
import com.smartremote.pro.presentation.theme.CyberCyan
import com.smartremote.pro.presentation.theme.GreenSuccess
import com.smartremote.pro.presentation.theme.NeonAmber
import com.smartremote.pro.presentation.theme.PowerRed
import com.smartremote.pro.presentation.theme.RemoteButtonBorder
import com.smartremote.pro.presentation.theme.TextPrimary
import com.smartremote.pro.presentation.theme.TextSecondary

@Composable
fun DeviceCard(
    device: Device,
    onClick: () -> Unit,
    onPowerToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cardShape = RoundedCornerShape(16.dp)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(cardShape)
            .background(CardDark)
            .border(1.dp, RemoteButtonBorder, cardShape)
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Device Category Icon
                val icon = when (device.type) {
                    DeviceType.TV -> Icons.Default.Tv
                    DeviceType.AC -> Icons.Default.AcUnit
                    DeviceType.SET_TOP_BOX -> Icons.Default.SettingsInputAntenna
                    DeviceType.SOUNDBAR -> Icons.Default.Speaker
                    else -> Icons.Default.Tv
                }

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(CyberCyan.copy(alpha = 0.15f))
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = device.name,
                        tint = CyberCyan,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Power Button
                TactileRemoteButton(
                    onClick = onPowerToggle,
                    size = 38.dp,
                    icon = Icons.Default.PowerSettingsNew,
                    iconTint = PowerRed,
                    glowColor = PowerRed
                )
            }

            Text(
                text = device.name,
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 12.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${device.brand} • ${device.room}",
                    color = TextSecondary,
                    fontSize = 13.sp
                )

                // Connectivity Badge (IR / WiFi / Hybrid)
                val badgeColor = when (device.connectionType) {
                    ConnectionType.IR -> NeonAmber
                    ConnectionType.WIFI -> GreenSuccess
                    ConnectionType.HYBRID -> CyberCyan
                    ConnectionType.BLUETOOTH -> CyberCyan
                }

                Text(
                    text = device.connectionType.name,
                    color = badgeColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(badgeColor.copy(alpha = 0.15f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
    }
}
