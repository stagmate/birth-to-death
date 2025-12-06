package com.birth2death.b2d.ui.task

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import com.birth2death.b2d.data.repository.TaskRepository

data class YogaPose(
    val name: String,
    val durationSeconds: Int,
    val instruction: String,
    val aiFeedback: String
)

@Composable
fun YogaSessionScreen(onBack: () -> Unit) {
    val poses = remember {
        listOf(
            YogaPose("Deep Breathing", 10, "Stand tall, inhale deeply through nose, exhale through mouth.", "Relax your shoulders..."),
            YogaPose("Tree Pose", 15, "Balance on one leg, place other foot on inner thigh.", "Focus on a fixed point to balance!"),
            YogaPose("Warrior I", 15, "Lunge forward, raise arms overhead.", "Keep your back leg straight!"),
            YogaPose("Cobra Pose", 10, "Lie on stomach, lift chest up.", "Don't strain your neck, look forward."),
            YogaPose("Child's Pose", 10, "Sit back on heels, stretch arms forward.", "Rest and breathe...")
        )
    }

    var currentPoseIndex by remember { mutableStateOf(0) }
    var timeLeft by remember { mutableStateOf(poses[0].durationSeconds) }
    var isRunning by remember { mutableStateOf(false) }
    var sessionComplete by remember { mutableStateOf(false) }

    LaunchedEffect(isRunning, timeLeft) {
        if (isRunning && timeLeft > 0) {
            delay(1000L)
            timeLeft--
        } else if (timeLeft == 0 && isRunning) {
            if (currentPoseIndex < poses.size - 1) {
                currentPoseIndex++
                timeLeft = poses[currentPoseIndex].durationSeconds
            } else {
                isRunning = false
                sessionComplete = true
                TaskRepository.addXp(100) // Big reward for yoga!
            }
        }
    }

    Scaffold(
        floatingActionButton = {
            if (!sessionComplete) {
                FloatingActionButton(onClick = { isRunning = !isRunning }) {
                     Icon(Icons.Default.PlayArrow, contentDescription = if (isRunning) "Pause" else "Start")
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (sessionComplete) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Namaste! 🙏", fontSize = 32.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("You earned 100 XP!", style = MaterialTheme.typography.titleLarge)
                        Spacer(modifier = Modifier.height(32.dp))
                        Button(onClick = onBack) {
                            Text("Return to Adventures")
                        }
                    }
                }
            } else {
                val pose = poses[currentPoseIndex]
                
                // 3D Model Placeholder
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                   Column(horizontalAlignment = Alignment.CenterHorizontally) {
                       Icon(Icons.Default.Face, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.primary)
                       Spacer(modifier = Modifier.height(8.dp))
                       Text("AI Instructor", style = MaterialTheme.typography.labelLarge)
                       Text("(3D Animation Here)", style = MaterialTheme.typography.bodySmall)
                   }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // AI Feedback Bubble
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text("🤖", fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = if (isRunning) pose.aiFeedback else "Ready to start?",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(pose.name, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text(pose.instruction, style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center)

                Spacer(modifier = Modifier.weight(1f))

                // Timer
                Text(
                    text = "00:${String.format("%02d", timeLeft)}",
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
