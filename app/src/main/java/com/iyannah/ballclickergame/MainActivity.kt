package com.iyannah.ballclickergame

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sqrt
import kotlin.random.Random

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HomeScreen()
        }
    }
}

@Composable
fun HomeScreen() {
    var point by remember {
        mutableIntStateOf(0)
    }

    var isRunning by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Point : $point", fontSize = 20.sp)
            Button(onClick = {
                isRunning = !isRunning
                point = 0
            }) {
                Text(text = if (isRunning) "Reset" else "Start")
            }
            CountTimer(isTimeRunning = isRunning) {
                isRunning = false
            }
        }

        BallClicker(enable = isRunning) {
            point++
        }
    }
}

@Composable
fun CountTimer(
    time: Long = 20000L,
    isTimeRunning: Boolean = false,
    onTimeEnd: () -> Unit
) {
    var curTime by remember {
        mutableLongStateOf(time)
    }
    val timeEnd by rememberUpdatedState(onTimeEnd)

    LaunchedEffect(key1 = curTime, key2 = isTimeRunning) {
        if (!isTimeRunning) {
            curTime = time
            return@LaunchedEffect
        }
        if (curTime > 0) {
            delay(1000L)
            curTime -= 1000L
        } else {
            timeEnd()
        }
    }

    Text(text = "${curTime / 1000L}", fontWeight = FontWeight.Bold, fontSize = 24.sp)
}

@Composable
fun BallClicker(
    radius: Float = 100f,
    enable: Boolean = false,
    ballColor: Color = Color.Red,
    onBallClick: () -> Unit
) {

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {

        var ballPosition by remember {
            mutableStateOf(
                randomOffset(
                    radius = radius,
                    width = constraints.maxWidth,
                    height = constraints.maxHeight
                )
            )
        }

        Canvas(modifier = Modifier
            .fillMaxSize()
            .pointerInput(enable) {
                if (!enable) {
                    return@pointerInput
                }
                detectTapGestures {
                    val distance = sqrt(
                        (it.x - ballPosition.x).pow(2) +
                                (it.y - ballPosition.y).pow(2)
                    )

                    if (distance <= radius) {
                        ballPosition = randomOffset(
                            radius = radius,
                            width = constraints.maxWidth,
                            height = constraints.maxHeight
                        )
                        onBallClick()
                    }
                }
            }) {
            drawCircle(
                color = ballColor,
                radius = radius,
                center = ballPosition
            )
        }
    }
}

private fun randomOffset(radius: Float, width: Int, height: Int): Offset {
    return Offset(
        x = Random.nextInt(radius.roundToInt(), width - radius.roundToInt()).toFloat(),
        y = Random.nextInt(radius.roundToInt(), height - radius.roundToInt()).toFloat()
    )
}