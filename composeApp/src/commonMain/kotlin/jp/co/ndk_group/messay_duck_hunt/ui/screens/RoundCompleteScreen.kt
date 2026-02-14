package jp.co.ndk_group.messay_duck_hunt.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import jp.co.ndk_group.messay_duck_hunt.constants.GameConfig
import jp.co.ndk_group.messay_duck_hunt.presentation.DuckHuntState
import jp.co.ndk_group.messay_duck_hunt.ui.screens.game.composables.GameBackground

/// Created by Aabhash Shakya on 14/02/2026
@Composable
fun RoundCompleteScreen(
    state: DuckHuntState,
    onNextLevel: () -> Unit,
    onRestart: () -> Unit,
    modifier: Modifier = Modifier
) {
    val totalDucks = state.stats.ducksHit + state.stats.ducksMissed
    val accuracy = if (totalDucks > 0) (state.stats.ducksHit * 100) / totalDucks else 0
    val passed = accuracy >= 50

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF87CEEB)),
        contentAlignment = Alignment.Center
    ) {
        GameBackground()
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp),
            modifier = Modifier
                .background(Color.White.copy(alpha = 0.7f), RoundedCornerShape(16.dp))
                .padding(48.dp)
        ) {
            Text(
                text = if (passed) "ROUND COMPLETE!" else "ROUND FAILED",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = if (passed) Color(0xFF228B22) else Color(0xFFFF4444)
            )

            if (!passed) {
                Text(
                    text = "Need 50% accuracy to advance",
                    fontSize = 20.sp,
                    color = Color(0xFFFF4444),
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            //stats
            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatText("Score", state.stats.score.toString(), Color(0xFFFAD000))
                StatText("Ducks Hit", state.stats.ducksHit.toString(), Color(0xFF228B22))
                StatText("Ducks Missed", state.stats.ducksMissed.toString(), Color(0xFFFF4444))
                StatText(
                    "Accuracy",
                    "$accuracy%",
                    if (passed) Color(0xFF228B22) else Color(0xFFFF4444)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (passed && state.stats.currentLevel < GameConfig.LEVELS) {
                RoundedButton(
                    text = "NEXT LEVEL",
                    onClick = onNextLevel
                )
            } else if (passed) {
                Text(
                    text = "🏆 YOU WIN! 🏆",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFFD700)
                )
            }

            RoundedButton(
                text = "RESTART",
                color = Color.Red,
                onClick = onRestart
            )
        }
    }
}

@Composable
fun StatText(label: String, value: String, color: Color) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$label:",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black.copy(alpha = 0.7f),
            modifier = Modifier.width(150.dp)
        )
        Text(
            text = value,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}
