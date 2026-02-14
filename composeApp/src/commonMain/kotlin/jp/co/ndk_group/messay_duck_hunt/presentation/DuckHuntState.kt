package jp.co.ndk_group.messay_duck_hunt.presentation

import androidx.compose.ui.geometry.Offset
import jp.co.ndk_group.messay_duck_hunt.constants.GameConfig
import jp.co.ndk_group.messay_duck_hunt.domain.models.DifficultySettings
import jp.co.ndk_group.messay_duck_hunt.domain.models.Duck
import jp.co.ndk_group.messay_duck_hunt.domain.models.GameState
import jp.co.ndk_group.messay_duck_hunt.domain.models.GameStats

/// Created by Aabhash Shakya on 14/02/2026
data class DuckHuntState(
    val gameState: GameState = GameState.MENU,
    val stats: GameStats = GameStats(),
    val difficulty: DifficultySettings,
    val currentDuck: Duck? = null,
    val reticlePosition: Offset = Offset(0.5f, 0.5f), //normalized position
    val showHitEffect: Boolean = false,
    val showMissEffect: Boolean = false,
    val canShoot: Boolean = true
) {
    companion object {
        fun initial(): DuckHuntState {
            return DuckHuntState(
                difficulty = DifficultySettings(
                    level = 1,
                    duckSpeed = GameConfig.INITIAL_DUCK_SPEED * GameConfig.getLevelSpeedMultiplier(1),
                    flyDuration = (GameConfig.INITIAL_FLY_DURATION_MS * GameConfig.getLevelDurationMultiplier(
                        1
                    )).toLong(),
                    hitboxSize = GameConfig.INITIAL_HITBOX_SIZE * GameConfig.getLevelHitboxMultiplier(
                        1
                    )
                )
            )
        }
    }
}