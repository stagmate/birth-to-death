package com.birth2death.b2d.ui.task

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import com.birth2death.b2d.data.repository.TaskRepository

@Composable
fun FocusTimerScreen(onBack: () -> Unit) {
    var timeLeft by remember { mutableStateOf(25 * 60L) } // 25 minutes
    var isRunning by remember { mutableStateOf(false) }
    var progress by remember { mutableStateOf(1f) }
    val totalTime = 25 * 60L

    LaunchedEffect(isRunning, timeLeft) {
        if (isRunning && timeLeft > 0) {
            delay(1000L)
            timeLeft--
            progress = timeLeft.toFloat() / totalTime
        } else if (timeLeft == 0L && isRunning) {
            isRunning = false
            TaskRepository.addXp(50) // Bonus for completing timer
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(contentAlignment = Alignment.Center) {
            CircularProgressIndicator(
                progress = progress,
                modifier = Modifier.size(250.dp),
                strokeWidth = 12.dp,
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
            Text(
                text = String.format("%02d:%02d", timeLeft / 60, timeLeft % 60),
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Button(onClick = { isRunning = !isRunning }) {
                Icon(
                    imageVector = if (isRunning) Icons.Default.Refresh else Icons.Default.PlayArrow, // Using Refresh as Pause placeholder or Stop
                    contentDescription = if (isRunning) "Pause" else "Start"
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (isRunning) "Pause" else "Start Focus")
            }
            
            Button(onClick = { 
                timeLeft = totalTime
                isRunning = false
                progress = 1f
            }, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)) {
                Text("Reset")
            }
        }
    }
}
