package com.birth2death.b2d.ui.task

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.birth2death.b2d.data.repository.TaskRepository
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import com.birth2death.b2d.data.model.Task

import androidx.compose.material.icons.filled.Face

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(
    onNavigateToCreate: () -> Unit,
    onNavigateToTimer: () -> Unit,
    onNavigateToRewards: () -> Unit,
    onNavigateToYoga: () -> Unit,
    onTaskClick: (String) -> Unit
) {
    val tasks by TaskRepository.tasks.collectAsState()
    val userStats by TaskRepository.userStats.collectAsState()
    
    // Filter out completed tasks for the main view
    val activeTasks = tasks.filter { it.status != com.birth2death.b2d.data.model.TaskStatus.COMPLETED }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Adventure Log") },
                actions = {
                    IconButton(onClick = onNavigateToTimer) {
                        Icon(Icons.Default.PlayArrow, contentDescription = "Focus Timer")
                    }
                    IconButton(onClick = onNavigateToRewards) {
                        Icon(Icons.Default.Star, contentDescription = "Rewards Shop")
                    }
                    IconButton(onClick = onNavigateToYoga) {
                        Icon(Icons.Default.Face, contentDescription = "Yoga AI")
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(end = 16.dp, start = 8.dp)
                    ) {
                        Text("🔥 ${userStats.streakDays}", style = MaterialTheme.typography.titleMedium)
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToCreate,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "New Quest")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            // Level Progress Header
            LevelProgressCard(
                level = userStats.level,
                currentXp = userStats.currentXp,
                requiredXp = userStats.requiredXp
            )

            // Task List
            if (activeTasks.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No quests active. Add one to start your journey!", style = MaterialTheme.typography.bodyLarge)
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(items = activeTasks, key = { it.id }) { task ->
                        val dismissState = rememberDismissState()
                        
                        if (dismissState.isDismissed(DismissDirection.StartToEnd)) {
                             LaunchedEffect(Unit) {
                                 TaskRepository.completeTask(task.id)
                             }
                        }

                        SwipeToDismiss(
                            state = dismissState,
                            directions = setOf(DismissDirection.StartToEnd, DismissDirection.EndToStart),
                            background = {
                                val color = when (dismissState.dismissDirection) {
                                    DismissDirection.StartToEnd -> MaterialTheme.colorScheme.primaryContainer // Complete
                                    DismissDirection.EndToStart -> MaterialTheme.colorScheme.errorContainer   // Snooze/Delete
                                    else -> Color.Transparent
                                }
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(color, shape = MaterialTheme.shapes.medium)
                                        .padding(horizontal = 20.dp),
                                    contentAlignment = if (dismissState.dismissDirection == DismissDirection.StartToEnd) 
                                        Alignment.CenterStart else Alignment.CenterEnd
                                ) {
                                    val icon = if (dismissState.dismissDirection == DismissDirection.StartToEnd) 
                                        Icons.Default.Check else Icons.Default.Close
                                    Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
                                }
                            },
                            dismissContent = {
                                TaskCard(task = task, onClick = { onTaskClick(task.id) })
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LevelProgressCard(level: Int, currentXp: Int, requiredXp: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Level $level",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "$currentXp / $requiredXp XP",
                    style = MaterialTheme.typography.labelMedium
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = currentXp.toFloat() / requiredXp,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskCard(task: Task, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
             // Dynamic color based on difficulty or status could go here
             containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                task.description?.let {
                    if (it.isNotBlank()) {
                         Text(
                             text = it,
                             style = MaterialTheme.typography.bodySmall,
                             maxLines = 2,
                             overflow = TextOverflow.Ellipsis
                         )
                    }
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Badge(containerColor = MaterialTheme.colorScheme.tertiaryContainer) {
                    Text(
                        text = "+${task.xpReward} XP",
                        modifier = Modifier.padding(4.dp),
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }
            }
        }
    }
}
