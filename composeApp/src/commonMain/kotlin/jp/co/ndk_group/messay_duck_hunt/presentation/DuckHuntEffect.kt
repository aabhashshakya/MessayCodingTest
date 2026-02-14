package jp.co.ndk_group.messay_duck_hunt.presentation

/// Created by Aabhash Shakya on 14/02/2026
sealed interface DuckHuntEffect {
    data object PlayShootSound : DuckHuntEffect
    data object PlayHitSound : DuckHuntEffect
    data object PlayDuckLaughSound : DuckHuntEffect
    data object VibrateOnHit : DuckHuntEffect
}