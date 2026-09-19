package com.smartremote.pro.presentation.ui.settings

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartremote.pro.presentation.theme.AccentOrange
import com.smartremote.pro.presentation.theme.BackgroundDark
import com.smartremote.pro.presentation.theme.ElectricBlue
import com.smartremote.pro.presentation.theme.SurfaceDark
import com.smartremote.pro.presentation.theme.TextPrimaryDark
import com.smartremote.pro.presentation.theme.TextSecondary
import com.smartremote.pro.presentation.viewmodels.SettingsViewModel

data class SettingsSection(
    val title: String,
    val items: List<String>
)

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onOpenProfiles: () -> Unit = {},
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()

    val sections = listOf(
        SettingsSection(
            title = "Devices",
            items = listOf("My Devices", "Add New Device", "Device Discovery")
        ),
        SettingsSection(
            title = "Voice Control",
            items = listOf("Language (Hindi / English)", "Voice Shortcuts", "Sensitivity")
        ),
        SettingsSection(
            title = "Appearance",
            items = listOf("Theme (Dark / Light / Auto)", "Remote Layout", "Button Size")
        ),
        SettingsSection(
            title = "Automation",
            items = listOf("Macros", "Schedules", "Routines")
        ),
        SettingsSection(
            title = "Family & Sharing",
            items = listOf("Family Profiles", "Parental Controls", "Guest Access")
        ),
        SettingsSection(
            title = "Premium",
            items = listOf("Upgrade to Pro", "Manage Subscription", "Restore Purchase")
        ),
        SettingsSection(
            title = "About",
            items = listOf("Help & Support", "Privacy Policy", "Version 1.0.0")
        )
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Top Bar: ⬅️ Settings
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimaryDark)
            }
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Settings",
                color = TextPrimaryDark,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        sections.forEach { section ->
            Text(
                text = section.title,
                color = if (section.title == "Premium") AccentOrange else ElectricBlue,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp, start = 4.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(SurfaceDark)
            ) {
                Column {
                    section.items.forEachIndexed { index, item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    if (item == "Family Profiles") onOpenProfiles()
                                }
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = item,
                                color = TextPrimaryDark,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Normal
                            )
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = "Open",
                                tint = TextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        if (index < section.items.size - 1) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(1.dp)
                                    .background(BackgroundDark)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}
