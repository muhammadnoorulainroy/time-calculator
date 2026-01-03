package com.example.timecalculator.ui.todo

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.timecalculator.model.Todo

// Color constants
private val GradientStart = Color(0xFF1B8A5A)
private val GradientMiddle = Color(0xFF4CAF7A)
private val GradientEnd = Color(0xFF7BC67E)
private val NavyText = Color(0xFF1A237E)
private val ErrorRed = Color(0xFFE57373)

// Screen showing list of todos with selection and finish functionality
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoListScreen(
    viewModel: TodoViewModel,
    onNavigateToCreate: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.listState.collectAsState()

    // Dialog for completion message
    state.completionMessage?.let { message ->
        AlertDialog(
            onDismissRequest = { viewModel.dismissCompletionMessage() },
            title = { 
                Text(
                    "Tasks Completed!",
                    color = GradientStart,
                    fontWeight = FontWeight.Bold
                ) 
            },
            text = { Text(message, color = NavyText) },
            confirmButton = {
                TextButton(onClick = { viewModel.dismissCompletionMessage() }) {
                    Text("OK", color = GradientStart)
                }
            },
            containerColor = Color.White
        )
    }

    // Dialog for error messages
    state.errorMessage?.let { error ->
        AlertDialog(
            onDismissRequest = { viewModel.dismissListError() },
            title = { 
                Text(
                    "Notice",
                    color = NavyText,
                    fontWeight = FontWeight.Bold
                ) 
            },
            text = { Text(error, color = NavyText) },
            confirmButton = {
                TextButton(onClick = { viewModel.dismissListError() }) {
                    Text("OK", color = GradientStart)
                }
            },
            containerColor = Color.White
        )
    }

    Scaffold(
        topBar = {
            // Green gradient header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(GradientStart, GradientMiddle)
                        )
                    )
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Todo List",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    // Show task count badge
                    if (state.todos.isNotEmpty()) {
                        Surface(
                            color = Color.White.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "${state.todos.size} task${if (state.todos.size != 1) "s" else ""}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        },
        // FAB to add new todo
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToCreate,
                shape = CircleShape,
                containerColor = Color.Transparent,
                contentColor = Color.White,
                modifier = Modifier.size(56.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(GradientStart, GradientEnd)
                            ),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Todo")
                }
            }
        },
        // Bottom bar with finish button when items selected
        bottomBar = {
            if (state.selectedIndices.isNotEmpty()) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.White,
                    shadowElevation = 8.dp
                ) {
                    Button(
                        onClick = { viewModel.finishSelected() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .height(56.dp),
                        shape = RoundedCornerShape(28.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent
                        ),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(GradientStart, GradientEnd)
                                    ),
                                    shape = RoundedCornerShape(28.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Finish Selected (${state.selectedIndices.size})",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        },
        containerColor = Color(0xFFF8F9FA)
    ) { paddingValues ->
        // Empty state
        if (state.todos.isEmpty()) {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "No todos yet",
                        style = MaterialTheme.typography.titleLarge,
                        color = NavyText.copy(alpha = 0.6f)
                    )
                    Text(
                        text = "Tap + to add a new todo",
                        style = MaterialTheme.typography.bodyMedium,
                        color = NavyText.copy(alpha = 0.4f)
                    )
                }
            }
        } else {
            // List of todos
            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                itemsIndexed(state.todos) { index, todo ->
                    TodoItem(
                        todo = todo,
                        isSelected = index in state.selectedIndices,
                        onToggleSelection = { viewModel.toggleSelection(index) },
                        onDelete = { viewModel.deleteTodo(index) }
                    )
                }
            }
        }
    }
}

// Single todo item card
@Composable
private fun TodoItem(
    todo: Todo,
    isSelected: Boolean,
    onToggleSelection: () -> Unit,
    onDelete: () -> Unit
) {
    val backgroundColor = if (isSelected) {
        Color(0xFFE8F5E9)
    } else {
        Color.White
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .clickable { onToggleSelection() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Checkbox(
            checked = isSelected,
            onCheckedChange = { onToggleSelection() },
            colors = CheckboxDefaults.colors(
                checkedColor = GradientStart,
                uncheckedColor = NavyText.copy(alpha = 0.3f)
            )
        )

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = todo.value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = NavyText,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = todo.duration.toString(),
                style = MaterialTheme.typography.bodySmall,
                color = GradientStart,
                fontWeight = FontWeight.SemiBold
            )
        }

        IconButton(onClick = onDelete) {
            Icon(
                Icons.Default.Delete,
                contentDescription = "Delete",
                tint = ErrorRed
            )
        }
    }
}
