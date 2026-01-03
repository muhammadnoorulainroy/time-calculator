package com.example.timecalculator.ui.todo

import androidx.lifecycle.ViewModel
import com.example.timecalculator.model.Duration
import com.example.timecalculator.model.Timed
import com.example.timecalculator.model.Todo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// State for todo list screen
data class TodoListState(
    val todos: List<Todo> = emptyList(),
    val selectedIndices: Set<Int> = emptySet(),
    val completionMessage: String? = null,
    val errorMessage: String? = null
)

// State for create todo screen
data class CreateTodoState(
    val description: String = "",
    val currentInput: String = "",
    val days: Int = 0,
    val hours: Int = 0,
    val minutes: Int = 0,
    val errorMessage: String? = null
) {
    // Computed property for display
    val displayDuration: String
        get() {
            val parts = mutableListOf<String>()
            if (days > 0) parts.add("${days}d")
            if (hours > 0) parts.add("${hours}h")
            if (minutes > 0) parts.add("${minutes}m")
            if (currentInput.isNotEmpty()) parts.add(currentInput)
            return if (parts.isEmpty()) "0m" else parts.joinToString(" ")
        }
    
    val hasDuration: Boolean
        get() = days > 0 || hours > 0 || minutes > 0
    
    val hasUncommittedInput: Boolean
        get() = currentInput.isNotEmpty() && currentInput != "0"
    
    val isValid: Boolean
        get() = description.isNotBlank() && hasDuration && !hasUncommittedInput
}

// ViewModel handling both todo list and create todo screens
class TodoViewModel : ViewModel() {

    private val _listState = MutableStateFlow(TodoListState())
    val listState: StateFlow<TodoListState> = _listState.asStateFlow()

    private val _createState = MutableStateFlow(CreateTodoState())
    val createState: StateFlow<CreateTodoState> = _createState.asStateFlow()

    companion object {
        private const val MAX_INPUT_LENGTH = 6
        private const val MAX_DESCRIPTION_LENGTH = 200
    }

    // Toggle selection of a todo item
    fun toggleSelection(index: Int) {
        _listState.update { current ->
            if (index < 0 || index >= current.todos.size) {
                return@update current
            }
            val newSelection = if (index in current.selectedIndices) {
                current.selectedIndices - index
            } else {
                current.selectedIndices + index
            }
            current.copy(selectedIndices = newSelection, errorMessage = null)
        }
    }

    // Finish selected todos and show completion message with total time
    fun finishSelected() {
        _listState.update { current ->
            if (current.selectedIndices.isEmpty()) {
                return@update current.copy(errorMessage = "No tasks selected")
            }

            val validIndices = current.selectedIndices.filter { it in current.todos.indices }
            if (validIndices.isEmpty()) {
                return@update current.copy(
                    selectedIndices = emptySet(),
                    errorMessage = "Invalid selection"
                )
            }

            val selectedTodos = validIndices
                .sorted()
                .map { current.todos[it] }

            // Use Timed.combine to get total duration
            val combined = Timed.combine(selectedTodos)
            val totalDuration = combined.duration
            val descriptions = combined.value

            val message = buildString {
                append("Completed ${descriptions.size} task(s)!\n\n")
                append("Total time: $totalDuration\n\n")
                append("Tasks:\n")
                descriptions.forEachIndexed { index, desc ->
                    append("${index + 1}. $desc\n")
                }
            }

            // Remove finished todos from list
            val remainingTodos = current.todos.filterIndexed { index, _ ->
                index !in validIndices.toSet()
            }

            current.copy(
                todos = remainingTodos,
                selectedIndices = emptySet(),
                completionMessage = message,
                errorMessage = null
            )
        }
    }

    fun dismissCompletionMessage() {
        _listState.update { it.copy(completionMessage = null) }
    }

    fun dismissListError() {
        _listState.update { it.copy(errorMessage = null) }
    }

    // Delete a single todo
    fun deleteTodo(index: Int) {
        _listState.update { current ->
            if (index < 0 || index >= current.todos.size) {
                return@update current.copy(errorMessage = "Cannot delete: invalid item")
            }
            
            val newTodos = current.todos.toMutableList().apply {
                removeAt(index)
            }
            // Adjust selection indices after deletion
            val newSelection = current.selectedIndices
                .filter { it != index }
                .map { if (it > index) it - 1 else it }
                .toSet()
            current.copy(todos = newTodos, selectedIndices = newSelection, errorMessage = null)
        }
    }

    // Update todo description
    fun updateDescription(description: String) {
        _createState.update { current ->
            val trimmed = if (description.length > MAX_DESCRIPTION_LENGTH) {
                description.take(MAX_DESCRIPTION_LENGTH)
            } else {
                description
            }
            current.copy(description = trimmed, errorMessage = null)
        }
    }

    // Handle digit input for duration
    fun onDurationDigit(digit: Int) {
        _createState.update { current ->
            val cleared = current.copy(errorMessage = null)
            
            if (current.currentInput.length >= MAX_INPUT_LENGTH) {
                return@update cleared
            }
            
            val newInput = if (current.currentInput == "0" || current.currentInput.isEmpty()) {
                if (digit == 0 && current.currentInput.isEmpty()) "0" else digit.toString()
            } else {
                current.currentInput + digit.toString()
            }
            cleared.copy(currentInput = newInput)
        }
    }

    // Handle unit selection for duration
    fun onDurationUnit(unit: Char) {
        _createState.update { current ->
            val value = current.currentInput.toIntOrNull()
            if (value == null || value == 0) {
                if (current.currentInput == "0") {
                    return@update current.copy(currentInput = "", errorMessage = null)
                }
                return@update current
            }
            
            val (newDays, newHours, newMinutes) = when (unit) {
                'd' -> Triple(current.days + value, current.hours, current.minutes)
                'h' -> Triple(current.days, current.hours + value, current.minutes)
                'm' -> Triple(current.days, current.hours, current.minutes + value)
                else -> Triple(current.days, current.hours, current.minutes)
            }
            // Normalize duration
            val duration = Duration.of(newDays, newHours, newMinutes)
            current.copy(
                currentInput = "",
                days = duration.days,
                hours = duration.hours,
                minutes = duration.minutes,
                errorMessage = null
            )
        }
    }

    fun clearDuration() {
        _createState.update {
            it.copy(currentInput = "", days = 0, hours = 0, minutes = 0, errorMessage = null)
        }
    }

    fun onDurationBackspace() {
        _createState.update { current ->
            current.copy(
                currentInput = current.currentInput.dropLast(1),
                errorMessage = null
            )
        }
    }

    // Validate todo before saving
    fun validateTodo(): String? {
        val current = _createState.value
        
        return when {
            current.description.isBlank() -> "Please enter a description"
            current.hasUncommittedInput -> "Please select a unit (d, h, m) for: ${current.currentInput}"
            !current.hasDuration -> "Please enter a duration"
            else -> null
        }
    }

    // Save todo to list
    fun saveTodo(): Boolean {
        val validationError = validateTodo()
        if (validationError != null) {
            _createState.update { it.copy(errorMessage = validationError) }
            return false
        }

        val current = _createState.value
        val duration = Duration.of(current.days, current.hours, current.minutes)
        val todo = Timed(duration, current.description.trim())

        _listState.update { it.copy(todos = it.todos + todo) }
        resetCreateState()
        return true
    }

    fun resetCreateState() {
        _createState.value = CreateTodoState()
    }

    fun dismissCreateError() {
        _createState.update { it.copy(errorMessage = null) }
    }
}
