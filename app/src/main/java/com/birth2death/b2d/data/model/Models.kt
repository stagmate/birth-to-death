package com.birth2death.b2d.data.model

import java.util.UUID

data class Task(
    val id: String = UUID.randomUUID().toString(),
    var title: String,
    var description: String? = null,
    var dueDate: String? = null, // simplified date
    val createdAt: String = "", // simplified date
    var completedAt: String? = null,
    var status: TaskStatus = TaskStatus.CREATED,
    var progressPercentage: Double = 0.0,
    var subtasks: MutableList<Subtask> = mutableListOf(),
    var character: Character3D? = null,
    var difficulty: Difficulty = Difficulty.MEDIUM,
    var sentimentScore: Double? = null,
    var emotionalState: EmotionalState? = null,
    var isOverwhelmedDetected: Boolean = false,
    val xpReward: Int = 10
) {
    val isOverdue: Boolean
        get() = false // Implement date logic if needed
        
    val subtaskCompletionRate: Double
        get() {
            if (subtasks.isEmpty()) return 0.0
            return subtasks.sumOf { it.progress } / subtasks.size
        }
}

enum class TaskStatus {
    CREATED, SCHEDULED, STARTED, IN_PROGRESS, COMPLETED, ABANDONED
}

enum class Difficulty {
    EASY, MEDIUM, HARD
}

data class Subtask(
    val id: String = UUID.randomUUID().toString(),
    var title: String,
    var isCompleted: Boolean = false,
    var children: MutableList<Subtask>? = null
) {
    val progress: Double
        get() {
            val total = totalSubtaskCount
            val completed = completedSubtaskCount
            return if (total > 0) completed.toDouble() / total else 0.0
        }

    val totalSubtaskCount: Int
        get() {
            var count = 1
            children?.forEach { count += it.totalSubtaskCount }
            return count
        }

    val completedSubtaskCount: Int
        get() {
            var count = if (isCompleted) 1 else 0
            children?.forEach { count += it.completedSubtaskCount }
            return count
        }
}

data class Character3D(
    val id: String = UUID.randomUUID().toString(),
    val characterType: CharacterType,
    val colorScheme: ColorScheme = ColorScheme.BLUE,
    val rarity: CharacterRarity = CharacterRarity.COMMON,
    var growthLevel: Int = 1,
    var currentScale: Float = 0.1f,
    var currentAnimation: AnimationState = AnimationState.IDLE,
    var mood: CharacterMood = CharacterMood.HAPPY
) {
    fun updateGrowth(progress: Double) {
        val newLevel = ((progress * 9.0) + 1.0).toInt()
        if (newLevel != growthLevel) {
            growthLevel = newLevel
            currentScale = 0.1f + ((growthLevel - 1) / 9.0f * 0.9f)
            currentAnimation = AnimationState.GROWING
        }
    }
}

enum class CharacterType {
    LUMIBUG, FLUFFTAIL, CORALYX, MOSSLING, SPARKWING, 
    FROSTPUFF, EMBERKIT, TIDEPUP, STARSHELL, VINEPAW
}

enum class ColorScheme {
    RED, BLUE, SILVER, GOLD, GREEN, PINK, ORANGE, BROWN, PURPLE, WHITE
}

enum class CharacterRarity {
    COMMON, RARE, EPIC, LEGENDARY
}

enum class AnimationState {
    IDLE, GROWING, CELEBRATING, ENCOURAGING, THINKING, SICK, DYING, DEAD
}

enum class CharacterMood {
    ECSTATIC, HAPPY, NEUTRAL, WORRIED, SAD, SICK, CRITICAL, DYING, DEAD
}

enum class EmotionalState {
    STRESSED, NEUTRAL, POSITIVE, OVERWHELMED, THRIVING;
    
    val emoji: String
        get() = when(this) {
            STRESSED -> "😟"
            NEUTRAL -> "😐"
            POSITIVE -> "😊"
            OVERWHELMED -> "😰"
            THRIVING -> "🌟"
        }
}

data class UserStats(
    val level: Int = 1,
    val currentXp: Int = 0,
    val requiredXp: Int = 100,
    val streakDays: Int = 0
)
