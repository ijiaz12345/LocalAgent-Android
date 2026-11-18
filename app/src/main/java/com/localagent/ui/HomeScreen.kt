package com.localagent.ui
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.localagent.AgentViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable fun HomeScreen(vm: AgentViewModel = viewModel()) {
    val u by vm.ui.collectAsState()
    Scaffold(topBar = { TopAppBar(title = { Text("Local Intelligence") }, actions = { IconButton(onClick = vm::ping) { Text("↻") } }) }) { pad ->
        Column(modifier = Modifier.padding(pad).padding(20.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            StatusCard(u.connected, u.brain, u.lessons, u.missions)
            MissionInput(u.input, vm::onInput, vm::run, enabled = u.connected && !u.running)
            if (u.running) LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            if (u.output.isNotBlank()) ResultCard(u.output, ok = u.error == null)
            u.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        }
    }
}
@Composable private fun StatusCard(ok: Boolean, brain: String, lessons: Int, missions: Int) {
    Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = if (ok) "●" else "○", color = if (ok) Color(0xFF35B56A) else Color.Gray, style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.width(8.dp))
                Text(text = if (ok) "Agent ready" else "Termux not connected", style = MaterialTheme.typography.titleMedium)
            }
            Spacer(Modifier.height(8.dp))
            Text("Brain: $brain", style = MaterialTheme.typography.bodyMedium)
            Text("Lessons: $lessons · Missions: $missions", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
@Composable private fun MissionInput(value: String, onChange: (String) -> Unit, onRun: () -> Unit, enabled: Boolean) {
    OutlinedTextField(value = value, onValueChange = onChange, modifier = Modifier.fillMaxWidth(), label = { Text("What should I build?") }, minLines = 4, shape = RoundedCornerShape(12.dp), enabled = enabled)
    Button(onClick = onRun, enabled = enabled && value.isNotBlank(), modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) { Text(if (enabled) "Run mission" else "Connecting…") }
}
@Composable private fun ResultCard(text: String, ok: Boolean) {
    Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = if (ok) Color(0xFF133820) else MaterialTheme.colorScheme.errorContainer)) {
        Text(text = text, modifier = Modifier.padding(16.dp), color = if (ok) Color(0xFFB6F2C8) else MaterialTheme.colorScheme.onErrorContainer, style = MaterialTheme.typography.bodyMedium)
    }
}
