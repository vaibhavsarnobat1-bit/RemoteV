package com.smartremote.pro.presentation.ui.home

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SettingsInputAntenna
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartremote.pro.domain.models.Device
import com.smartremote.pro.domain.models.DeviceType
import com.smartremote.pro.presentation.theme.AccentOrange
import com.smartremote.pro.presentation.theme.BackgroundDark
import com.smartremote.pro.presentation.theme.DeepBlue
import com.smartremote.pro.presentation.theme.ElectricBlue
import com.smartremote.pro.presentation.theme.ErrorRed
import com.smartremote.pro.presentation.theme.SuccessGreen
import com.smartremote.pro.presentation.theme.SurfaceDark
import com.smartremote.pro.presentation.theme.TextPrimaryDark
import com.smartremote.pro.presentation.theme.TextSecondary
import com.smartremote.pro.presentation.viewmodels.HomeViewModel

data class QuickActionItem(
    val title: String,
    val icon: ImageVector,
    val color: Color
)

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onDeviceClick: (Device) -> Unit,
    onVoiceSearchClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onAddNewDeviceClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()

    val quickActions = listOf(
        QuickActionItem("TV", Icons.Default.Tv, ElectricBlue),
        QuickActionItem("AC", Icons.Default.AcUnit, Color(0xFF06B6D4)),
        QuickActionItem("Lights", Icons.Default.Lightbulb, AccentOrange)
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // 1. Top Bar: [ ☰  SmartRemote Pro   🔍 ⚙️ ]
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu", tint = TextPrimaryDark)
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "SmartRemote Pro",
                        color = TextPrimaryDark,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onVoiceSearchClick) {
                        Icon(Icons.Default.Search, contentDescription = "Search", tint = TextSecondary)
                    }
                    IconButton(onClick = onSettingsClick) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings", tint = TextSecondary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 2. Voice Search Bar: [ 🎤 "Say a command..." ]
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(SurfaceDark)
                    .clickable(onClick = onVoiceSearchClick)
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = "Microphone",
                    tint = ElectricBlue,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Say a command...",
                    color = TextSecondary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 3. Quick Actions: [TV] [AC] [Lights]
            Text(
                text = "Quick Actions",
                color = TextSecondary,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(10.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(quickActions) { action ->
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(SurfaceDark)
                            .border(1.dp, Color(0xFF334155), RoundedCornerShape(12.dp))
                            .clickable {}
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = action.icon,
                            contentDescription = action.title,
                            tint = action.color,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = action.title,
                            color = TextPrimaryDark,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 4. My Devices Header
            Text(
                text = "My Devices",
                color = TextPrimaryDark,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(12.dp))

            // 5. My Devices List
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                // Card 1: 📺 Living Room TV (🟢) - Samsung Smart TV - Last used: 5 min ago - [Control Now →]
                item {
                    DeviceListItemCard(
                        title = "Living Room TV",
                        subtitle = "Samsung Smart TV",
                        statusText = "Last used: 5 min ago",
                        isOnline = true,
                        icon = Icons.Default.Tv,
                        gradientColors = listOf(DeepBlue, ElectricBlue),
                        buttonText = "Control Now →",
                        onButtonClick = {
                            val tv = state.devices.firstOrNull { it.type == DeviceType.TV }
                            if (tv != null) onDeviceClick(tv)
                        }
                    )
                }

                // Card 2: 📡 Airtel Xstream (🟢) - Channel: Star Plus - [Control Now →]
                item {
                    DeviceListItemCard(
                        title = "Airtel Xstream",
                        subtitle = "Channel: Star Plus (100)",
                        statusText = "Set-Top Box • Active",
                        isOnline = true,
                        icon = Icons.Default.SettingsInputAntenna,
                        gradientColors = listOf(Color(0xFFB91C1C), Color(0xFFEF4444)),
                        buttonText = "Control Now →",
                        onButtonClick = {
                            val box = state.devices.firstOrNull { it.type == DeviceType.SET_TOP_BOX }
                            if (box != null) onDeviceClick(box)
                        }
                    )
                }

                // Card 3: ❄️ Bedroom AC (🔴) - Currently OFF - [Turn On →]
                item {
                    DeviceListItemCard(
                        title = "Bedroom AC",
                        subtitle = "Currently OFF",
                        statusText = "Voltas 1.5 Ton Inverter",
                        isOnline = false,
                        icon = Icons.Default.AcUnit,
                        gradientColors = listOf(Color(0xFF0E7490), Color(0xFF06B6D4)),
                        buttonText = "Turn On →",
                        onButtonClick = {}
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }

        // 6. FAB Button: [ ➕ Add New Device ]
        FloatingActionButton(
            onClick = onAddNewDeviceClick,
            containerColor = AccentOrange,
            contentColor = TextPrimaryDark,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add New Device")
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Add Device",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun DeviceListItemCard(
    title: String,
    subtitle: String,
    statusText: String,
    isOnline: Boolean,
    icon: ImageVector,
    gradientColors: List<Color>,
    buttonText: String,
    onButtonClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceDark)
            .border(1.dp, Color(0xFF334155), RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Device Icon with Gradient Background
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(gradientColors))
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = title,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Text(
                            text = title,
                            color = TextPrimaryDark,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = subtitle,
                            color = TextSecondary,
                            fontSize = 14.sp
                        )
                    }
                }

                // Status Indicator (🟢 Green = On, 🔴 Red = Off)
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(if (isOnline) SuccessGreen else ErrorRed)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = statusText,
                    color = TextSecondary,
                    fontSize = 12.sp
                )

                Button(
                    onClick = onButtonClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isOnline) ElectricBlue else DeepBlue
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = buttonText,
                        color = TextPrimaryDark,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}
