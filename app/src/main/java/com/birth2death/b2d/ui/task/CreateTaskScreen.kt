package com.birth2death.b2d.ui.task

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.birth2death.b2d.data.model.Task
import com.birth2death.b2d.data.model.Character3D
import com.birth2death.b2d.data.model.CharacterType
import com.birth2death.b2d.data.repository.TaskRepository
import com.birth2death.b2d.data.remote.AzureOpenAIService
import kotlinx.coroutines.launch
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTaskScreen(
    onNavigateBack: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var isGenerating by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("New Task") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Task Title") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description (Optional)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    scope.launch {
                        isGenerating = true
                        val newTask = Task(
                            title = title,
                            description = description,
                            character = Character3D(characterType = CharacterType.LUMIBUG) // Default character
                        )
                        // Generate AI subtasks
                        val subtasks = AzureOpenAIService.generateSubtasks(title, description)
                        newTask.subtasks.addAll(subtasks)
                        
                        TaskRepository.addTask(newTask)
                        isGenerating = false
                        onNavigateBack()
                    }
                },
                enabled = title.isNotBlank() && !isGenerating,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isGenerating) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Generating Magic...")
                } else {
                    Text("Create Task")
                }
            }
        }
    }
}
