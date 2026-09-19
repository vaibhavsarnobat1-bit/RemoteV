package com.smartremote.pro.presentation.ui.macro

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartremote.pro.domain.models.Macro
import com.smartremote.pro.presentation.theme.CardDark
import com.smartremote.pro.presentation.theme.CyberCyan
import com.smartremote.pro.presentation.theme.DarkBackground
import com.smartremote.pro.presentation.theme.ElectricViolet
import com.smartremote.pro.presentation.theme.NeonAmber
import com.smartremote.pro.presentation.theme.RemoteButtonBorder
import com.smartremote.pro.presentation.theme.TextPrimary
import com.smartremote.pro.presentation.theme.TextSecondary
import com.smartremote.pro.presentation.viewmodels.MacroViewModel

@Composable
fun MacroScreen(
    viewModel: MacroViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    var showEditor by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(horizontal = 16.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(modifier = Modifier.height(16.dp))

            // Title
            Text(
                text = "Macro Automation",
                color = CyberCyan,
                fontSize = 24.sp,
                fontWeight = FontWeight.Black
            )
            Text(
                text = "One-tap sequential routines for all your devices",
                color = TextSecondary,
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Progress Banner during Macro Execution
            if (state.isExecuting) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(CardDark)
                        .border(1.dp, NeonAmber, RoundedCornerShape(14.dp))
                        .padding(16.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Running Step ${state.currentStepIndex} of ${state.totalSteps}...",
                                color = NeonAmber,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = NeonAmber,
                                strokeWidth = 2.dp
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = if (state.totalSteps > 0) state.currentStepIndex.toFloat() / state.totalSteps.toFloat() else 0f,
                            color = NeonAmber,
                            trackColor = CardDark,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Macro Cards List
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(state.macros) { macro ->
                    MacroItemCard(
                        macro = macro,
                        isExecuting = state.executingMacroId == macro.id,
                        onRun = { viewModel.runMacro(macro) }
                    )
                }
            }
        }
    }
}

@Composable
private fun MacroItemCard(
    macro: Macro,
    isExecuting: Boolean,
    onRun: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CardDark)
            .border(1.dp, RemoteButtonBorder, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(ElectricViolet.copy(alpha = 0.2f))
                ) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = "Macro",
                        tint = ElectricViolet,
                        modifier = Modifier.size(26.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = macro.name,
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = macro.description,
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                    if (macro.voiceTrigger != null) {
                        Text(
                            text = "Voice: \"${macro.voiceTrigger}\"",
                            color = CyberCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Button(
                onClick = onRun,
                enabled = !isExecuting,
                colors = ButtonDefaults.buttonColors(containerColor = CyberCyan),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Run",
                    tint = DarkBackground,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "RUN",
                    color = DarkBackground,
                    fontWeight = FontWeight.Black,
                    fontSize = 12.sp
                )
            }
        }
    }
}
