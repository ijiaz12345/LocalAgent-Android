package com.localagent.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.localagent.domain.model.AgentError
import com.localagent.domain.model.AgentErrorKind
import com.localagent.domain.repository.AgentRepository
import com.localagent.domain.usecase.RunMissionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val input: String = "",
    val output: String = "",
    val connected: Boolean = false,
    val running: Boolean = false,
    val retrying: Boolean = false,
    val retryAttempt: Int = 0,
    val errorMessage: String? = null,
    val errorKind: AgentErrorKind? = null,
    val brain: String = "غير متصل",
    val lessons: Int = 0,
    val missions: Int = 0
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: AgentRepository,
    private val runMission: RunMissionUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val maxRetries = 2
    private val initialBackoffMs = 1000L

    init { ping() }

    fun onInputChanged(value: String) {
        _uiState.update { it.copy(input = value, errorMessage = null, errorKind = null) }
    }

    fun ping() {
        viewModelScope.launch {
            _uiState.update { it.copy(retrying = false, retryAttempt = 0, errorMessage = null) }
            try {
                val conn = repository.ping()
                _uiState.update {
                    it.copy(connected = conn.connected, brain = conn.brain,
                        lessons = conn.lessons, missions = conn.missions)
                }
            } catch (e: CancellationException) { throw e }
            catch (e: AgentError) {
                _uiState.update { it.copy(connected = false, brain = "Offline", 
                    errorMessage = e.message, errorKind = e.kind) }
            } catch (e: Exception) {
                _uiState.update { it.copy(connected = false, brain = "Offline", 
                    errorMessage = "خطأ غير متوقع", errorKind = AgentErrorKind.UNKNOWN) }
            }
        }
    }

    fun run() {
        val prompt = _uiState.value.input.trim()
        if (prompt.isEmpty() || _uiState.value.running) return

        viewModelScope.launch {
            _uiState.update { it.copy(running = true, retrying = false, retryAttempt = 0,
                output = "", errorMessage = null, errorKind = null) }
            try {
                val result = retryWithBackoff { runMission(prompt) }
                _uiState.update { it.copy(output = result.output) }
            } catch (e: CancellationException) { throw e }
            catch (e: AgentError) {
                _uiState.update { it.copy(errorMessage = e.message, errorKind = e.kind) }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "خطأ غير متوقع: ${e.message}", 
                    errorKind = AgentErrorKind.UNKNOWN) }
            } finally {
                _uiState.update { it.copy(running = false, retrying = false) }
            }
        }
    }

    private suspend fun <T> retryWithBackoff(block: suspend () -> T): T {
        var attempt = 0
        while (true) {
            try { return block() }
            catch (e: CancellationException) { throw e }
            catch (e: AgentError) {
                if (!e.retryable || attempt >= maxRetries) throw e
                attempt++
                _uiState.update { it.copy(retrying = true, retryAttempt = attempt, 
                    errorMessage = "محاولة إعادة الاتصال... ($attempt/$maxRetries)") }
                val waitMs = initialBackoffMs * (1L shl (attempt - 1))
                delay(waitMs)
            }
        }
    }
}
