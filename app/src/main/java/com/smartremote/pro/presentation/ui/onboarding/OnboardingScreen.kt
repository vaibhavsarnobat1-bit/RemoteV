package com.smartremote.pro.presentation.ui.onboarding

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartremote.pro.presentation.theme.AccentOrange
import com.smartremote.pro.presentation.theme.BackgroundDark
import com.smartremote.pro.presentation.theme.DeepBlue
import com.smartremote.pro.presentation.theme.ElectricBlue
import com.smartremote.pro.presentation.theme.SurfaceDark
import com.smartremote.pro.presentation.theme.TextPrimaryDark
import com.smartremote.pro.presentation.theme.TextSecondary
import kotlinx.coroutines.launch

data class OnboardingStep(
    val title: String,
    val subtext: String,
    val icon: ImageVector
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    onFinish: () -> Unit
) {
    val steps = listOf(
        OnboardingStep(
            title = "Your Universal Remote",
            subtext = "Control all devices from one app",
            icon = Icons.Default.Tv
        ),
        OnboardingStep(
            title = "Voice Control in Hindi & English",
            subtext = "Just say 'TV chalu karo'",
            icon = Icons.Default.Mic
        ),
        OnboardingStep(
            title = "Smart Automation",
            subtext = "Create one-tap shortcuts for everything",
            icon = Icons.Default.AutoAwesome
        )
    )

    val pagerState = rememberPagerState(pageCount = { steps.size })
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Skip Button (Top-Right)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            if (pagerState.currentPage < steps.size - 1) {
                TextButton(onClick = onFinish) {
                    Text("Skip", color = TextSecondary, fontSize = 14.sp)
                }
            } else {
                Spacer(modifier = Modifier.height(48.dp))
            }
        }

        // Pager Section
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->
            val step = steps[page]
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                // Hero Illustration Placeholder with Deep Blue glow
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(160.dp)
                        .clip(CircleShape)
                        .background(DeepBlue.copy(alpha = 0.4f))
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(DeepBlue)
                    ) {
                        Icon(
                            imageVector = step.icon,
                            contentDescription = step.title,
                            tint = ElectricBlue,
                            modifier = Modifier.size(64.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                Text(
                    text = step.title,
                    color = TextPrimaryDark,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = step.subtext,
                    color = TextSecondary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
            }
        }

        // Bottom Navigation with Pager Dots and Buttons
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Dots indicator
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                for (i in steps.indices) {
                    val isSelected = pagerState.currentPage == i
                    Box(
                        modifier = Modifier
                            .size(if (isSelected) 24.dp else 8.dp, 8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(if (isSelected) ElectricBlue else SurfaceDark)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Buttons according to screen step
            when (pagerState.currentPage) {
                0 -> {
                    // Screen 1: Next button
                    Button(
                        onClick = {
                            coroutineScope.launch { pagerState.animateScrollToPage(1) }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Next", color = TextPrimaryDark, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                    }
                }
                1 -> {
                    // Screen 2: Back and Next buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                coroutineScope.launch { pagerState.animateScrollToPage(0) }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Back", color = TextSecondary, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                        }
                        Button(
                            onClick = {
                                coroutineScope.launch { pagerState.animateScrollToPage(2) }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Next", color = TextPrimaryDark, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }
                2 -> {
                    // Screen 3: Get Started button
                    Button(
                        onClick = onFinish,
                        colors = ButtonDefaults.buttonColors(containerColor = AccentOrange),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Get Started", color = TextPrimaryDark, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }
    }
}
