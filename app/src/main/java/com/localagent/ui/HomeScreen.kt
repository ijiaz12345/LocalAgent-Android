package com.localagent.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.localagent.AgentViewModel
import kotlinx.coroutines.launch

private data class Suggestion(val id: String, val label: String, val prompt: String)

@Composable
fun HomeScreen(
    vm: AgentViewModel = viewModel(),
    onToggleTheme: (kotlinx.coroutines.CoroutineScope) -> Unit = {}
) {
    val u by vm.ui.collectAsState()
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    LaunchedEffect(u.output, u.error, u.running) {
        if (u.output.isNotBlank() || u.error != null || u.running)
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
            // ===== Header =====
            Row(verticalAlignment = Alignment.CenterVertically) {
                BrandMark(44.dp, 22.sp)
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text("Local Agent", color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text("${u.brain} · ${u.lessons} درس",
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                        fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                val dotColor by animateColorAsState(
                    if (u.connected) Color(0xFF4ADE80) else Color(0xFFF87171),
                    tween(450), label = "dot")
                PressSurface(onClick = { vm.ping() }) {
                    Row(Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(8.dp).clip(CircleShape).background(dotColor))
                        Spacer(Modifier.width(6.dp))
                        Text(if (u.connected) "متصل" else "غير متصل",
                            color = MaterialTheme.colorScheme.onBackground, fontSize = 12.sp)
                    }
                }
                Spacer(Modifier.width(6.dp))
                PressSurface(onClick = { onToggleTheme(scope) }) {
                    Box(Modifier.size(36.dp).clip(CircleShape),
                        contentAlignment = Alignment.Center) {
                        Text(if (u.connected) "☀" else "☾",
                            color = MaterialTheme.colorScheme.primary, fontSize = 16.sp)
                    }
                }
            }

            Text("ماذا تريد أن تنشئ اليوم؟",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 26.sp, lineHeight = 33.sp, fontWeight = FontWeight.Bold)
            Text("اكتب مهمتك، ودع مساعدك يبدأ العمل.",
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f), fontSize = 14.sp)

            // ===== Suggestions (LazyRow + key + contentType) =====
            val suggestions = remember {
                listOf(
                    Suggestion("code", "كود", "أنشئ برنامج Python لإدارة المهام مع حفظها في ملف."),
                    Suggestion("game", "لعبة", "أنشئ لعبة تخمين رقم من 1 إلى 100 بلغة Python."),
                    Suggestion("learn", "شرح", "اشرح لي أساسيات البرمجة للمبتدئين مع أمثلة."),
                    Suggestion("write", "كتابة", "اكتب لي خطة تعلم البرمجة خلال 30 يوماً.")
                )
            }
            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                items(suggestions, key = { it.id }, contentType = { "suggestion" }) { s ->
                    PressSurface(onClick = { vm.onInput(s.prompt) }) {
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

            // ===== Input =====
            OutlinedTextField(
                value = u.input, onValueChange = vm::onInput,
                modifier = Modifier.fillMaxWidth(),
                enabled = u.connected && !u.running,
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

            // ===== Run button =====
            val canRun = u.connected && !u.running && u.input.isNotBlank()
            Button(
                onClick = { vm.run() },
                enabled = canRun,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    disabledContainerColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.12f)
                )
            ) {
                if (u.running) {
                    CircularProgressIndicator(Modifier.size(18.dp), color = Color.White, strokeWidth = 2.dp)
                    Spacer(Modifier.width(10.dp))
                    Text("جارٍ التنفيذ…", fontWeight = FontWeight.SemiBold)
                } else {
                    Text("ابدأ المهمة  ↗", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                }
            }

            // ===== Result bubble =====
            AnimatedContent(targetState = u.output.isNotBlank() || u.error != null, label = "result") { show ->
                if (show) {
                    val isError = u.error != null
                    Row(verticalAlignment = Alignment.Top) {
                        BrandMark(28.dp, 14.sp)
                        Spacer(Modifier.width(8.dp))
                        Surface(
                            color = if (isError) MaterialTheme.colorScheme.errorContainer
                                    else MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(topStart = 4.dp, topEnd = 18.dp,
                                bottomEnd = 18.dp, bottomStart = 18.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = u.output.ifBlank { u.error ?: "" },
                                color = if (isError) MaterialTheme.colorScheme.onErrorContainer
                                        else MaterialTheme.colorScheme.onSurface,
                                fontSize = 14.sp, lineHeight = 21.sp,
                                modifier = Modifier.padding(14.dp)
                            )
                        }
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
    val scale by animateFloatAsState(if (pressed) 0.97f else 1f,
        label = "press")
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .clip(RoundedCornerShape(14.dp))
    ) {
        Box(Modifier.padding(0.dp)) {
            androidx.compose.foundation.clickable(
                interactionSource = interaction, indication = null, onClick = onClick
            ) { content() }
        }
    }
}

@Composable
private fun BrandMark(size: androidx.compose.ui.unit.Dp, glyph: androidx.compose.ui.unit.TextUnit) {
    val transition = rememberInfiniteTransitionLocal()
    val glow by transition.animateFloat(0.8f, 1f, label = "glow")
    Box(
        modifier = Modifier
            .size(size)
            .graphicsLayer { scaleX = glow; scaleY = glow }
            .clip(CircleShape)
            .background(androidx.compose.ui.graphics.Brush.linearGradient(
                listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary))),
        contentAlignment = Alignment.Center
    ) { Text("✦", color = Color.White, fontSize = glyph, fontWeight = FontWeight.Bold) }
}

@Composable
private fun rememberInfiniteTransitionLocal(): androidx.compose.animation.core.InfiniteTransition =
    remember { androidx.compose.animation.core.rememberInfiniteTransition("brand") }
