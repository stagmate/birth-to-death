package com.birth2death.b2d.data.repository

import com.birth2death.b2d.data.model.Task
import com.birth2death.b2d.data.model.TaskStatus
import com.birth2death.b2d.data.model.UserStats
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object TaskRepository {
    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks.asStateFlow()

    private val _userStats = MutableStateFlow(UserStats())
    val userStats: StateFlow<UserStats> = _userStats.asStateFlow()

    fun addTask(task: Task) {
        _tasks.value = _tasks.value + task
    }

    fun getTask(id: String): Task? {
        return _tasks.value.find { it.id == id }
    }

    fun completeTask(taskId: String) {
        val currentTasks = _tasks.value.toMutableList()
        val index = currentTasks.indexOfFirst { it.id == taskId }
        if (index != -1) {
            val task = currentTasks[index]
            if (task.status != TaskStatus.COMPLETED) {
                val updatedTask = task.copy(status = TaskStatus.COMPLETED, completedAt = "Just now")
                currentTasks[index] = updatedTask
                _tasks.value = currentTasks

                // Update XP
                val stats = _userStats.value
                var newXp = stats.currentXp + task.xpReward
                var newLevel = stats.level
                var reqXp = stats.requiredXp

                if (newXp >= reqXp) {
                    newLevel += 1
                    reqXp = (reqXp * 1.5).toInt()
                }
                
                _userStats.value = stats.copy(
                    currentXp = newXp,
                    level = newLevel,
                    requiredXp = reqXp,
                    streakDays = stats.streakDays + (if (stats.streakDays == 0) 1 else 0) // Simple mock streak
                )
            }
        }
    }
    fun addXp(amount: Int) {
        val stats = _userStats.value
        var newXp = stats.currentXp + amount
        var newLevel = stats.level
        var reqXp = stats.requiredXp

        while (newXp >= reqXp) {
            newLevel += 1
            reqXp = (reqXp * 1.5).toInt()
        }
        
        _userStats.value = stats.copy(
            currentXp = newXp,
            level = newLevel,
            requiredXp = reqXp
        )
    }
}
