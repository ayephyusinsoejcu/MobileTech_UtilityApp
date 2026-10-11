package au.edu.jcu.assessment.utilityapp.viewmodel

import android.os.SystemClock
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import au.edu.jcu.assessment.utilityapp.SessionMode
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TimerUiState(
    val focusMinutes: Int = 25,
    val breakMinutes: Int = 5,
    val darkMode: Boolean = false,
    val sessionMode: SessionMode = SessionMode.FOCUS,
    val remainingMillis: Long = 25L * 60_000L,
    val totalMillis: Long = 25L * 60_000L,
    val isRunning: Boolean = false,
    val completedSessions: Int = 0
)

class TimerViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(TimerUiState())
    val uiState: StateFlow<TimerUiState> = _uiState.asStateFlow()

    private var countdownJob: Job? = null
    private var deadlineMillis: Long = 0L

    fun changeSessionMode(newMode: SessionMode) {
        if (_uiState.value.sessionMode == newMode) return

        stopTimer()
        val duration = if (newMode == SessionMode.FOCUS) {
            _uiState.value.focusMinutes
        } else {
            _uiState.value.breakMinutes
        }
        val durationMillis = duration * 60_000L

        _uiState.update {
            it.copy(
                sessionMode = newMode,
                isRunning = false,
                remainingMillis = durationMillis,
                totalMillis = durationMillis
            )
        }
    }

    fun startPauseTimer() {
        if (_uiState.value.isRunning) {
            pauseTimer()
        } else {
            startTimer()
        }
    }

    private fun startTimer() {
        if (_uiState.value.remainingMillis <= 0L) return

        deadlineMillis = SystemClock.elapsedRealtime() + _uiState.value.remainingMillis
        _uiState.update { it.copy(isRunning = true) }

        countdownJob?.cancel()
        countdownJob = viewModelScope.launch {
            while (_uiState.value.isRunning) {
                val timeLeft = (deadlineMillis - SystemClock.elapsedRealtime()).coerceAtLeast(0L)
                if (timeLeft == 0L) {
                    val isFocus = _uiState.value.sessionMode == SessionMode.FOCUS
                    _uiState.update { state ->
                        state.copy(
                            isRunning = false,
                            remainingMillis = 0L,
                            completedSessions = if (isFocus) state.completedSessions + 1 else state.completedSessions
                        )
                    }
                    break
                }
                _uiState.update { it.copy(remainingMillis = timeLeft) }
                delay(100L)
            }
        }
    }

    private fun pauseTimer() {
        countdownJob?.cancel()
        val timeLeft = (deadlineMillis - SystemClock.elapsedRealtime()).coerceAtLeast(0L)
        _uiState.update {
            it.copy(
                isRunning = false,
                remainingMillis = timeLeft
            )
        }
    }

    private fun stopTimer() {
        countdownJob?.cancel()
    }

    fun resetTimer() {
        stopTimer()
        val total = _uiState.value.totalMillis
        _uiState.update {
            it.copy(
                isRunning = false,
                remainingMillis = total
            )
        }
    }

    fun updateFocusDuration(newDuration: Int) {
        if (_uiState.value.focusMinutes == newDuration) return
        _uiState.update { state ->
            val isFocus = state.sessionMode == SessionMode.FOCUS
            val total = if (isFocus) newDuration * 60_000L else state.totalMillis
            if (isFocus) stopTimer()
            state.copy(
                focusMinutes = newDuration,
                totalMillis = total,
                remainingMillis = if (isFocus) total else state.remainingMillis,
                isRunning = if (isFocus) false else state.isRunning
            )
        }
    }

    fun updateBreakDuration(newDuration: Int) {
        if (_uiState.value.breakMinutes == newDuration) return
        _uiState.update { state ->
            val isBreak = state.sessionMode == SessionMode.BREAK
            val total = if (isBreak) newDuration * 60_000L else state.totalMillis
            if (isBreak) stopTimer()
            state.copy(
                breakMinutes = newDuration,
                totalMillis = total,
                remainingMillis = if (isBreak) total else state.remainingMillis,
                isRunning = if (isBreak) false else state.isRunning
            )
        }
    }

    fun updateDarkMode(enabled: Boolean) {
        _uiState.update { it.copy(darkMode = enabled) }
    }
}
