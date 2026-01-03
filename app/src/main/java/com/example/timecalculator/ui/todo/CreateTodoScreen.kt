package com.example.timecalculator.ui.todo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Color constants
private val GradientStart = Color(0xFF1B8A5A)
private val GradientMiddle = Color(0xFF4CAF7A)
private val GradientEnd = Color(0xFF7BC67E)
private val NavyText = Color(0xFF1A237E)
private val LightGray = Color(0xFFF8F9FA)
private val ErrorRed = Color(0xFFE57373)

// Screen for creating a new todo with description and duration
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTodoScreen(
    viewModel: TodoViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.createState.collectAsState()

    Scaffold(
        topBar = {
            // Green gradient header with back button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(GradientStart, GradientMiddle)
                        )
                    )
                    .statusBarsPadding()
                    .padding(horizontal = 8.dp, vertical = 8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = {
                        viewModel.resetCreateState()
                        onNavigateBack()
                    }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                    Text(
                        text = "New Todo",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Error message display
            state.errorMessage?.let { error ->
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = ErrorRed.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = error,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFFC62828),
                        modifier = Modifier.padding(12.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Description text field
            OutlinedTextField(
                value = state.description,
                onValueChange = { viewModel.updateDescription(it) },
                label = { Text("Description") },
                placeholder = { Text("Enter todo description...") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = false,
                minLines = 2,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GradientStart,
                    focusedLabelColor = GradientStart,
                    cursorColor = GradientStart,
                    unfocusedBorderColor = NavyText.copy(alpha = 0.2f),
                    errorBorderColor = ErrorRed
                ),
                isError = state.errorMessage?.contains("description") == true,
                supportingText = {
                    Text(
                        text = "${state.description.length}/200",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (state.description.length > 180) ErrorRed else NavyText.copy(alpha = 0.5f)
                    )
                }
            )

            // Duration label with required indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Duration",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = NavyText
                )
                if (!state.hasDuration && !state.hasUncommittedInput) {
                    Text(
                        text = "Required",
                        style = MaterialTheme.typography.bodySmall,
                        color = ErrorRed
                    )
                }
            }

            // Duration display with gradient background
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(GradientStart, GradientMiddle, GradientEnd)
                        )
                    )
                    .padding(20.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = state.displayDuration,
                        style = MaterialTheme.typography.headlineLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    // Hint when digits need unit
                    if (state.hasUncommittedInput) {
                        Text(
                            text = "Select unit (d/h/m)",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            // Mini calculator for duration input
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Unit buttons row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MiniUnitButton(
                        text = "d",
                        onClick = { viewModel.onDurationUnit('d') },
                        modifier = Modifier.weight(1f),
                        highlighted = state.hasUncommittedInput
                    )
                    MiniUnitButton(
                        text = "h",
                        onClick = { viewModel.onDurationUnit('h') },
                        modifier = Modifier.weight(1f),
                        highlighted = state.hasUncommittedInput
                    )
                    MiniUnitButton(
                        text = "m",
                        onClick = { viewModel.onDurationUnit('m') },
                        modifier = Modifier.weight(1f),
                        highlighted = state.hasUncommittedInput
                    )
                    MiniButton(
                        text = "C",
                        onClick = { viewModel.clearDuration() },
                        modifier = Modifier.weight(1f),
                        textColor = ErrorRed
                    )
                }

                // Digit rows
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MiniButton("1", { viewModel.onDurationDigit(1) }, Modifier.weight(1f))
                    MiniButton("2", { viewModel.onDurationDigit(2) }, Modifier.weight(1f))
                    MiniButton("3", { viewModel.onDurationDigit(3) }, Modifier.weight(1f))
                    MiniButton("⌫", { viewModel.onDurationBackspace() }, Modifier.weight(1f))
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MiniButton("4", { viewModel.onDurationDigit(4) }, Modifier.weight(1f))
                    MiniButton("5", { viewModel.onDurationDigit(5) }, Modifier.weight(1f))
                    MiniButton("6", { viewModel.onDurationDigit(6) }, Modifier.weight(1f))
                    Spacer(modifier = Modifier.weight(1f))
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MiniButton("7", { viewModel.onDurationDigit(7) }, Modifier.weight(1f))
                    MiniButton("8", { viewModel.onDurationDigit(8) }, Modifier.weight(1f))
                    MiniButton("9", { viewModel.onDurationDigit(9) }, Modifier.weight(1f))
                    Spacer(modifier = Modifier.weight(1f))
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Spacer(modifier = Modifier.weight(1f))
                    MiniButton("0", { viewModel.onDurationDigit(0) }, Modifier.weight(1f))
                    Spacer(modifier = Modifier.weight(1f))
                    Spacer(modifier = Modifier.weight(1f))
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Save button with gradient when valid
            Button(
                onClick = {
                    if (viewModel.saveTodo()) {
                        onNavigateBack()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent
                ),
                contentPadding = PaddingValues(0.dp)
            ) {
                val isValid = state.isValid
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = if (isValid) {
                                Brush.horizontalGradient(
                                    colors = listOf(GradientStart, GradientEnd)
                                )
                            } else {
                                Brush.horizontalGradient(
                                    colors = listOf(Color.Gray.copy(alpha = 0.3f), Color.Gray.copy(alpha = 0.3f))
                                )
                            },
                            shape = RoundedCornerShape(28.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Save Todo",
                        style = MaterialTheme.typography.titleMedium,
                        color = if (isValid) Color.White else Color.Gray,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

// Mini button for calculator
@Composable
private fun MiniButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    textColor: Color = NavyText
) {
    TextButton(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = text,
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
            color = textColor
        )
    }
}

// Mini unit button with highlight
@Composable
private fun MiniUnitButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    highlighted: Boolean = false
) {
    val backgroundColor = if (highlighted) {
        Color(0xFFE8F5E9)
    } else {
        LightGray
    }
    val textColor = if (highlighted) {
        GradientStart
    } else {
        GradientStart.copy(alpha = 0.7f)
    }
    
    TextButton(
        onClick = onClick,
        modifier = modifier
            .height(48.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = text,
            fontSize = 18.sp,
            fontWeight = if (highlighted) FontWeight.Bold else FontWeight.SemiBold,
            color = textColor
        )
    }
}
