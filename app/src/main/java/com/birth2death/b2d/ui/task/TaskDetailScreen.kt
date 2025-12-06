package com.birth2death.b2d.ui.task

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.birth2death.b2d.data.model.Task
import com.birth2death.b2d.data.repository.TaskRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailScreen(
    taskId: String?,
    onNavigateToAR: (String) -> Unit,
    onNavigateBack: () -> Unit
) {
    val task = remember(taskId) { TaskRepository.getTask(taskId ?: "") }
    
    // Force refresh when list might change
    // Ideally we subscribe to specific task flow, but repo is simple for now
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(task?.title ?: "Task Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (task == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Task not found")
            }
        } else {
            Column(modifier = Modifier.padding(padding).padding(16.dp)) {
                LinearProgressIndicator(
                    progress = task.subtaskCompletionRate.toFloat(),
                    modifier = Modifier.fillMaxWidth().height(8.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                
                Button(
                    onClick = { onNavigateToAR(task.id) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Vue in AR")
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                Text("Subtasks", style = MaterialTheme.typography.titleMedium)
                
                LazyColumn {
                    items(task.subtasks) { subtask ->
                        var isChecked by remember { mutableStateOf(subtask.isCompleted) }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                        ) {
                            Checkbox(
                                checked = isChecked,
                                onCheckedChange = { checked ->
                                    isChecked = checked
                                    subtask.isCompleted = checked
                                    // Trigger update in repo/character growth logic here
                                    // In a real app, we'd emit an event to ViewModel
                                    task.character?.updateGrowth(task.subtaskCompletionRate)
                                }
                            )
                            Text(text = subtask.title)
                        }
                    }
                }
            }
        }
    }
}
