package jp.co.ndk_group.messay_duck_hunt.domain.models

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Dp

/// Created by Aabhash Shakya on 14/02/2026

data class Duck(
    val id: Int,
    val position: Offset,
    val velocity: Offset,
    val isAlive: Boolean = true,
    val isFalling: Boolean = false,
    val duckDirection: DuckDirection,
    val hitboxSize: Dp
)