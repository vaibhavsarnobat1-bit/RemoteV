package com.smartremote.pro.presentation.ui.voice

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartremote.pro.presentation.theme.AccentOrange
import com.smartremote.pro.presentation.theme.BackgroundDark
import com.smartremote.pro.presentation.theme.DeepBlue
import com.smartremote.pro.presentation.theme.ElectricBlue
import com.smartremote.pro.presentation.theme.SuccessGreen
import com.smartremote.pro.presentation.theme.SurfaceDark
import com.smartremote.pro.presentation.theme.TextPrimaryDark
import com.smartremote.pro.presentation.theme.TextSecondary
import com.smartremote.pro.presentation.theme.WarningYellow
import com.smartremote.pro.presentation.viewmodels.VoiceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoiceControlSheet(
    viewModel: VoiceViewModel,
    onDismiss: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val infiniteTransition = rememberInfiniteTransition(label = "pulse_mic")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = if (state.isListening) 1.2f else 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = BackgroundDark
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header: 🎤 Voice Control
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Mic, contentDescription = "Voice", tint = ElectricBlue)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Voice Control",
                    color = TextPrimaryDark,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Microphone Animation Card: [🎙️ Listening]
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(110.dp)
                    .scale(pulseScale)
                    .clip(CircleShape)
                    .background(DeepBlue.copy(alpha = 0.5f))
                    .border(2.dp, ElectricBlue, CircleShape)
            ) {
                IconButton(
                    onClick = {
                        if (state.isListening) viewModel.stopListening() else viewModel.startListening()
                    },
                    modifier = Modifier.size(90.dp)
                ) {
                    Icon(
                        imageVector = if (state.isListening) Icons.Default.Stop else Icons.Default.Mic,
                        contentDescription = "Mic",
                        tint = if (state.isListening) AccentOrange else ElectricBlue,
                        modifier = Modifier.size(46.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = if (state.isListening) "Listening..." else "Tap to Speak",
                color = if (state.isListening) ElectricBlue else TextSecondary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Detected Text: "TV chalu karo"
            Text(
                text = if (state.recognizedText.isNotBlank()) "\"${state.recognizedText}\"" else "\"TV chalu karo\"",
                color = WarningYellow,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Feedback: ✅ TV Turned On
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(SuccessGreen.copy(alpha = 0.15f))
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Icon(Icons.Default.CheckCircle, contentDescription = "Success", tint = SuccessGreen, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (state.feedbackMessage.isNotBlank()) state.feedbackMessage else "TV Turned On",
                    color = SuccessGreen,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Quick Voice Commands Box
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(SurfaceDark)
                    .padding(16.dp)
            ) {
                Text(
                    text = "Quick Voice Commands:",
                    color = TextPrimaryDark,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))
                listOf(
                    "• \"Volume badao\"",
                    "• \"Channel 25 lagao\"",
                    "• \"YouTube kholo\"",
                    "• \"Mute karo\""
                ).forEach { cmd ->
                    Text(
                        text = cmd,
                        color = TextSecondary,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // My Shortcuts Box
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(SurfaceDark)
                    .padding(16.dp)
            ) {
                Text(
                    text = "My Shortcuts:",
                    color = TextPrimaryDark,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(10.dp))

                // Shortcut 1: 🎬 "Movie time" -> TV on + Netflix + Vol 30
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(BackgroundDark)
                        .padding(10.dp)
                ) {
                    Text("🎬 \"Movie time\"", color = AccentOrange, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    Text("➡️ TV on + Netflix + Vol 30", color = TextSecondary, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Shortcut 2: 🏏 "Cricket mode" -> Sports + Volume max
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(BackgroundDark)
                        .padding(10.dp)
                ) {
                    Text("🏏 \"Cricket mode\"", color = ElectricBlue, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    Text("➡️ Sports channel + Volume max", color = TextSecondary, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(12.dp))

                // [+ Create New Shortcut]
                Button(
                    onClick = {},
                    colors = ButtonDefaults.buttonColors(containerColor = SurfaceDark),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ElectricBlue),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add", tint = ElectricBlue, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Create New Shortcut", color = ElectricBlue, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
