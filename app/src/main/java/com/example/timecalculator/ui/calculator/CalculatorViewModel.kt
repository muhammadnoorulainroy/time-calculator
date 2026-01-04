package com.example.timecalculator.ui.calculator

import androidx.lifecycle.ViewModel
import com.example.timecalculator.model.Duration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// State class holding all calculator UI data
data class CalculatorState(
    val display: String = "0m",
    val expression: String? = null,
    val currentInput: String = "",
    val currentDays: Int = 0,
    val currentHours: Int = 0,
    val currentMinutes: Int = 0,
    val accumulatedDuration: Duration? = null,
    val expressionParts: List<String> = emptyList(),
    val pendingOperation: Boolean = false,
    val errorMessage: String? = null
)

// ViewModel for calculator screen - handles all calculator logic
class CalculatorViewModel : ViewModel() {

    private val _state = MutableStateFlow(CalculatorState())
    val state: StateFlow<CalculatorState> = _state.asStateFlow()

    companion object {
        private const val MAX_INPUT_LENGTH = 6
    }

    // Handle digit button press (0-9)
    fun onDigit(digit: Int) {
        _state.update { current ->
            val clearedError = current.copy(errorMessage = null, expression = null)
            
            if (current.currentInput.length >= MAX_INPUT_LENGTH) {
                return@update clearedError
            }
            
            val newInput = if (current.currentInput == "0" || current.currentInput.isEmpty()) {
                if (digit == 0 && current.currentInput.isEmpty()) "0" else digit.toString()
            } else {
                current.currentInput + digit.toString()
            }
            
            clearedError.copy(
                currentInput = newInput,
                display = buildDisplayString(
                    current.currentDays,
                    current.currentHours,
                    current.currentMinutes,
                    newInput,
                    current.accumulatedDuration,
                    current.pendingOperation
                )
            )
        }
    }

    // Handle unit button press (d, h, m)
    fun onUnit(unit: Char) {
        _state.update { current ->
            val value = current.currentInput.toIntOrNull()
            if (value == null || value == 0) {
                if (current.currentInput == "0") {
                    return@update current.copy(
                        currentInput = "",
                        errorMessage = null
                    )
                }
                return@update current
            }
            
            val (newDays, newHours, newMinutes) = when (unit) {
                'd' -> Triple(current.currentDays + value, current.currentHours, current.currentMinutes)
                'h' -> Triple(current.currentDays, current.currentHours + value, current.currentMinutes)
                'm' -> Triple(current.currentDays, current.currentHours, current.currentMinutes + value)
                else -> Triple(current.currentDays, current.currentHours, current.currentMinutes)
            }
            
            // Normalize the duration
            val duration = Duration.of(newDays, newHours, newMinutes)
            
            current.copy(
                currentInput = "",
                currentDays = duration.days,
                currentHours = duration.hours,
                currentMinutes = duration.minutes,
                errorMessage = null,
                expression = null,
                display = buildDisplayString(
                    duration.days,
                    duration.hours,
                    duration.minutes,
                    "",
                    current.accumulatedDuration,
                    current.pendingOperation
                )
            )
        }
    }

    // Handle plus button - accumulate current duration
    fun onPlus() {
        _state.update { current ->
            // Check for uncommitted input
            if (current.currentInput.isNotEmpty() && current.currentInput != "0") {
                return@update current.copy(
                    errorMessage = "Please select a unit (d, h, m) for: ${current.currentInput}"
                )
            }
            
            val currentDuration = Duration.of(
                current.currentDays,
                current.currentHours,
                current.currentMinutes
            )
            
            if (currentDuration == Duration.ZERO && current.accumulatedDuration == null) {
                return@update current.copy(
                    errorMessage = "Enter a duration first"
                )
            }
            
            val newAccumulated = if (current.accumulatedDuration != null) {
                current.accumulatedDuration + currentDuration
            } else {
                currentDuration
            }
            
            // Track expression parts for display after =
            val newExpressionParts = if (currentDuration != Duration.ZERO) {
                current.expressionParts + currentDuration.toString()
            } else {
                current.expressionParts
            }
            
            current.copy(
                accumulatedDuration = newAccumulated,
                expressionParts = newExpressionParts,
                currentDays = 0,
                currentHours = 0,
                currentMinutes = 0,
                currentInput = "",
                pendingOperation = true,
                errorMessage = null,
                expression = null,
                display = "${newAccumulated} +"
            )
        }
    }

    // Handle equals button - calculate final result
    fun onEquals() {
        _state.update { current ->
            if (current.currentInput.isNotEmpty() && current.currentInput != "0") {
                return@update current.copy(
                    errorMessage = "Please select a unit (d, h, m) for: ${current.currentInput}"
                )
            }
            
            val currentDuration = Duration.of(
                current.currentDays,
                current.currentHours,
                current.currentMinutes
            )
            
            if (currentDuration == Duration.ZERO && current.accumulatedDuration == null) {
                return@update current.copy(
                    errorMessage = "Enter a duration first"
                )
            }
            
            val result = if (current.accumulatedDuration != null) {
                current.accumulatedDuration + currentDuration
            } else {
                currentDuration
            }
            
            // Build expression string showing what was added
            val finalExpressionParts = if (currentDuration != Duration.ZERO) {
                current.expressionParts + currentDuration.toString()
            } else {
                current.expressionParts
            }
            
            val expressionString = if (finalExpressionParts.size > 1) {
                finalExpressionParts.joinToString(" + ")
            } else {
                null
            }
            
            current.copy(
                accumulatedDuration = null,
                expressionParts = emptyList(),
                currentDays = result.days,
                currentHours = result.hours,
                currentMinutes = result.minutes,
                currentInput = "",
                pendingOperation = false,
                errorMessage = null,
                expression = expressionString,
                display = result.toString()
            )
        }
    }

    // Clear all calculator state
    fun onClear() {
        _state.value = CalculatorState()
    }

    // Delete last digit from input
    fun onBackspace() {
        _state.update { current ->
            val newInput = if (current.currentInput.isNotEmpty()) {
                current.currentInput.dropLast(1)
            } else {
                ""
            }
            current.copy(
                currentInput = newInput,
                errorMessage = null,
                display = buildDisplayString(
                    current.currentDays,
                    current.currentHours,
                    current.currentMinutes,
                    newInput,
                    current.accumulatedDuration,
                    current.pendingOperation
                )
            )
        }
    }

    fun dismissError() {
        _state.update { it.copy(errorMessage = null) }
    }

    fun getCurrentDuration(): Duration {
        val current = _state.value
        return Duration.of(current.currentDays, current.currentHours, current.currentMinutes)
    }

    fun hasUncommittedInput(): Boolean {
        val current = _state.value
        return current.currentInput.isNotEmpty() && current.currentInput != "0"
    }

    // Build display string for UI
    private fun buildDisplayString(
        days: Int,
        hours: Int,
        minutes: Int,
        input: String,
        accumulated: Duration?,
        pendingOp: Boolean
    ): String {
        val prefix = if (accumulated != null && pendingOp) {
            "${accumulated} + "
        } else {
            ""
        }
        
        val parts = mutableListOf<String>()
        if (days > 0) parts.add("${days}d")
        if (hours > 0) parts.add("${hours}h")
        if (minutes > 0) parts.add("${minutes}m")
        
        if (input.isNotEmpty()) {
            parts.add(input)
        } else if (parts.isEmpty()) {
            parts.add("0m")
        }
        
        return prefix + parts.joinToString(" ")
    }
}
