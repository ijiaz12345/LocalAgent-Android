// Apple Intelligence UI - Enhanced Version
package com.localagent.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.localagent.AgentViewModel

@Composable
fun HomeScreen(vm: AgentViewModel = viewModel()) {
    val u by vm.ui.collectAsState()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF0B1220), Color(0xFF101B33), Color(0xFF0B1220))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 48.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Column {
                Text("Local Intelligence", fontSize = 30.sp, fontWeight = FontWeight.Bold,
                    color = Color.White, letterSpacing = (-0.5).sp)
                Spacer(Modifier.height(4.dp))
                Text("Your private coding agent, running on this phone",
                    fontSize = 14.sp, color = Color(0xFF8B93A7))
            }

            Row(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF16233F)).padding(18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PulsingDot(if (u.connected) Color(0xFF34D399) else Color(0xFFF87171))
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(if (u.connected) "Connected" else "Offline",
                        color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                    Text("Brain: ${u.brain} · ${u.lessons} lessons · ${u.missions} missions",
                        color = Color(0xFF8B93A7), fontSize = 12.sp)
                }
                TextButton(onClick = vm::ping) { Text("Refresh", color = Color(0xFF7FB8FF)) }
            }

            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF16233F))) {
                Column(Modifier.padding(20.dp)) {
                    Text("What should I build?", color = Color(0xFF8B93A7),
                        fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = u.input, onValueChange = vm::onInput,
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("e.g. Build a dice roller that prints five rolls",
                            color = Color(0xFF5A6478)) },
                        minLines = 3, maxLines = 6,
                        enabled = u.connected && !u.running,
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF7FB8FF),
                            unfocusedBorderColor = Color(0xFF2A3A5C),
                            focusedContainerColor = Color(0xFF0F1A30),
                            unfocusedContainerColor = Color(0xFF0F1A30),
                            cursorColor = Color(0xFF7FB8FF),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                    Spacer(Modifier.height(16.dp))
                    Box(
                        modifier = Modifier.fillMaxWidth().height(52.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                if (u.running || !u.connected || u.input.isBlank())
                                    Brush.linearGradient(listOf(Color(0xFF2A3A5C), Color(0xFF2A3A5C)))
                                else Brush.horizontalGradient(listOf(Color(0xFF4F8DFF), Color(0xFF7FB8FF)))
                            )
                            .clickable(enabled = !u.running && u.connected && u.input.isNotBlank()) { vm.run() },
                        contentAlignment = Alignment.Center
                    ) {
                        if (u.running) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp,
                                    modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(10.dp))
                                Text("Working…", color = Color.White, fontWeight = FontWeight.SemiBold)
                            }
                        } else {
                            Text("Run mission",
                                color = if (u.connected && u.input.isNotBlank()) Color.White else Color(0xFF5A6478),
                                fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }

            AnimatedVisibility(visible = u.output.isNotBlank() || u.error != null,
                enter = fadeIn() + slideInVertically { it / 3 }) {
                val isError = u.error != null
                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isError) Color(0xFF3A1420) else Color(0xFF0F2A1C))) {
                    Column(Modifier.padding(20.dp)) {
                        Text(if (isError) "✕ Failed" else "✓ Done",
                            color = if (isError) Color(0xFFF87171) else Color(0xFF34D399),
                            fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Spacer(Modifier.height(10.dp))
                        Text(u.output.ifBlank { u.error ?: "" },
                            color = Color(0xFFD6DBE6), fontSize = 13.sp, lineHeight = 20.sp)
                    }
                }
            }

            Text("One mission at a time · code saved to ~/.localagent/missions",
                color = Color(0xFF5A6478), fontSize = 11.sp,
                textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
        }
    }
}

@Composable
private fun PulsingDot(color: Color) {
    val pulse by rememberInfiniteTransition(label = "p").animateFloat(
        initialValue = 0.6f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse), label = "v")
    Box(modifier = Modifier.size(12.dp).clip(CircleShape).background(color.copy(alpha = pulse)))
}
