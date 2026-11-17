package com.localagent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class Ui(
    val connected: Boolean = false,
    val brain: String = "",
    val lessons: Int = 0,
    val missions: Int = 0,
    val running: Boolean = false,
    val input: String = "",
    val output: String = "",
    val error: String? = null,
)

class AgentViewModel : ViewModel() {
    private val _ui = MutableStateFlow(Ui())
    val ui: StateFlow<Ui> = _ui
    init { ping() }
    fun ping() = viewModelScope.launch {
        try {
            val s = AgentApi.status()
            _ui.value = _ui.value.copy(
                connected = s.ok, brain = s.brain,
                lessons = s.lessonCount, missions = s.missionCount,
                error = null)
        } catch (e: Exception) {
            _ui.value = _ui.value.copy(connected = false,
                error = "Termux server not reachable. Start it: python ~/.localagent/server.py")
        }
    }
    fun onInput(s: String) { _ui.value = _ui.value.copy(input = s) }
    fun run() {
        val m = _ui.value.input.trim()
        if (m.isEmpty() || _ui.value.running) return
        viewModelScope.launch {
            _ui.value = _ui.value.copy(running = true, output = "", error = null)
            try {
                val r = AgentApi.run(m)
                _ui.value = _ui.value.copy(
                    running = false,
                    output = r.text,
                    error = if (r.ok) null else r.text)
            } catch (e: Exception) {
                _ui.value = _ui.value.copy(running = false, error = e.message)
            }
        }
    }
}
