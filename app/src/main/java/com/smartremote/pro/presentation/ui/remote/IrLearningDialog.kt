package com.smartremote.pro.presentation.ui.remote

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.SettingsRemote
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.window.Dialog
import com.smartremote.pro.core.ir.IRLearningManager
import com.smartremote.pro.core.ir.LearningState
import com.smartremote.pro.presentation.theme.CardDark
import com.smartremote.pro.presentation.theme.CyberCyan
import com.smartremote.pro.presentation.theme.GreenSuccess
import com.smartremote.pro.presentation.theme.NeonAmber
import com.smartremote.pro.presentation.theme.TextPrimary
import com.smartremote.pro.presentation.theme.TextSecondary

@Composable
fun IrLearningDialog(
    learningManager: IRLearningManager,
    onSaveLearnedCode: (commandName: String, hexCode: String) -> Unit,
    onDismiss: () -> Unit
) {
    var state by remember { mutableStateOf<LearningState>(LearningState.Idle) }
    var commandName by remember { mutableStateOf("Custom Button") }

    LaunchedEffect(Unit) {
        learningManager.startLearning().collect {
            state = it
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(CardDark)
                .padding(24.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "IR Remote Learning",
                    color = CyberCyan,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                when (val current = state) {
                    is LearningState.Listening -> {
                        CircularProgressIndicator(
                            modifier = Modifier.size(54.dp),
                            color = NeonAmber,
                            strokeWidth = 3.dp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Point your existing remote at phone\nand press the target button...",
                            color = TextPrimary,
                            fontSize = 14.sp
                        )
                    }
                    is LearningState.Success -> {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Captured",
                            tint = GreenSuccess,
                            modifier = Modifier.size(54.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Signal Captured: ${current.hexCode}",
                            color = GreenSuccess,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = commandName,
                            onValueChange = { commandName = it },
                            label = { Text("Command Name") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    else -> {}
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel", color = TextSecondary)
                    }
                    if (state is LearningState.Success) {
                        Button(
                            onClick = {
                                val hex = (state as LearningState.Success).hexCode
                                onSaveLearnedCode(commandName, hex)
                                onDismiss()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CyberCyan)
                        ) {
                            Text("Save Button", color = CardDark, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
