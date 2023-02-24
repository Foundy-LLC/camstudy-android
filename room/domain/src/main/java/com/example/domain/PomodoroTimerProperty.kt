package com.example.domain

import kotlinx.serialization.Serializable

@Serializable
data class PomodoroTimerProperty(
    val timerLengthMinutes: Int,
    val shortBreakMinutes: Int,
    val longBreakMinutes: Int,
    val longBreakInterval: Int,
)
