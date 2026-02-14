package jp.co.ndk_group.messay_duck_hunt.ui.screens.game.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Dp
import jp.co.ndk_group.messay_duck_hunt.constants.GameConfig
import messayduckhunt.composeapp.generated.resources.Res
import messayduckhunt.composeapp.generated.resources.reticle
import org.jetbrains.compose.resources.painterResource

/// Created by Aabhash Shakya on 14/02/2026
@Composable
fun Reticle(
    normalizedPosition: Offset,
    screenWidth: Dp,
    screenHeight: Dp
) {
    val actualX = screenWidth * normalizedPosition.x
    val actualY = screenHeight * normalizedPosition.y

    Image(
        painter = painterResource(Res.drawable.reticle),
        contentDescription = "Reticle",
        modifier = Modifier.Companion
            .offset(actualX - GameConfig.RETICLE_SIZE / 2, actualY - GameConfig.RETICLE_SIZE / 2)
            .size(GameConfig.RETICLE_SIZE)
    )
}