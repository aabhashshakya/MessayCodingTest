package jp.co.ndk_group.messay_duck_hunt.presentation.vm

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import jp.co.ndk_group.messay_duck_hunt.constants.GameConfig
import jp.co.ndk_group.messay_duck_hunt.domain.models.DifficultySettings
import jp.co.ndk_group.messay_duck_hunt.domain.models.Duck
import jp.co.ndk_group.messay_duck_hunt.domain.models.DuckDirection
import jp.co.ndk_group.messay_duck_hunt.domain.models.GameState
import jp.co.ndk_group.messay_duck_hunt.presentation.DuckHuntEffect
import jp.co.ndk_group.messay_duck_hunt.presentation.DuckHuntIntent
import jp.co.ndk_group.messay_duck_hunt.presentation.DuckHuntState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlin.math.sqrt
import kotlin.random.Random


/// Created by Aabhash Shakya on 14/02/2026
class DuckHuntViewModel : ViewModel() {

    // State
    var state by mutableStateOf(DuckHuntState.initial())
        private set

    // Effects channel
    private val _effects = Channel<DuckHuntEffect>(Channel.Factory.BUFFERED)
    val effects: Flow<DuckHuntEffect> = _effects.receiveAsFlow()

    private var duckIdCounter = 0
    private var featherIdCounter = 0


    fun handleIntent(intent: DuckHuntIntent) {
        when (intent) {
            is DuckHuntIntent.StartGame -> startGame()
            is DuckHuntIntent.RestartGame -> restartGame()
            is DuckHuntIntent.NextLevel -> nextLevel()
            is DuckHuntIntent.Shoot -> shoot()
            is DuckHuntIntent.UpdateReticlePosition -> updateReticlePosition(
                intent.normalizedX,
                intent.normalizedY
            )

            is DuckHuntIntent.UpdateDuckPosition -> updateDuckPosition(intent.deltaTime)
            is DuckHuntIntent.DuckEscaped -> duckEscaped()
            is DuckHuntIntent.SpawnNextDuck -> spawnNextDuck()
        }
    }

    private fun startGame() {
        state = DuckHuntState.initial().copy(
            gameState = GameState.PLAYING
        )
        spawnNextDuck()
    }

    private fun restartGame() {
        duckIdCounter = 0
        featherIdCounter = 0
        startGame()
    }

    private fun nextLevel() {
        // Check if player achieved at least 50% accuracy for THIS ROUND
        val totalDucks = state.stats.ducksHitThisRound + state.stats.ducksMissedThisRound
        val accuracy = if (totalDucks > 0) (state.stats.ducksHitThisRound * 100) / totalDucks else 0

        if (accuracy < 50) {
            // Failed to meet requirement, game over
            state = state.copy(gameState = GameState.GAME_OVER)
            return
        }

        val newLevel = state.stats.currentLevel + 1
        if (newLevel > GameConfig.LEVELS) {
            // Game complete
            state = state.copy(gameState = GameState.GAME_OVER)
        } else {
            val newDifficulty = DifficultySettings(
                level = newLevel,
                duckSpeed = GameConfig.INITIAL_DUCK_SPEED * GameConfig.getLevelSpeedMultiplier(
                    newLevel
                ),
                flyDuration = (GameConfig.INITIAL_FLY_DURATION_MS * GameConfig.getLevelDurationMultiplier(
                    newLevel
                )).toLong(),
                hitboxSize = GameConfig.INITIAL_HITBOX_SIZE * GameConfig.getLevelHitboxMultiplier(
                    newLevel
                )
            )

            state = state.copy(
                gameState = GameState.PLAYING,
                stats = state.stats.copy(
                    currentLevel = newLevel,
                    ducksRemainingInRound = GameConfig.DUCKS_PER_ROUND,
                    // Reset round-specific stats for new round
                    ducksHitThisRound = 0,
                    ducksMissedThisRound = 0
                ),
                difficulty = newDifficulty
            )
            spawnNextDuck()
        }
    }

