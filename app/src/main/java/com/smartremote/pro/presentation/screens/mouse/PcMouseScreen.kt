package com.smartremote.pro.presentation.screens.mouse

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartremote.designsystem.theme.*
import com.smartremote.pro.core.mouse.PcMouseController

enum class PcControlMode {
    TOUCHPAD, AIR_MOUSE, PPT_CLICKER
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PcMouseScreen(
    controller: PcMouseController,
    onNavigateBack: () -> Unit
) {
    var currentMode by remember { mutableStateOf(PcControlMode.TOUCHPAD) }
    var textInput by remember { mutableStateOf("") }
    val haptic = LocalHapticFeedback.current
    val isConnected by controller.isConnected.collectAsState()
    val isAirMouseActive by controller.isAirMouseActive.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "💻 Laptop / PC Controller",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (isConnected) "WiFi: 192.168.1.10 (3ms) • Connected" else "Bluetooth HID Ready",
                            fontSize = 11.sp,
                            color = if (isConnected) SuccessGreen else TextSecondary
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { controller.connectToPc("192.168.1.10") }) {
                        Icon(Icons.Default.Wifi, contentDescription = "Reconnect")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Mode Selector Tabs: Touchpad | Air Mouse | PPT Clicker
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                PcControlMode.values().forEach { mode ->
                    val isSelected = currentMode == mode
                    val title = when (mode) {
                        PcControlMode.TOUCHPAD -> "👆 Finger Move"
                        PcControlMode.AIR_MOUSE -> "📱 Mobile Move"
                        PcControlMode.PPT_CLICKER -> "📊 PPT Clicker"
                    }
                    val subtitle = when (mode) {
                        PcControlMode.TOUCHPAD -> "उँगली से"
                        PcControlMode.AIR_MOUSE -> "हवा में हिलाएं"
                        PcControlMode.PPT_CLICKER -> "स्लाइड्स"
                    }
                    Button(
                        onClick = {
                            currentMode = mode
                            if (mode == PcControlMode.AIR_MOUSE) {
                                controller.toggleAirMouse(true)
                            } else {
                                controller.toggleAirMouse(false)
                            }
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSelected) ElectricBlue else Color.Transparent,
                            contentColor = if (isSelected) Color.White else TextSecondary
                        ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(vertical = 6.dp, horizontal = 2.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = title, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text(text = subtitle, fontSize = 9.sp, fontWeight = FontWeight.Normal, opacity = 0.8f)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            when (currentMode) {
                PcControlMode.TOUCHPAD -> {
                    // Touchpad Surface with Scroll Strip
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Multi-touch Touchpad
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(16.dp))
                                .background(MaterialTheme.colorScheme.surface)
                                .border(1.5.dp, BorderDark, RoundedCornerShape(16.dp))
                                .pointerInput(Unit) {
                                    detectDragGestures { change, dragAmount ->
                                        change.consume()
                                        controller.moveCursor(
                                            (dragAmount.x * 1.8f).toInt(),
                                            (dragAmount.y * 1.8f).toInt()
                                        )
                                    }
                                }
                                .pointerInput(Unit) {
                                    detectTapGestures(
                                        onTap = {
                                            controller.clickMouse("left")
                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        },
                                        onDoubleTap = {
                                            controller.clickMouse("left")
                                            controller.clickMouse("left")
                                        },
                                        onLongPress = {
                                            controller.clickMouse("right")
                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        }
                                    )
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "☝️ 1-Finger: Move / Tap: Left Click",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                                Text(
                                    text = "✌️ 2-Finger / Long Press: Right Click",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }
                        }

                        // Scroll Strip
                        Box(
                            modifier = Modifier
                                .width(44.dp)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(16.dp))
                                .background(MaterialTheme.colorScheme.surface)
                                .border(1.dp, BorderDark, RoundedCornerShape(16.dp))
                                .pointerInput(Unit) {
                                    detectDragGestures { change, dragAmount ->
                                        change.consume()
                                        val delta = if (dragAmount.y > 0) -1 else 1
                                        controller.scrollWheel(delta)
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                modifier = Modifier.fillMaxHeight(),
                                verticalArrangement = Arrangement.SpaceBetween,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("▲", fontSize = 10.sp, color = TextSecondary, modifier = Modifier.padding(top = 10.dp))
                                Text(
                                    "SCROLL",
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ElectricBlue
                                )
                                Text("▼", fontSize = 10.sp, color = TextSecondary, modifier = Modifier.padding(bottom = 10.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Left, Middle, Right Click Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                controller.clickMouse("left")
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            },
                            modifier = Modifier
                                .weight(2f)
                                .height(52.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Text("🖱️ Left Click", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                controller.clickMouse("middle")
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Text("⚙️ Mid", color = MaterialTheme.colorScheme.onSurface)
                        }

                        Button(
                            onClick = {
                                controller.clickMouse("right")
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            },
                            modifier = Modifier
                                .weight(2f)
                                .height(52.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Text("🖱️ Right Click", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Quick Shortcuts Bar (Win, Alt+Tab, Ctrl+C, Ctrl+V, Esc, Enter)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        val shortcuts = listOf("win" to "⊞ Win", "alt_tab" to "Alt+Tab", "ctrl_c" to "Ctrl+C", "ctrl_v" to "Ctrl+V", "esc" to "Esc", "enter" to "↵")
                        shortcuts.forEach { (key, label) ->
                            OutlinedButton(
                                onClick = {
                                    controller.sendShortcutKey(key)
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(38.dp),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text(label, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Text input field for typing
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = textInput,
                            onValueChange = { textInput = it },
                            placeholder = { Text("Type to send keys to laptop...", fontSize = 12.sp) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )
                        Button(
                            onClick = {
                                if (textInput.isNotBlank()) {
                                    controller.sendText(textInput)
                                    textInput = ""
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue)
                        ) {
                            Text("Send")
                        }
                    }
                }

                PcControlMode.AIR_MOUSE -> {
                    // Air Mouse Gyroscope Stage
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .clip(RoundedCornerShape(20.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .border(1.dp, BorderDark, RoundedCornerShape(20.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(CircleShape)
                                    .background(ElectricBlue.copy(alpha = 0.2f))
                                    .border(2.dp, ElectricBlue, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("🎯", fontSize = 36.sp)
                            }
                            Spacer(modifier = Modifier.height(20.dp))
                            Text(
                                "Wave your phone in the air to move cursor",
                                fontSize = 13.sp,
                                color = TextSecondary,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { controller.clickMouse("left") },
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Text("🖱️ Left Click", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
                        }
                        Button(
                            onClick = { controller.clickMouse("right") },
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Text("🖱️ Right Click", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                PcControlMode.PPT_CLICKER -> {
                    // Presentation Clicker Mode
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("PowerPoint & Google Slides", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text("⏱️ Slide Time: 02:45", fontSize = 12.sp, color = AccentOrange)
                        }

                        // Giant Prev and Next Slide Buttons
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Button(
                                onClick = {
                                    controller.sendPptCommand("prev")
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight(),
                                shape = RoundedCornerShape(20.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("◀", fontSize = 36.sp, color = MaterialTheme.colorScheme.onSurface)
                                    Text("PREV SLIDE", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                }
                            }

                            Button(
                                onClick = {
                                    controller.sendPptCommand("next")
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight(),
                                shape = RoundedCornerShape(20.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue)
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("▶", fontSize = 36.sp, color = Color.White)
                                    Text("NEXT SLIDE", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }
                        }

                        // PPT Quick Actions (Start F5, Black B, Laser, Exit)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { controller.sendPptCommand("f5") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("▶️ F5")
                            }
                            OutlinedButton(
                                onClick = { controller.sendPptCommand("black") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("⬛ Black")
                            }
                            OutlinedButton(
                                onClick = { controller.sendPptCommand("esc") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("⏹️ Exit")
                            }
                        }
                    }
                }
            }
        }
    }
}
