package com.smartremote.pro.presentation.ui.airtel

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartremote.pro.presentation.components.DpadControl
import com.smartremote.pro.presentation.components.TactileRemoteButton
import com.smartremote.pro.presentation.theme.AccentOrange
import com.smartremote.pro.presentation.theme.BackgroundDark
import com.smartremote.pro.presentation.theme.DeepBlue
import com.smartremote.pro.presentation.theme.ElectricBlue
import com.smartremote.pro.presentation.theme.ErrorRed
import com.smartremote.pro.presentation.theme.SuccessGreen
import com.smartremote.pro.presentation.theme.SurfaceDark
import com.smartremote.pro.presentation.theme.TextPrimaryDark
import com.smartremote.pro.presentation.theme.TextSecondary
import com.smartremote.pro.presentation.viewmodels.AirtelXstreamViewModel

@Composable
fun AirtelXstreamScreen(
    viewModel: AirtelXstreamViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()

    val favoriteChips = listOf(
        Pair(100, "Star Plus"),
        Pair(101, "Sony TV"),
        Pair(300, "Sony Ten 1"),
        Pair(420, "Colors HD")
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        // 1. Top Bar: ⬅️ Airtel Xstream  🎤 ⋮
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimaryDark)
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Airtel Xstream",
                    color = TextPrimaryDark,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = {}) {
                    Icon(Icons.Default.Mic, contentDescription = "Voice", tint = ElectricBlue)
                }
                IconButton(onClick = {}) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Menu", tint = TextSecondary)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 2. Current Channel Banner: 📺 Star Plus (100) / Yeh Rishta Kya... / 8:00 PM - 9:00 PM
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(SurfaceDark)
                .border(1.dp, Color(0xFF334155), RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Tv, contentDescription = "Channel", tint = ElectricBlue, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Star Plus (100)",
                        color = TextPrimaryDark,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Text(
                    text = "Yeh Rishta Kya Kehlata Hai",
                    color = AccentOrange,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(top = 4.dp)
                )
                Text(
                    text = "8:00 PM - 9:00 PM",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 2.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // [⬅️ Prev] [Guide] [Next ➡️]
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { viewModel.sendDthCommand("chDown") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("◀ Prev", color = TextPrimaryDark, fontSize = 12.sp)
                    }
                    Button(
                        onClick = { viewModel.sendDthCommand("guide") },
                        colors = ButtonDefaults.buttonColors(containerColor = DeepBlue),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Guide", color = ElectricBlue, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    OutlinedButton(
                        onClick = { viewModel.sendDthCommand("chUp") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Next ▶", color = TextPrimaryDark, fontSize = 12.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 3. Favorites ⭐: [100] [101] [300] [420]
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Favorites", color = TextPrimaryDark, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.width(6.dp))
            Icon(Icons.Default.Star, contentDescription = "Star", tint = AccentOrange, modifier = Modifier.size(18.dp))
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            favoriteChips.forEach { (number, name) ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(SurfaceDark)
                        .clickable { viewModel.sendDthCommand("ok") }
                        .padding(vertical = 10.dp)
                ) {
                    Text("$number", color = ElectricBlue, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    Text(name, color = TextSecondary, fontSize = 11.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 4. Quick Apps: [Netflix] [Prime] [Hotstar]
        Text("Quick Apps:", color = TextSecondary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = { viewModel.sendDthCommand("netflix") },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE50914)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Netflix", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            Button(
                onClick = { viewModel.sendDthCommand("prime") },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00A8E1)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Prime", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            Button(
                onClick = { viewModel.sendDthCommand("hotstar") },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0C2053)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Hotstar", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 5. Large Power Button (🔴)
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            TactileRemoteButton(
                onClick = { viewModel.sendDthCommand("power") },
                size = 68.dp,
                shape = CircleShape,
                icon = Icons.Default.PowerSettingsNew,
                iconTint = Color.White,
                backgroundColor = ErrorRed,
                glowColor = ErrorRed
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        // 6. Navigation & Controls D-Pad
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            DpadControl(
                onUp = { viewModel.sendDthCommand("up") },
                onDown = { viewModel.sendDthCommand("down") },
                onLeft = { viewModel.sendDthCommand("left") },
                onRight = { viewModel.sendDthCommand("right") },
                onOk = { viewModel.sendDthCommand("ok") }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 7. Recharge Info Card: Balance: ₹250.00 / Valid till: 15 Jan 2025 / [Recharge Now]
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(SurfaceDark)
                .border(1.dp, Color(0xFF334155), RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column {
                Text("Recharge Info:", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Balance: ₹250.00", color = SuccessGreen, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                        Text("Valid till: 15 Jan 2025", color = TextSecondary, fontSize = 13.sp)
                    }

                    Button(
                        onClick = {},
                        colors = ButtonDefaults.buttonColors(containerColor = AccentOrange),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Recharge Now", color = TextPrimaryDark, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}
