package jp.co.ndk_group.messay_duck_hunt.constants

import androidx.compose.ui.unit.dp

/// Created by Aabhash Shakya on 14/02/2026

object GameConfig {
    //UI
    val DUCK_SIZE = 110.dp
    val INITIAL_HITBOX_SIZE = 120.dp
    val RETICLE_SIZE = 50.dp

    //Duck Behavior
    const val DUCKS_PER_ROUND = 10
    const val INITIAL_DUCK_SPEED = 100f
    const val DUCK_SPEED_INCREASE_PER_HIT = 20f
    const val INITIAL_FLY_DURATION_MS = 6000L
    const val DUCK_FALL_DURATION_MS = 800L

    //Scoring
    const val BASE_POINTS = 100
    const val BONUS_POINTS_PER_DIFFICULTY = 50

    //Levels
    const val LEVELS = 5
    fun getLevelSpeedMultiplier(level: Int): Float = 1f + (level - 1) * 0.3f
    fun getLevelDurationMultiplier(level: Int): Float = 1f - (level - 1) * 0.15f
    fun getLevelHitboxMultiplier(level: Int): Float = 1f - (level - 1) * 0.1f

    //MDK Configuration
    const val EYE_CLOSE_THRESHOLD = 0.8f
    const val SHOOT_REQUIRED_MILLIS = 50L
    const val FACE_MOVEMENT_SENSITIVITY_HORIZONTAL = 0.7f
    const val FACE_MOVEMENT_SENSITIVITY_VERTICAL = 0.9f
}
