package com.smartremote.pro.presentation.ui.profiles

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.smartremote.pro.domain.models.FamilyProfile
import com.smartremote.pro.domain.models.ProfileRole
import com.smartremote.pro.presentation.theme.CardDark
import com.smartremote.pro.presentation.theme.CyberCyan
import com.smartremote.pro.presentation.theme.DarkBackground
import com.smartremote.pro.presentation.theme.GreenSuccess
import com.smartremote.pro.presentation.theme.NeonAmber
import com.smartremote.pro.presentation.theme.PowerRed
import com.smartremote.pro.presentation.theme.TextPrimary
import com.smartremote.pro.presentation.theme.TextSecondary
import com.smartremote.pro.presentation.viewmodels.ProfileViewModel

@Composable
fun ProfileManagementDialog(
    viewModel: ProfileViewModel,
    onDismiss: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    var enteredPin by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(CardDark)
                .padding(20.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Family Profiles",
                    color = CyberCyan,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Switch profiles or activate Elder Mode",
                    color = TextSecondary,
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Profiles List
                state.profiles.forEach { profile ->
                    val isActive = profile.id == state.activeProfile?.id
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isActive) CyberCyan.copy(alpha = 0.15f) else DarkBackground)
                            .clickable { viewModel.requestProfileSwitch(profile) }
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(if (profile.role == ProfileRole.ELDER) NeonAmber else CyberCyan)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = profile.name,
                                    tint = DarkBackground,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = profile.name,
                                    color = TextPrimary,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (profile.isElderMode) "Elder Mode (Big Buttons)" else profile.role.name,
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        if (isActive) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Active",
                                tint = GreenSuccess
                            )
                        } else if (!profile.pinHash.isNullOrBlank()) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "PIN Locked",
                                tint = TextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                // PIN prompt modal if admin profile requires authentication
                if (state.isPinPromptVisible) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Enter Admin PIN (Default: 1234)",
                        color = NeonAmber,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = enteredPin,
                        onValueChange = { enteredPin = it },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        isError = state.pinError != null,
                        supportingText = state.pinError?.let { { Text(it, color = PowerRed) } },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { viewModel.dismissPinPrompt() }) {
                            Text("Cancel", color = TextSecondary)
                        }
                        Button(
                            onClick = {
                                viewModel.submitPin(enteredPin)
                                enteredPin = ""
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CyberCyan)
                        ) {
                            Text("Unlock", color = DarkBackground, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Close", color = CyberCyan, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
