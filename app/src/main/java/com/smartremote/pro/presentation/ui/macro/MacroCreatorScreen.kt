package com.smartremote.pro.presentation.ui.macro

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartremote.pro.presentation.theme.AccentOrange
import com.smartremote.pro.presentation.theme.BackgroundDark
import com.smartremote.pro.presentation.theme.ElectricBlue
import com.smartremote.pro.presentation.theme.ErrorRed
import com.smartremote.pro.presentation.theme.SurfaceDark
import com.smartremote.pro.presentation.theme.TextPrimaryDark
import com.smartremote.pro.presentation.theme.TextSecondary

data class MacroActionItem(
    val title: String,
    val deviceName: String
)

@Composable
fun MacroCreatorScreen(
    onBack: () -> Unit,
    onSave: (name: String, voiceTrigger: String, actions: List<MacroActionItem>) -> Unit,
    modifier: Modifier = Modifier
) {
    var macroName by remember { mutableStateOf("Movie Night") }
    var voiceTrigger by remember { mutableStateOf("movie chalao") }
    var selectedIconIndex by remember { mutableStateOf(0) }

    val icons = listOf("🎬", "📺", "🍿", "🎭")

    val actions = remember {
        mutableListOf(
            MacroActionItem("Turn on TV", "Living Room TV"),
            MacroActionItem("Open Netflix", "Living Room TV"),
            MacroActionItem("Set Volume 30%", "Living Room TV")
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Top Bar: ⬅️ Create Macro
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimaryDark)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Create Macro",
                color = TextPrimaryDark,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Macro Name Input
        Text(
            text = "Macro Name:",
            color = TextSecondary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = macroName,
            onValueChange = { macroName = it },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Actions Sequence
        Text(
            text = "Actions Sequence:",
            color = TextPrimaryDark,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(12.dp))

        actions.forEachIndexed { index, action ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(SurfaceDark)
                    .border(1.dp, Color(0xFF334155), RoundedCornerShape(12.dp))
                    .padding(14.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${index + 1}. ${action.title}",
                            color = TextPrimaryDark,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Text(
                        text = "Device: ${action.deviceName}",
                        color = TextSecondary,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(top = 2.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = {}) {
                            Text("Edit", color = ElectricBlue, fontSize = 12.sp)
                        }
                        TextButton(onClick = {}) {
                            Text("Remove", color = ErrorRed, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // [+ Add Action]
        Button(
            onClick = {},
            colors = ButtonDefaults.buttonColors(containerColor = SurfaceDark),
            border = androidx.compose.foundation.BorderStroke(1.dp, ElectricBlue),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add", tint = ElectricBlue)
            Spacer(modifier = Modifier.width(6.dp))
            Text("Add Action", color = ElectricBlue, fontSize = 14.sp, fontWeight = FontWeight.Medium)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Voice Trigger (Optional)
        Text(
            text = "Voice Trigger (Optional):",
            color = TextSecondary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = voiceTrigger,
            onValueChange = { voiceTrigger = it },
            leadingIcon = { Icon(Icons.Default.Mic, contentDescription = "Voice", tint = AccentOrange) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Icon Picker: [🎬] [📺] [🍿] [🎭]
        Text(
            text = "Icon:",
            color = TextSecondary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            icons.forEachIndexed { idx, emoji ->
                val isSelected = selectedIconIndex == idx
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(50.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) AccentOrange.copy(alpha = 0.2f) else SurfaceDark)
                        .border(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) AccentOrange else Color(0xFF334155),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable { selectedIconIndex = idx }
                ) {
                    Text(emoji, fontSize = 24.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(36.dp))

        // Bottom Actions: [Cancel]  [Save Macro]
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Cancel", color = TextSecondary, fontSize = 15.sp)
            }

            Button(
                onClick = { onSave(macroName, voiceTrigger, actions) },
                colors = ButtonDefaults.buttonColors(containerColor = AccentOrange),
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Save Macro", color = TextPrimaryDark, fontSize = 15.sp, fontWeight = FontWeight.Medium)
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}
