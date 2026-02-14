package jp.co.ndk_group.messay_duck_hunt.ui.screens.game

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import jp.co.ndk_group.messay_duck_hunt.presentation.DuckHuntState
import jp.co.ndk_group.messay_duck_hunt.ui.screens.game.composables.DuckSprite
import jp.co.ndk_group.messay_duck_hunt.ui.screens.game.composables.GameBackground
import jp.co.ndk_group.messay_duck_hunt.ui.screens.game.composables.GameHUD
import jp.co.ndk_group.messay_duck_hunt.ui.screens.game.composables.GunBarrel
import jp.co.ndk_group.messay_duck_hunt.ui.screens.game.composables.HitEffect
import jp.co.ndk_group.messay_duck_hunt.ui.screens.game.composables.MissEffect
import jp.co.ndk_group.messay_duck_hunt.ui.screens.game.composables.Reticle

/// Created by Aabhash Shakya on 14/02/2026
@Composable
fun DuckHuntGameScreen(
    state: DuckHuntState,
    screenWidth: Dp,
    screenHeight: Dp,
    modifier: Modifier = Modifier.Companion
) {
    Box(modifier = modifier.fillMaxSize()) {
        GameBackground()

        state.currentDuck?.let { duck ->
            DuckSprite(
                duck = duck,
                screenWidth = screenWidth,
                screenHeight = screenHeight,
                isFalling = duck.isFalling
            )
        }

        GunBarrel(
            reticleX = screenWidth * state.reticlePosition.x,
        )

        Reticle(
            normalizedPosition = state.reticlePosition,
            screenWidth = screenWidth,
            screenHeight = screenHeight
        )

        GameHUD(
            score = state.stats.score,
            ducksRemaining = state.stats.ducksRemainingInRound,
            totalDucks = state.stats.totalDucksInRound,
            level = state.stats.currentLevel,
            modifier = Modifier.align(Alignment.TopCenter)
        )

        //flash screen when duck hit or miss
        if (state.showHitEffect) {
            HitEffect()
        }
        if (state.showMissEffect) {
            MissEffect()
        }
    }
}