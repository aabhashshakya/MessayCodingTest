package jp.co.ndk_group.messay_duck_hunt

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import jp.co.ndk_group.messay_duck_hunt.constants.GameConfig
import jp.co.ndk_group.messay_duck_hunt.domain.models.GameState
import jp.co.ndk_group.messay_duck_hunt.presentation.DuckHuntEffect
import jp.co.ndk_group.messay_duck_hunt.presentation.DuckHuntIntent
import jp.co.ndk_group.messay_duck_hunt.presentation.vm.DuckHuntViewModel
import jp.co.ndk_group.messay_duck_hunt.ui.screens.game.DuckHuntGameScreen
import jp.co.ndk_group.mdk.MdkOptions
import jp.co.ndk_group.mdk.MdkResult
import jp.co.ndk_group.mdk.MdkTarget
import jp.co.ndk_group.mdk.MdkView
import jp.co.ndk_group.mdk.entity.MdkSide
import jp.co.ndk_group.messay_duck_hunt.core.sound.createAudioPlayer
import jp.co.ndk_group.messay_duck_hunt.core.utils.BackHandler
import jp.co.ndk_group.messay_duck_hunt.ui.dialogs.QuitGameDialog
import jp.co.ndk_group.messay_duck_hunt.ui.screens.MenuScreen
import jp.co.ndk_group.messay_duck_hunt.ui.screens.RoundCompleteScreen
import kotlinx.coroutines.delay
import messayduckhunt.composeapp.generated.resources.Res
import kotlin.time.Clock

/// Created by Aabhash Shakya on 14/02/2026
@Composable
fun App() {
    val audioPlayer = createAudioPlayer()
    val viewModel: DuckHuntViewModel = viewModel { DuckHuntViewModel() }
    val state = viewModel.state
    val hapticFeedback = LocalHapticFeedback.current

    //Dialog state
    var showQuitDialog by remember { mutableStateOf(false) }

    //MDK targets
    val eyeCloseHold = remember { MdkTarget.EyeCloseHold(MdkSide.Both) }
    val faceMovement = remember { MdkTarget.FaceMovement }

    //Back press handler
    BackHandler(enabled = state.gameState != GameState.MENU) {
        showQuitDialog = true
    }

    //collecting and playing effects
    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                DuckHuntEffect.VibrateOnHit -> {
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                }

                DuckHuntEffect.PlayDuckLaughSound -> audioPlayer.playSound(Res.getUri("files/duck_laugh.mp3"))
                DuckHuntEffect.PlayHitSound -> audioPlayer.playSound(Res.getUri("files/duck_shot.mp3"))
                DuckHuntEffect.PlayShootSound -> audioPlayer.playSound(Res.getUri("files/gunshot.mp3"))
            }
        }
    }

    //game loop for duck movement
    LaunchedEffect(state.gameState) {
        if (state.gameState == GameState.PLAYING) {
            var lastTime = Clock.System.now().toEpochMilliseconds()
            while (state.gameState == GameState.PLAYING) {
                val currentTime = Clock.System.now().toEpochMilliseconds()
                val deltaTime = currentTime - lastTime
                lastTime = currentTime

                viewModel.handleIntent(DuckHuntIntent.UpdateDuckPosition(deltaTime))
                delay(16) // ~60 FPS movement, as 1 frame = 16 ms
            }
        }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val screenWidth = maxWidth
        val screenHeight = maxHeight

        Box(modifier = Modifier.fillMaxSize()) {
            // Hidden MDK camera view
            MdkView(
                MdkOptions.Builder()
                    .setEnabledMdkTargets(setOf(eyeCloseHold, faceMovement))
                    .setActionParams(
                        eyeCloseHold,
                        MdkOptions.HorizontalPairedHoldActionParams(
                            thresholdRatio = { _ ->
                                GameConfig.EYE_CLOSE_THRESHOLD
                            },
                            requiredMillis = { _ ->
                                GameConfig.SHOOT_REQUIRED_MILLIS
                            }
                        )
                    )
                    .setActionParams(
                        faceMovement,
                        MdkOptions.MovementActionParams(
                            blinkThresholdRatio = { _ ->
                                GameConfig.EYE_CLOSE_THRESHOLD
                            },
                            sensitivityFactor = {
                                when (it) {
                                    MdkSide.Axis.Horizontal -> GameConfig.FACE_MOVEMENT_SENSITIVITY_HORIZONTAL
                                    MdkSide.Axis.Vertical -> GameConfig.FACE_MOVEMENT_SENSITIVITY_VERTICAL
                                }
                            }
                        )
                    )
                    .setListener {
                        // update face movement for reticle position
                        val movement = faceMovement.currentState()
                        viewModel.handleIntent(
                            DuckHuntIntent.UpdateReticlePosition(
                                normalizedX = movement.x,
                                normalizedY = movement.y
                            )
                        )

                        //handle eye close for shooting
                        when (eyeCloseHold.currentState()) {
                            is MdkResult.ScalarActionState.Start -> {
                                viewModel.handleIntent(DuckHuntIntent.Shoot)
                            }
                            else -> {}
                        }
                    }
                    .build(),
                modifier = Modifier
                    .size(1.dp)
                    .alpha(0f)
            )

            //Game UI based on state
            when (state.gameState) {
                GameState.MENU -> {
                    MenuScreen(
                        onStartGame = {
                            viewModel.handleIntent(DuckHuntIntent.StartGame)
                        }
                    )
                }

                GameState.PLAYING -> {
                    DuckHuntGameScreen(
                        state = state,
                        screenWidth = screenWidth,
                        screenHeight = screenHeight
                    )
                }

                GameState.ROUND_COMPLETE, GameState.GAME_OVER -> {
                    RoundCompleteScreen(
                        state = state,
                        onNextLevel = {
                            viewModel.handleIntent(DuckHuntIntent.NextLevel)
                        },
                        onRestart = {
                            viewModel.handleIntent(DuckHuntIntent.RestartGame)
                        }
                    )
                }
            }

            // Quit Game Dialog
            if (showQuitDialog) {
                QuitGameDialog(
                    onDismiss = { showQuitDialog = false },
                    onQuit = {
                        showQuitDialog = false
                        viewModel.handleIntent(DuckHuntIntent.QuitToMenu)
                    }
                )
            }
        }
    }

    //release audio player when app is disposed
    DisposableEffect(Unit) {
        onDispose {
            audioPlayer.release()
        }
    }
}
