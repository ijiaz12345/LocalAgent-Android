package com.localagent.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.localagent.AgentViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(vm: AgentViewModel = viewModel()) {
    val u by vm.ui.collectAsState()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Local Intelligence") },
                actions = { IconButton(onClick = vm::ping) { Text("Refresh") } }
            )
        }
    ) { pad ->
        Column(
            modifier = Modifier
                .padding(pad)
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            StatusCard(u.connected, u.brain, u.lessons, u.missions)
            MissionInput(u.input, vm::onInput, vm::run,
                enabled = u.connected && !u.running)
            if (u.running) LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            if (u.output.isNotBlank()) ResultCard(u.output, ok = u.error == null)
            u.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        }
    }
}

@Composable
private fun StatusCard(ok: Boolean, brain: String, lessons: Int, missions: Int) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(if (ok) "Connected" else "Not connected",
                 style = MaterialTheme.typography.titleMedium)
            Text("Brain: ${brain.ifEmpty { "none" }}")
            Text("Lessons: $lessons    Missions: $missions")
        }
    }
}

@Composable
private fun MissionInput(value: String, onChange: (String) -> Unit,
                         onRun: () -> Unit, enabled: Boolean) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(
            value = value, onValueChange = onChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Mission") },
            placeholder = { Text("e.g. Build a dice roller that prints five rolls") },
            enabled = enabled)
        Button(onClick = onRun, enabled = enabled, modifier = Modifier.fillMaxWidth()) {
            Text("Run mission")
        }
    }
}

@Composable
private fun ResultCard(text: String, ok: Boolean) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text(if (ok) "Result" else "Output",
                 style = MaterialTheme.typography.titleSmall)
            Spacer(Modifier.height(8.dp))
            Text(text)
        }
    }
}
