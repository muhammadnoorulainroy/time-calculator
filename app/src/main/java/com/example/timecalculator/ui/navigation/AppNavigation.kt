package com.example.timecalculator.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.timecalculator.ui.calculator.CalculatorScreen
import com.example.timecalculator.ui.todo.CreateTodoScreen
import com.example.timecalculator.ui.todo.TodoListScreen
import com.example.timecalculator.ui.todo.TodoViewModel

// Color constants for navigation
private val GradientStart = Color(0xFF1B8A5A)
private val NavyText = Color(0xFF1A237E)

// Screen routes
sealed class Screen(val route: String, val title: String) {
    data object Calculator : Screen("calculator", "Calculator")
    data object TodoList : Screen("todo_list", "Todos")
    data object CreateTodo : Screen("create_todo", "New Todo")
}

// Main navigation component with bottom nav bar
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    
    // Shared ViewModel for todo screens
    val todoViewModel: TodoViewModel = viewModel()

    val bottomNavItems = listOf(
        Screen.Calculator to Icons.Default.DateRange,
        Screen.TodoList to Icons.Default.List
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    
    // Hide bottom bar on create todo screen
    val showBottomBar = currentDestination?.route != Screen.CreateTodo.route

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = Color.White,
                    contentColor = NavyText
                ) {
                    bottomNavItems.forEach { (screen, icon) ->
                        val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
                        NavigationBarItem(
                            icon = { Icon(icon, contentDescription = screen.title) },
                            label = { Text(screen.title) },
                            selected = selected,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = GradientStart,
                                selectedTextColor = GradientStart,
                                unselectedIconColor = NavyText.copy(alpha = 0.5f),
                                unselectedTextColor = NavyText.copy(alpha = 0.5f),
                                indicatorColor = Color(0xFFE8F5E9)
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        // Navigation host with all screens
        NavHost(
            navController = navController,
            startDestination = Screen.Calculator.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Calculator.route) {
                CalculatorScreen()
            }
            
            composable(Screen.TodoList.route) {
                TodoListScreen(
                    viewModel = todoViewModel,
                    onNavigateToCreate = {
                        navController.navigate(Screen.CreateTodo.route)
                    }
                )
            }
            
            composable(Screen.CreateTodo.route) {
                CreateTodoScreen(
                    viewModel = todoViewModel,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}
