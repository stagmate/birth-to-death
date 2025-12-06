package com.birth2death.b2d

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.birth2death.b2d.ui.theme.B2DTheme
import com.birth2death.b2d.ui.task.TaskListScreen
import com.birth2death.b2d.ui.task.CreateTaskScreen
import com.birth2death.b2d.ui.task.TaskDetailScreen
import com.birth2death.b2d.ui.ar.ARScreen
import com.birth2death.b2d.ui.task.FocusTimerScreen
import com.birth2death.b2d.ui.task.RewardsScreen
import com.birth2death.b2d.ui.task.YogaSessionScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            B2DTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = "taskList") {
                        composable("taskList") {
                            TaskListScreen(
                                onNavigateToCreate = { navController.navigate("createTask") },
                                onNavigateToTimer = { navController.navigate("timer") },
                                onNavigateToRewards = { navController.navigate("rewards") },
                                onNavigateToYoga = { navController.navigate("yoga") },
                                onTaskClick = { taskId -> navController.navigate("taskDetail/$taskId") }
                            )
                        }
                        composable("createTask") {
                            CreateTaskScreen(
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                        composable("taskDetail/{taskId}") { backStackEntry ->
                            val taskId = backStackEntry.arguments?.getString("taskId")
                            TaskDetailScreen(
                                taskId = taskId,
                                onNavigateToAR = { id -> navController.navigate("arView/$id") },
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                        composable("arView/{taskId}") { backStackEntry ->
                             val taskId = backStackEntry.arguments?.getString("taskId")
                             ARScreen(taskId = taskId, onBack = { navController.popBackStack() })
                        }
                        composable("timer") {
                            FocusTimerScreen(onBack = { navController.popBackStack() })
                        }
                        composable("rewards") {
                            RewardsScreen(onBack = { navController.popBackStack() })
                        }
                        composable("yoga") {
                            YogaSessionScreen(onBack = { navController.popBackStack() })
                        }
                    }
                }
            }
        }
    }
}
