package jp.co.ndk_group.messay_duck_hunt.ui.screens.game.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import messayduckhunt.composeapp.generated.resources.Res
import messayduckhunt.composeapp.generated.resources.gun
import org.jetbrains.compose.resources.painterResource

/// Created by Aabhash Shakya on 14/02/2026

@Composable
fun BoxScope.GunBarrel(
    reticleX: Dp,
) {
    val gunWidth = 360.dp
    Image(
        painter = painterResource(Res.drawable.gun),
        contentDescription = "Gun",
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .offset(x = reticleX - gunWidth / 2)
            .width(gunWidth)
            .height(200.dp),
        contentScale = ContentScale.Crop
    )
}