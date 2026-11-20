package com.localagent.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

private data class Suggestion(val id: String, val label: String, val prompt: String)

@Composable
fun HomeScreen(
    darkTheme: Boolean,
    onToggleTheme: () -> Unit,
    onOpenTerminal: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    LaunchedEffect(state.output, state.errorMessage, state.running) {
        if (state.output.isNotBlank() || state.errorMessage != null || state.running)
            scrollState.animateScrollTo(scrollState.maxValue)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .imePadding()
                .padding(horizontal = 20.dp, vertical = 36.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                BrandMark(44.dp, 22.sp)
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text("Local Agent", color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text("${state.brain} · ${state.lessons} درس · ${state.missions} مهمة",
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                        fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                IconButton(onClick = onOpenTerminal) {
                    Icon(Icons.Default.Terminal, contentDescription = "Terminal",
                        tint = MaterialTheme.colorScheme.primary)
                }
                val dotColor by animateColorAsState(
                    if (state.connected) Color(0xFF4ADE80) else Color(0xFFF87171),
                    tween(450), label = "dot")
                PressSurface(onClick = { viewModel.ping() }) {
                    Row(Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(8.dp).clip(CircleShape).background(dotColor))
                        Spacer(Modifier.width(6.dp))
                        Text(if (state.connected) "متصل" else "غير متصل",
                            color = MaterialTheme.colorScheme.onBackground, fontSize = 12.sp)
                    }
                }
                Spacer(Modifier.width(6.dp))
                PressSurface(onClick = onToggleTheme) {
                    Box(Modifier.size(36.dp).clip(CircleShape),
                        contentAlignment = Alignment.Center) {
                        Text(if (darkTheme) "☀" else "☾",
                            color = MaterialTheme.colorScheme.primary, fontSize = 16.sp)
                    }
                }
            }

            Text("ماذا تريد أن تنشئ اليوم؟",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 26.sp, lineHeight = 33.sp, fontWeight = FontWeight.Bold)
            Text("اكتب مهمتك، ودع مساعدك يبدأ العمل.",
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f), fontSize = 14.sp)

            // Suggestions
            val suggestions = remember {
                listOf(
                    Suggestion("code", "كود", "أنشئ برنامج Python لإدارة المهام"),
                    Suggestion("game", "لعبة", "أنشئ لعبة تخمين رقم من 1 إلى 100"),
                    Suggestion("learn", "شرح", "اشرح أساسيات البرمجة للمبتدئين"),
                    Suggestion("write", "كتابة", "اكتب خطة تعلم البرمجة خلال 30 يوماً")
                )
            }
            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                items(suggestions, key = { it.id }, contentType = { "suggestion" }) { s ->
                    PressSurface(onClick = { viewModel.onInputChanged(s.prompt) }) {
                        Column(Modifier.padding(horizontal = 14.dp, vertical = 11.dp)) {
                            Text(s.label, color = MaterialTheme.colorScheme.onBackground,
                                fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                            Spacer(Modifier.height(3.dp))
                            Text("استخدم ↗",
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.55f),
                                fontSize = 11.sp)
                        }
                    }
                }
            }

            // Error Banner
            AnimatedVisibility(
                visible = state.errorMessage != null,
                enter = fadeIn(tween(200)) + slideInVertically(tween(250)) { it / 4 },
                exit = fadeOut(tween(150))
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = state.errorMessage ?: "",
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            fontSize = 13.sp
                        )
                        if (state.errorKind?.let { it == com.localagent.domain.model.AgentErrorKind.NETWORK || it == com.localagent.domain.model.AgentErrorKind.SERVER } == true) {
                            OutlinedButton(
                                onClick = { viewModel.run() },
                                modifier = Modifier.align(Alignment.End)
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("إعادة المحاولة", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            // Input
            OutlinedTextField(
                value = state.input, onValueChange = viewModel::onInputChanged,
                modifier = Modifier.fillMaxWidth(),
                enabled = state.connected && !state.running,
                placeholder = { Text("مثال: أنشئ لعبة تخمين أرقام بلغة Python",
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.45f)) },
                minLines = 3, maxLines = 6,
                shape = RoundedCornerShape(20.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f),
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    cursorColor = MaterialTheme.colorScheme.primary,
                    focusedTextColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedTextColor = MaterialTheme.colorScheme.onBackground
                )
            )

            // Run button
            val canRun = state.connected && !state.running && state.input.isNotBlank()
            Button(
                onClick = { viewModel.run() },
                enabled = canRun,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    disabledContainerColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.12f)
                )
            ) {
                if (state.running) {
                    CircularProgressIndicator(Modifier.size(18.dp), color = Color.White, strokeWidth = 2.dp)
                    Spacer(Modifier.width(10.dp))
                    Text(if (state.retrying) "إعادة المحاولة ${state.retryAttempt}..." else "جارٍ التنفيذ…", fontWeight = FontWeight.SemiBold)
                } else {
                    Text("ابدأ المهمة  ↗", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                }
            }

            // Result
            AnimatedVisibility(
                visible = state.output.isNotBlank() && state.errorMessage == null,
                enter = fadeIn(tween(300)) + slideInVertically(tween(350)) { it / 5 }
            ) {
                Row(verticalAlignment = Alignment.Top) {
                    BrandMark(28.dp, 14.sp)
                    Spacer(Modifier.width(8.dp))
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(topStart = 4.dp, topEnd = 18.dp,
                            bottomEnd = 18.dp, bottomStart = 18.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = state.output,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 14.sp, lineHeight = 21.sp,
                            modifier = Modifier.padding(14.dp)
                        )
                    }
                }
            }

            Text("تُحفظ مهامك محلياً على جهازك",
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                fontSize = 11.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
        }
    }
}

@Composable
private fun PressSurface(onClick: () -> Unit, content: @Composable () -> Unit) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(if (pressed) 0.97f else 1f, label = "press")
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.graphicsLayer { scaleX = scale; scaleY = scale }
    ) {
        Box(modifier = Modifier.clip(RoundedCornerShape(14.dp))) {
            androidx.compose.foundation.clickable(
                interactionSource = interaction, indication = null, onClick = onClick
            ) { content() }
        }
    }
}

@Composable
private fun BrandMark(size: androidx.compose.ui.unit.Dp, glyph: androidx.compose.ui.unit.TextUnit) {
    val transition = rememberInfiniteTransition(label = "brand-glow")
    val glow by transition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1600),
            repeatMode = RepeatMode.Reverse
        ),
        label = "brand-glow-alpha"
    )
    Box(
        modifier = Modifier
            .size(size)
            .graphicsLayer { scaleX = glow; scaleY = glow }
            .clip(CircleShape)
            .background(Brush.linearGradient(
                listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary))),
        contentAlignment = Alignment.Center
    ) { Text("✦", color = Color.White, fontSize = glyph, fontWeight = FontWeight.Bold) }
}
