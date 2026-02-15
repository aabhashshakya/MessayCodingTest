package jp.co.ndk_group.messay_duck_hunt.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import jp.co.ndk_group.messay_duck_hunt.constants.GameConfig
import jp.co.ndk_group.messay_duck_hunt.core.ui.GifPlayer
import jp.co.ndk_group.messay_duck_hunt.ui.screens.game.composables.GameBackground
import messayduckhunt.composeapp.generated.resources.Res

/// Created by Aabhash Shakya on 14/02/2026
@Composable
fun MenuScreen(
    onStartGame: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
,        contentAlignment = Alignment.Center
    ) {
        GameBackground()
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row {
                GifPlayer(
                    url = Res.getUri("drawable/duck_ltr.gif"),
                    modifier = Modifier
                        .size(GameConfig.DUCK_SIZE), contentScale = ContentScale.Crop,
                    contentDescription = "Duck"
                )
                GifPlayer(
                    url = Res.getUri("drawable/duck_rtl.gif"),
                    modifier = Modifier
                        .size(GameConfig.DUCK_SIZE)
                    ,contentScale = ContentScale.Crop,
                    contentDescription = "Duck"
                )
            }
            // Title
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(0.dp),
                modifier = Modifier.offset(y= (-10).dp)
            ) {
                Text(
                    text = "DUCK",
                    fontSize = 64.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFFA0290A),
                    textAlign = TextAlign.Center,
                    letterSpacing = 6.sp,
                    style = TextStyle(
                        shadow = Shadow(
                            color = Color.Black,
                            offset = Offset(4f, 4f),
                            blurRadius = 0f
                        )
                    )
                )
                Text(
                    text = "HUNT",
                    fontSize = 64.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF0D6853),
                    textAlign = TextAlign.Center,
                    letterSpacing = 6.sp,
                    style = TextStyle(
                        shadow = Shadow(
                            color = Color.Black,
                            offset = Offset(4f, 4f),
                            blurRadius = 0f
                        )
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            //instructions
            Column(
                modifier = Modifier
                    .background(Color.White.copy(alpha = 0.8f), RoundedCornerShape(16.dp))
                    .padding(24.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "HOW TO PLAY:",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                InstructionItem("\uD83D\uDE42🎯  Move your face to aim the reticle")
                InstructionItem("👁️👁️  Close your eyes briefly to shoot")
            }

            Spacer(modifier = Modifier.height(24.dp))

            RoundedButton(
                text = "START GAME",
                onClick = onStartGame
            )
        }
    }
}

@Composable
fun InstructionItem(text: String) {
    Text(
        text = text,
        fontSize = 18.sp,
        color = Color.Black.copy(alpha = 0.8f)
    )
}


@Composable
fun RoundedButton(
    text: String,
    color: Color = Color(0xFF162FE3),
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors().copy(containerColor = color),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = text,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 32.dp, vertical = 8.dp)
        )
    }
}