    private fun shoot() {
        if (!state.canShoot || state.currentDuck == null || state.gameState != GameState.PLAYING) {
            return
        }

        sendEffect(DuckHuntEffect.PlayShootSound)

        val duck = state.currentDuck ?: return

        //if we shot the same duck multiple times, dont count as the duck being hit
        if (!duck.isAlive || duck.isFalling) {
            handleMiss()
            // Prevent rapid fire
            state = state.copy(canShoot = false)
            viewModelScope.launch {
                delay(100)
                state = state.copy(canShoot = true)
            }
            return
        }

        // Check collision
        if (checkCollision(state.reticlePosition, duck)) {
            // Hit!
            handleDuckHit(duck)
        } else {
            // Miss
            handleMiss()
        }

        // Prevent rapid fire
        state = state.copy(canShoot = false)
        viewModelScope.launch {
            delay(100)
            state = state.copy(canShoot = true)
        }
    }

    private fun checkCollision(reticleNormalized: Offset, duck: Duck): Boolean {
        // Both reticle and duck use normalized coordinates (0-1)
        // Calculate distance in normalized space
        val dx = reticleNormalized.x - duck.position.x
        val dy = reticleNormalized.y - duck.position.y
        val distance = sqrt(dx * dx + dy * dy)

        // Hitbox size as a fraction of screen (roughly 0.1 = 10% of screen)
        val hitboxNormalized = 0.08f

        return distance < hitboxNormalized
    }

    private fun handleDuckHit(duck: Duck) {
        sendEffect(DuckHuntEffect.PlayHitSound)
        sendEffect(DuckHuntEffect.VibrateOnHit)

        // Calculate score based on difficulty
        val difficultyBonus =
            (state.difficulty.level-1) * GameConfig.BONUS_POINTS_PER_DIFFICULTY
        val points = GameConfig.BASE_POINTS  + difficultyBonus


        // Update state - track both round-specific and total stats
        val newStats = state.stats.copy(
            score = state.stats.score + points,
            ducksHitThisRound = state.stats.ducksHitThisRound + 1,
            totalDucksHit = state.stats.totalDucksHit + 1,
            ducksRemainingInRound = state.stats.ducksRemainingInRound - 1
        )

        val newDifficulty = state.difficulty.copy(
            successfulHits = state.difficulty.successfulHits + 1,
            duckSpeed = state.difficulty.duckSpeed + GameConfig.DUCK_SPEED_INCREASE_PER_HIT,
        )

        state = state.copy(
            currentDuck = duck.copy(isFalling = true, isAlive = false),
            stats = newStats,
            difficulty = newDifficulty,
            showHitEffect = true
        )

        // Hide hit effect after animation
        viewModelScope.launch {
            delay(GameConfig.DUCK_FALL_DURATION_MS)
            state = state.copy(showHitEffect = false, currentDuck = null)

            if (newStats.ducksRemainingInRound <= 0) {
                // Round complete
                state = state.copy(gameState = GameState.ROUND_COMPLETE)
            } else {
                spawnNextDuck()
            }
        }
    }

    private fun handleMiss() {
        state = state.copy(showMissEffect = true)
        viewModelScope.launch {
            delay(200)
            state = state.copy(showMissEffect = false)
        }
    }

    private fun duckEscaped() {
        if (state.currentDuck == null) return

        sendEffect(DuckHuntEffect.PlayDuckLaughSound)

        // Update both round-specific and total stats
        val newStats = state.stats.copy(
            ducksMissedThisRound = state.stats.ducksMissedThisRound + 1,
            totalDucksMissed = state.stats.totalDucksMissed + 1,
            ducksRemainingInRound = state.stats.ducksRemainingInRound - 1
        )

        state = state.copy(
            currentDuck = null,
            stats = newStats
        )

        if (newStats.ducksRemainingInRound <= 0) {
            state = state.copy(gameState = GameState.ROUND_COMPLETE)
        } else {
            spawnNextDuck()
        }
    }

