package com.birth2death.b2d.ui.task

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.birth2death.b2d.data.repository.TaskRepository

data class ShopItem(
    val id: String,
    val name: String,
    val cost: Int,
    val description: String,
    val color: Color
)

@Composable
fun RewardsScreen(onBack: () -> Unit) {
    val userStats by TaskRepository.userStats.collectAsState()
    
    val shopItems = remember {
        listOf(
            ShopItem("1", "Blue Aura", 100, "A cool blue glow for your pet", Color(0xFF64B5F6)),
            ShopItem("2", "Golden Crown", 500, "Royal headgear", Color(0xFFFFD54F)),
            ShopItem("3", "Fire Effect", 1000, "Blazing particles", Color(0xFFE57373)),
            ShopItem("4", "Forest Theme", 250, "Nature background", Color(0xFFAED581)),
            ShopItem("5", "Space Suit", 2000, "To the moon!", Color(0xFFBA68C8)),
            ShopItem("6", "Neon Lights", 750, "Cyberpunk vibes", Color(0xFF4DD0E1)),
        )
    }

    Scaffold(
        topBar = {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Rewards Shop", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("${userStats.currentXp} XP Available", style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    ) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(padding)
        ) {
            items(shopItems) { item ->
                ShopItemCard(item = item, userXp = userStats.currentXp) {
                    // Buy logic placeholder
                    if (userStats.currentXp >= item.cost) {
                        TaskRepository.addXp(-item.cost) // Deduct cost
                        // TODO: Add to inventory logic
                    }
                }
            }
        }
    }
}

@Composable
fun ShopItemCard(item: ShopItem, userXp: Int, onBuy: () -> Unit) {
    val canAfford = userXp >= item.cost

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clickable(enabled = false) {}, // Placeholder for image
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    shape = MaterialTheme.shapes.medium,
                    color = item.color,
                    modifier = Modifier.fillMaxSize()
                ) {}
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(item.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(item.description, style = MaterialTheme.typography.bodySmall, textAlign = TextAlign.Center, minLines = 2)
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = onBuy,
                enabled = canAfford,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (canAfford) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceTint
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("${item.cost} XP")
            }
        }
    }
}
