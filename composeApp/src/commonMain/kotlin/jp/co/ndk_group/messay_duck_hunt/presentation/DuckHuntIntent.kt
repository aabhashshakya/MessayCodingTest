package jp.co.ndk_group.messay_duck_hunt.presentation

/// Created by Aabhash Shakya on 14/02/2026
sealed interface DuckHuntIntent {
    data object StartGame : DuckHuntIntent
    data object RestartGame : DuckHuntIntent
    data object NextLevel : DuckHuntIntent
    data object Shoot : DuckHuntIntent
    data class UpdateReticlePosition(val normalizedX: Float, val normalizedY: Float) : DuckHuntIntent
    data class UpdateDuckPosition(val deltaTime: Long) : DuckHuntIntent
    data object DuckEscaped : DuckHuntIntent
    data object SpawnNextDuck : DuckHuntIntent
}