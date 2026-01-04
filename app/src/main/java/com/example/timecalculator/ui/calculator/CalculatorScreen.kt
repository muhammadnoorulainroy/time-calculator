package com.example.timecalculator.ui.calculator

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.lifecycle.viewmodel.compose.viewModel

// Color constants for the green theme
private val GradientStart = Color(0xFF1B8A5A)
private val GradientMiddle = Color(0xFF4CAF7A)
private val GradientEnd = Color(0xFF7BC67E)
private val NavyText = Color(0xFF1A237E)
private val LightGray = Color(0xFFF8F9FA)

// Main calculator screen composable
@Composable
fun CalculatorScreen(
    viewModel: CalculatorViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Green gradient display area at top
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(GradientStart, GradientMiddle, GradientEnd)
                    )
                )
                .padding(24.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Time Calculator",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White.copy(alpha = 0.9f),
                    fontWeight = FontWeight.Medium
                )
                
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.End
                ) {
                    // Show error message if any
                    state.errorMessage?.let { error ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp),
                            color = Color.White.copy(alpha = 0.9f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = error,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFC62828),
                                modifier = Modifier.padding(8.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                    
                    // Show expression after calculation (e.g., "3d 13h + 5m")
                    state.expression?.let { expr ->
                        Text(
                            text = expr,
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.White.copy(alpha = 0.7f),
                            textAlign = TextAlign.End
                        )
                    }
                    
                    // Main display showing current value
                    Text(
                        text = state.display,
                        style = MaterialTheme.typography.displayLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.End
                    )
                    
                    // Hint to select unit when digits are entered
                    if (state.currentInput.isNotEmpty() && state.currentInput != "0") {
                        Text(
                            text = "Select unit (d/h/m)",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.7f),
                            textAlign = TextAlign.End
                        )
                    }
                }
            }
        }

        // White button area
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.8f),
            color = Color.White,
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Unit buttons row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    UnitButton(
                        text = "d",
                        onClick = { viewModel.onUnit('d') },
                        modifier = Modifier.weight(1f),
                        highlighted = state.currentInput.isNotEmpty() && state.currentInput != "0"
                    )
                    UnitButton(
                        text = "h",
                        onClick = { viewModel.onUnit('h') },
                        modifier = Modifier.weight(1f),
                        highlighted = state.currentInput.isNotEmpty() && state.currentInput != "0"
                    )
                    UnitButton(
                        text = "m",
                        onClick = { viewModel.onUnit('m') },
                        modifier = Modifier.weight(1f),
                        highlighted = state.currentInput.isNotEmpty() && state.currentInput != "0"
                    )
                    OperatorButton("C", { viewModel.onClear() }, Modifier.weight(1f))
                }

                // Digit rows
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    DigitButton("1", { viewModel.onDigit(1) }, Modifier.weight(1f))
                    DigitButton("2", { viewModel.onDigit(2) }, Modifier.weight(1f))
                    DigitButton("3", { viewModel.onDigit(3) }, Modifier.weight(1f))
                    OperatorButton("⌫", { viewModel.onBackspace() }, Modifier.weight(1f))
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    DigitButton("4", { viewModel.onDigit(4) }, Modifier.weight(1f))
                    DigitButton("5", { viewModel.onDigit(5) }, Modifier.weight(1f))
                    DigitButton("6", { viewModel.onDigit(6) }, Modifier.weight(1f))
                    Spacer(modifier = Modifier.weight(1f))
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    DigitButton("7", { viewModel.onDigit(7) }, Modifier.weight(1f))
                    DigitButton("8", { viewModel.onDigit(8) }, Modifier.weight(1f))
                    DigitButton("9", { viewModel.onDigit(9) }, Modifier.weight(1f))
                    OperatorButton("+", { viewModel.onPlus() }, Modifier.weight(1f))
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Spacer(modifier = Modifier.weight(1f))
                    DigitButton("0", { viewModel.onDigit(0) }, Modifier.weight(1f))
                    Spacer(modifier = Modifier.weight(1f))
                    EqualsButton({ viewModel.onEquals() }, Modifier.weight(1f))
                }
            }
        }
    }
}

// Digit button component
@Composable
private fun DigitButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TextButton(
        onClick = onClick,
        modifier = modifier.height(60.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Text(
            text = text,
            fontSize = 28.sp,
            fontWeight = FontWeight.Medium,
            color = NavyText
        )
    }
}

// Unit button (d, h, m) with highlight when input pending
@Composable
private fun UnitButton(
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
            .height(60.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor),
        shape = RoundedCornerShape(16.dp)
    ) {
        Text(
            text = text,
            fontSize = 22.sp,
            fontWeight = if (highlighted) FontWeight.Bold else FontWeight.SemiBold,
            color = textColor
        )
    }
}

// Operator button (C, backspace, +)
@Composable
private fun OperatorButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TextButton(
        onClick = onClick,
        modifier = modifier.height(60.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Text(
            text = text,
            fontSize = 26.sp,
            fontWeight = FontWeight.Medium,
            color = NavyText.copy(alpha = 0.7f)
        )
    }
}

// Equals button with gradient background
@Composable
private fun EqualsButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(60.dp),
        shape = CircleShape,
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
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "=",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}
