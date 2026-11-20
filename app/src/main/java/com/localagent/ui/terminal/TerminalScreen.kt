package com.localagent.ui.terminal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class LogLine(val text: String, val type: String = "info")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TerminalScreen(onBack: () -> Unit) {
    val logs = remember {
        mutableStateListOf(
            LogLine("> Initializing LocalAgent Terminal...", "system"),
            LogLine("> Connecting to 127.0.0.1:8765", "info"),
            LogLine("> Connection established.", "success"),
            LogLine("> Ready for commands.", "system")
        )
    }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    var isRunning by remember { mutableStateOf(false) }

    LaunchedEffect(logs.size) {
        if (logs.isNotEmpty()) listState.animateScrollToItem(logs.size - 1)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Manus Terminal", color = Color(0xFF00FF00), fontFamily = FontFamily.Monospace) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0D0D0D))
            )
        },
        containerColor = Color(0xFF0D0D0D)
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Terminal Output
            Surface(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF1A1A1A),
                shadowElevation = 4.dp
            ) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(logs) { line ->
                        Text(
                            text = line.text,
                            color = when (line.type) {
                                "error" -> Color(0xFFFF5555)
                                "success" -> Color(0xFF50FA7B)
                                "system" -> Color(0xFF8BE9FD)
                                else -> Color(0xFFF8F8F2)
                            },
                            fontFamily = FontFamily.Monospace,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            // Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = {
                        isRunning = true
                        scope.launch {
                            logs.add(LogLine("> Executing task...", "system"))
                            delay(1000)
                            logs.add(LogLine("> [OK] Task completed successfully.", "success"))
                            isRunning = false
                        }
                    },
                    enabled = !isRunning,
                    modifier = Modifier.weight(1f).height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF50FA7B), contentColor = Color.Black)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Run", fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = { logs.add(LogLine("> Stopped by user.", "error")) },
                    modifier = Modifier.weight(1f).height(48.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFF5555))
                ) {
                    Icon(Icons.Default.Stop, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Stop")
                }
            }
        }
    }
}
