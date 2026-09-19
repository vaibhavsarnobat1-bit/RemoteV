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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.Input
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartremote.pro.domain.models.Device
import com.smartremote.pro.presentation.components.DpadControl
import com.smartremote.pro.presentation.components.TactileRemoteButton
import com.smartremote.pro.presentation.theme.AccentOrange
import com.smartremote.pro.presentation.theme.DeepBlue
import com.smartremote.pro.presentation.theme.ElectricBlue
import com.smartremote.pro.presentation.theme.ErrorRed
import com.smartremote.pro.presentation.theme.SurfaceDark
import com.smartremote.pro.presentation.theme.TextPrimaryDark
import com.smartremote.pro.presentation.theme.TextSecondary

@Composable
fun TvRemoteLayout(
    device: Device,
    onCommand: (String) -> Unit,
    onLaunchApp: (String) -> Unit,
    onOpenLearningMode: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var volumeValue by remember { mutableFloatStateOf(0.75f) } // 75% default

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 1. Large Power Button
        Box(
            modifier = Modifier
                .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            TactileRemoteButton(
                onClick = { onCommand("power") },
                size = 72.dp,
                shape = CircleShape,
                icon = Icons.Default.PowerSettingsNew,
                iconTint = Color.White,
                backgroundColor = ErrorRed,
                borderColor = Color(0xFFFCA5A5),
                glowColor = ErrorRed
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 2. D-Pad Navigation Pad
        Text(
            text = "D-Pad Navigation",
            color = TextSecondary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(8.dp))
        DpadControl(
            onUp = { onCommand("up") },
            onDown = { onCommand("down") },
            onLeft = { onCommand("left") },
            onRight = { onCommand("right") },
            onOk = { onCommand("ok") }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 3. Media Controls: [◀️◀️] [▶️] [⏸️] [▶️▶️]
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(SurfaceDark)
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TactileRemoteButton(
                onClick = { onCommand("rewind") },
                size = 44.dp,
                icon = Icons.Default.FastRewind,
                iconTint = ElectricBlue
            )
            TactileRemoteButton(
                onClick = { onCommand("play") },
                size = 48.dp,
                icon = Icons.Default.PlayArrow,
                iconTint = ElectricBlue
            )
            TactileRemoteButton(
                onClick = { onCommand("pause") },
                size = 48.dp,
                icon = Icons.Default.Pause,
                iconTint = ElectricBlue
            )
            TactileRemoteButton(
                onClick = { onCommand("forward") },
                size = 44.dp,
                icon = Icons.Default.FastForward,
                iconTint = ElectricBlue
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 4. Volume Slider: Volume: ━━━━━●━━━━ 🔊 75%
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(SurfaceDark)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.VolumeUp, contentDescription = "Volume", tint = ElectricBlue, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Volume", color = TextPrimaryDark, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                }
                Text(
                    text = "${(volumeValue * 100).toInt()}%",
                    color = ElectricBlue,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Slider(
                value = volumeValue,
                onValueChange = {
                    volumeValue = it
                    onCommand("volUp")
                },
                colors = SliderDefaults.colors(
                    thumbColor = ElectricBlue,
                    activeTrackColor = ElectricBlue,
                    inactiveTrackColor = Color(0xFF334155)
                )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 5. Quick Actions: [🔇 Mute]  [⚙️ Source]
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = { onCommand("mute") },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SurfaceDark),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.VolumeOff, contentDescription = "Mute", tint = AccentOrange, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Mute", color = TextPrimaryDark, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            }

            Button(
                onClick = { onCommand("source") },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SurfaceDark),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Input, contentDescription = "Source", tint = ElectricBlue, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Source", color = TextPrimaryDark, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 6. Numpad Grid: [1][2][3] / [4][5][6] / [7][8][9] / [*][0][#]
        Text(
            text = "Numpad",
            color = TextSecondary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(8.dp))
        val numpadRows = listOf(
            listOf("1", "2", "3"),
            listOf("4", "5", "6"),
            listOf("7", "8", "9"),
            listOf("*", "0", "#")
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(SurfaceDark)
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            numpadRows.forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    row.forEach { digit ->
                        TactileRemoteButton(
                            onClick = { onCommand("num$digit") },
                            size = 46.dp,
                            text = digit,
                            textColor = TextPrimaryDark,
                            backgroundColor = DeepBlue.copy(alpha = 0.3f),
                            borderColor = Color(0xFF334155)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 7. App Shortcuts: [📺 Netflix] [▶️ YouTube] / [⭐ Hotstar] [🎬 Prime]
        Text(
            text = "Apps",
            color = TextSecondary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = { onLaunchApp("netflix") },
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE50914)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Netflix", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
            Button(
                onClick = { onLaunchApp("youtube") },
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF0000)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("YouTube", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = { onLaunchApp("hotstar") },
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0C2053)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Hotstar", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
            Button(
                onClick = { onLaunchApp("prime") },
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00A8E1)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Prime", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 8. Bottom Actions: [💬 Macros] [⏰ Timer]
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onOpenLearningMode,
                modifier = Modifier
                    .weight(1f)
                    .height(46.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SurfaceDark),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.Tune, contentDescription = "Macros", tint = AccentOrange, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Macros", color = TextPrimaryDark, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            }
            Button(
                onClick = {},
                modifier = Modifier
                    .weight(1f)
                    .height(46.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SurfaceDark),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.Schedule, contentDescription = "Timer", tint = ElectricBlue, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Timer", color = TextPrimaryDark, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            }
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}
