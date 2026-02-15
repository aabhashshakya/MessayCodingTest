package jp.co.ndk_group.messay_duck_hunt.domain.models

import androidx.compose.ui.unit.Dp
import jp.co.ndk_group.messay_duck_hunt.constants.GameConfig

/// Created by Aabhash Shakya on 14/02/2026

enum class GameState {
    MENU,
    PLAYING,
    ROUND_COMPLETE,
    GAME_OVER
}

data class DifficultySettings(
    val level: Int = 1,
    val duckSpeed: Float,
    val flyDuration: Long,
    val hitboxSize: Dp,
    val successfulHits: Int = 0
)

data class GameStats(
    val score: Int = 0,
    val ducksHitThisRound: Int = 0,  //Reset each round
    val ducksMissedThisRound: Int = 0,  //Reset each round
    val totalDucksHit: Int = 0,  //Accumulated across all rounds
    val totalDucksMissed: Int = 0,  //Accumulated across all rounds
    val currentLevel: Int = 1,
    val ducksRemainingInRound: Int = GameConfig.DUCKS_PER_ROUND,
    val totalDucksInRound: Int = GameConfig.DUCKS_PER_ROUND
)
