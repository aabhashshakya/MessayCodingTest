package jp.co.ndk_group.messay_duck_hunt.ui.screens.game.composables

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import jp.co.ndk_group.messay_duck_hunt.constants.GameConfig
import jp.co.ndk_group.messay_duck_hunt.core.ui.GifPlayer
import jp.co.ndk_group.messay_duck_hunt.domain.models.Duck
import jp.co.ndk_group.messay_duck_hunt.domain.models.DuckDirection
import messayduckhunt.composeapp.generated.resources.Res

/// Created by Aabhash Shakya on 14/02/2026

@Composable
fun DuckSprite(
    duck: Duck,
    screenWidth: Dp,
    screenHeight: Dp,
    isFalling: Boolean
) {
    //fall animation
    val fallOffset by animateFloatAsState(
        targetValue = if (isFalling) 300f else 0f,
        animationSpec = tween(GameConfig.DUCK_FALL_DURATION_MS.toInt()),
        label = "fall_animation"
    )

    val fallRotation by animateFloatAsState(
        targetValue = if (isFalling) 180f else 0f,
        animationSpec = tween(GameConfig.DUCK_FALL_DURATION_MS.toInt()),
        label = "fall_rotation"
    )

    //converting normalized position to actual pixels
    val actualX = screenWidth * duck.position.x
    val actualY = screenHeight * duck.position.y + fallOffset.dp

    val duckGif = when (duck.duckDirection) {
        DuckDirection.LEFT_TO_RIGHT, DuckDirection.DIAGONAL_UP_RIGHT, DuckDirection.DIAGONAL_DOWN_RIGHT -> "drawable/duck_ltr.gif"
        DuckDirection.RIGHT_TO_LEFT, DuckDirection.DIAGONAL_DOWN_LEFT, DuckDirection.DIAGONAL_UP_LEFT -> "drawable/duck_rtl.gif"
    }


    GifPlayer(
        url = Res.getUri(duckGif),
        modifier = Modifier
            .offset(actualX - GameConfig.DUCK_SIZE / 2, actualY - GameConfig.DUCK_SIZE / 2)
            .size(GameConfig.DUCK_SIZE)
            .graphicsLayer {
                rotationZ = fallRotation
            }, contentScale = ContentScale.Crop,
        contentDescription = "Duck"
    )

}