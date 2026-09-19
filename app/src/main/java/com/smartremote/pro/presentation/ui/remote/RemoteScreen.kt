package com.smartremote.pro.presentation.ui.remote

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartremote.pro.core.gesture.RemoteGesture
import com.smartremote.pro.core.ir.IRLearningManager
import com.smartremote.pro.domain.models.DeviceType
import com.smartremote.pro.presentation.components.ElderModeRemote
import com.smartremote.pro.presentation.theme.CyberCyan
import com.smartremote.pro.presentation.theme.DarkBackground
import com.smartremote.pro.presentation.theme.TextPrimary
import com.smartremote.pro.presentation.theme.TextSecondary
import com.smartremote.pro.presentation.viewmodels.RemoteViewModel
import kotlin.math.abs

@Composable
fun RemoteScreen(
    deviceId: String,
    viewModel: RemoteViewModel,
    learningManager: IRLearningManager,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    var showLearningDialog by remember { mutableStateOf(false) }

    LaunchedEffect(deviceId) {
        viewModel.loadDevice(deviceId)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            // Directional gesture detector (swipe up/down = vol, swipe left/right = ch)
            .pointerInput(Unit) {
                var totalDragX = 0f
                var totalDragY = 0f
                detectDragGestures(
                    onDragStart = {
                        totalDragX = 0f
                        totalDragY = 0f
                    },
                    onDragEnd = {
                        val threshold = 120f
                        if (abs(totalDragX) > abs(totalDragY)) {
                            if (totalDragX > threshold) viewModel.handleGesture(RemoteGesture.SWIPE_RIGHT)
                            else if (totalDragX < -threshold) viewModel.handleGesture(RemoteGesture.SWIPE_LEFT)
                        } else {
                            if (totalDragY > threshold) viewModel.handleGesture(RemoteGesture.SWIPE_DOWN)
                            else if (totalDragY < -threshold) viewModel.handleGesture(RemoteGesture.SWIPE_UP)
                        }
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        totalDragX += dragAmount.x
                        totalDragY += dragAmount.y
                    }
                )
            }
    ) {
        if (state.isLoading || state.currentDevice == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = CyberCyan)
            }
        } else {
            val device = state.currentDevice!!

            Column(modifier = Modifier.fillMaxSize()) {
                // Top App Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = CyberCyan
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = device.name,
                            color = TextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${device.brand} • ${device.connectionType}",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }

                // Layout Router
                if (state.isElderMode) {
                    ElderModeRemote(
                        onPower = { viewModel.sendCommand("power") },
                        onVolUp = { viewModel.sendCommand("volUp") },
                        onVolDown = { viewModel.sendCommand("volDown") },
                        onChUp = { viewModel.sendCommand("chUp") },
                        onChDown = { viewModel.sendCommand("chDown") },
                        onMute = { viewModel.sendCommand("mute") },
                        onHome = { viewModel.sendCommand("home") }
                    )
                } else {
                    when (device.type) {
                        DeviceType.AC -> {
                            AcRemoteLayout(
                                device = device,
                                temperature = state.acTemperature,
                                onAdjustTemp = { viewModel.adjustAcTemperature(it) },
                                onCommand = { viewModel.sendCommand(it) }
                            )
                        }
                        DeviceType.SOUNDBAR -> {
                            SoundbarRemoteLayout(
                                device = device,
                                onCommand = { viewModel.sendCommand(it) }
                            )
                        }
                        else -> {
                            TvRemoteLayout(
                                device = device,
                                onCommand = { viewModel.sendCommand(it) },
                                onLaunchApp = { viewModel.launchStreamingApp(it) },
                                onOpenLearningMode = { showLearningDialog = true }
                            )
                        }
                    }
                }
            }

            if (showLearningDialog) {
                IrLearningDialog(
                    learningManager = learningManager,
                    onSaveLearnedCode = { cmdName, hex ->
                        viewModel.sendCommand(hex)
                    },
                    onDismiss = { showLearningDialog = false }
                )
            }
        }
    }
}
