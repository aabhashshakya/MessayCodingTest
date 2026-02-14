package jp.co.ndk_group.messay_duck_hunt.ui.screens.game.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import messayduckhunt.composeapp.generated.resources.Res
import messayduckhunt.composeapp.generated.resources.background
import org.jetbrains.compose.resources.painterResource

/// Created by Aabhash Shakya on 14/02/2026

@Composable
fun GameBackground() {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(Res.drawable.background),
            contentDescription = "Game Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

    }
}