    private fun spawnNextDuck() {
        if (state.stats.ducksRemainingInRound <= 0) return

        val direction = DuckDirection.entries.toTypedArray().random()
        val startPosition = getStartPosition(direction)
        val velocity = getVelocityForDirection(direction, state.difficulty.duckSpeed)

        val duck = Duck(
            id = duckIdCounter++,
            position = startPosition,
            velocity = velocity,
            hitboxSize = state.difficulty.hitboxSize,
            duckDirection = direction
        )

        state = state.copy(currentDuck = duck)

        // Schedule duck escape after fly duration
        viewModelScope.launch {
            delay(state.difficulty.flyDuration)
            if (state.currentDuck?.id == duck.id && state.currentDuck?.isAlive == true) {
                handleIntent(DuckHuntIntent.DuckEscaped)
            }
        }
    }

    private fun updateReticlePosition(normalizedX: Float, normalizedY: Float) {
        state = state.copy(
            reticlePosition = Offset(
                normalizedX.coerceIn(0f, 1f),
                normalizedY.coerceIn(0f, 1f)
            )
        )
    }

    private fun updateDuckPosition(deltaTime: Long) {
        val duck = state.currentDuck ?: return
        if (!duck.isAlive || duck.isFalling) return

        val deltaSeconds = deltaTime / 1000f
        val newPosition = Offset(
            duck.position.x + duck.velocity.x * deltaSeconds,
            duck.position.y + duck.velocity.y * deltaSeconds
        )

        state = state.copy(
            currentDuck = duck.copy(position = newPosition)
        )
    }

    private fun getStartPosition(direction: DuckDirection): Offset {
        return when (direction) {
            DuckDirection.LEFT_TO_RIGHT -> Offset(-0.1f, Random.Default.nextFloat() * 0.6f + 0.2f)
            DuckDirection.RIGHT_TO_LEFT -> Offset(1.1f, Random.Default.nextFloat() * 0.6f + 0.2f)
            DuckDirection.DIAGONAL_UP_RIGHT -> Offset(-0.1f, 0.8f)
            DuckDirection.DIAGONAL_UP_LEFT -> Offset(1.1f, 0.8f)
            DuckDirection.DIAGONAL_DOWN_RIGHT -> Offset(-0.1f, 0.1f)
            DuckDirection.DIAGONAL_DOWN_LEFT -> Offset(1.1f, 0.1f)
        }
    }

    private fun getVelocityForDirection(direction: DuckDirection, speed: Float): Offset {
        // Speed is now in normalized coordinates per second (0-1 scale)
        // Use 0.1 to 0.3 for good visible movement
        val normalizedSpeed = 0.15f + (speed / GameConfig.INITIAL_DUCK_SPEED) * 0.1f

        return when (direction) {
            DuckDirection.LEFT_TO_RIGHT -> Offset(normalizedSpeed, 0f)
            DuckDirection.RIGHT_TO_LEFT -> Offset(-normalizedSpeed, 0f)
            DuckDirection.DIAGONAL_UP_RIGHT -> Offset(
                normalizedSpeed * 0.7f,
                -normalizedSpeed * 0.7f
            )

            DuckDirection.DIAGONAL_UP_LEFT -> Offset(
                -normalizedSpeed * 0.7f,
                -normalizedSpeed * 0.7f
            )

            DuckDirection.DIAGONAL_DOWN_RIGHT -> Offset(
                normalizedSpeed * 0.7f,
                normalizedSpeed * 0.5f
            )

            DuckDirection.DIAGONAL_DOWN_LEFT -> Offset(
                -normalizedSpeed * 0.7f,
                normalizedSpeed * 0.5f
            )
        }
    }


    private fun sendEffect(effect: DuckHuntEffect) {
        viewModelScope.launch {
            _effects.send(effect)
        }
    }
}