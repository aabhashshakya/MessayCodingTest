package jp.co.ndk_group.messay_coding_test

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import jp.co.ndk_group.mdk.MdkOptions
import jp.co.ndk_group.mdk.MdkResult
import jp.co.ndk_group.mdk.MdkTarget
import jp.co.ndk_group.mdk.MdkView
import jp.co.ndk_group.mdk.entity.MdkSide

private val eyeCloseHold = MdkTarget.EyeCloseHold(MdkSide.Unspecified)
private val faceMovement = MdkTarget.FaceMovement

private val optionsBuilder = MdkOptions.Builder()
    .setEnabledMdkTargets(
        setOf(
            eyeCloseHold,
            faceMovement,
        )
    )
    .setActionParams(
        eyeCloseHold,
        MdkOptions.HorizontalPairedHoldActionParams(
            thresholdRatio = { side ->
                when (side) {
                    MdkSide.Left -> 0.6f
                    MdkSide.Right -> 0.6f
                }
            },
            requiredMillis = { count -> if (count == 1) 500 else 1_000 },
        ),
    )
    .setActionParams(
        faceMovement,
        MdkOptions.MovementActionParams(
            blinkThresholdRatio = { side ->
                when (side) {
                    MdkSide.Left -> 0.6f
                    MdkSide.Right -> 0.6f
                }
            },
            sensitivityFactor = {
                when (it) {
                    MdkSide.Axis.Horizontal -> 1f
                    MdkSide.Axis.Vertical -> 1f
                }
            },
        )
    )

@Composable
fun Sample() {

    val hapticFeedback = LocalHapticFeedback.current

    var holdHistory by remember {
        mutableStateOf(History())
    }

    var repeatHistory by remember {
        mutableStateOf(History())
    }

    BoxWithConstraints(
        modifier = Modifier.fillMaxSize()
    ) {
        // BoxWithConstraintsScopeから利用可能な最大幅・高さを取得する。
        val screenWidth = maxWidth
        val screenHeight = maxHeight

        // 画面サイズが変更された場合に初期値を再計算するため、keyにサイズを指定する。
        var pointerPositionX by remember(screenWidth) {
            mutableStateOf(screenWidth / 2)
        }

        var pointerPositionY by remember(screenHeight) {
            mutableStateOf(screenHeight / 2)
        }

        Box {
            Column {
                MdkView(
                    optionsBuilder
                        .setListener {
                            when (val hold = eyeCloseHold.currentState()) {
                                is MdkResult.ScalarActionState.CountUp -> {
                                    holdHistory = holdHistory.copy(
                                        currentCount = hold.count,
                                        lastCount = hold.count,
                                    )
                                    hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                }

                                is MdkResult.ScalarActionState.End -> {
                                    if (hold.count > 0) {
                                        holdHistory = holdHistory.copy(
                                            currentCount = null,
                                            lastCount = hold.count,
                                            history = holdHistory.history + hold.count
                                        )
                                    }
                                }

                                is MdkResult.ScalarActionState.None -> {
                                    holdHistory = holdHistory.copy(
                                        currentCount = null,
                                    )
                                }

                                else -> {
                                }
                            }

                            val normalizedMovementState = faceMovement.currentState()
                            pointerPositionX =
                                screenWidth * normalizedMovementState.x
                            pointerPositionY =
                                screenWidth * normalizedMovementState.y
                        }
                        .build(),
                    Modifier.weight(1f),
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(10.dp)
                        .weight(1f)
                ) {

                    HistoryView("hold", holdHistory)
                    HistoryView("repeat", repeatHistory)

                }
            }

            Pointer(
                pointerPositionX = { pointerPositionX },
                pointerPositionY = { pointerPositionY },
            )

        }
    }
}

@Composable
private fun Pointer(
    pointerPositionX: () -> Dp,
    pointerPositionY: () -> Dp,
    modifier: Modifier = Modifier,
) {
    // Pointer
    Box(
        modifier
            .offset(pointerPositionX() - 10.dp, pointerPositionY() - 10.dp)
            .size(20.dp)
            .background(Color.Red, CircleShape)
    )
}

data class History(
    val currentCount: Int? = null,
    val lastCount: Int? = null,
    val history: List<Int> = emptyList(),
)

@Composable
fun HistoryView(
    name: String,
    history: History,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {

        Text(name, style = MaterialTheme.typography.titleMedium)

        Row {
            Text("current: ${history.currentCount}, ")
            Text("last: ${history.lastCount}")
        }

        Text(
            "history: ${history.history.takeLast(10).joinToString(",")}",
        )

    }
}
