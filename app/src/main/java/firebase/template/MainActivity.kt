package firebase.template

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import firebase.template.ui.theme.FirebaseTemplateTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FirebaseTemplateTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainSurface {
                        AnimatedTransitionDialog(modifier = Modifier
                            .background(Color.Transparent)
                            .fillMaxSize(),
                            onDismissRequest = {
                        },
                            content = {
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = colorResource(R.color.white)
                                ) {
                                    Box(modifier = Modifier
                                    ) {
                                        Column(modifier = Modifier
                                            .fillMaxSize(),
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.SpaceBetween)
                                        {
                                            AngledSwipeToDismissSimplified()
//                                            SwipeToDismissExample()
                                        }
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MainSurface(
    modifier: Modifier = Modifier,
    color: Color = Color.White,
    content: @Composable () -> Unit,
) {
    Surface(
        modifier = modifier,
        color = color
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            content()
        }
    }
}

@OptIn(ExperimentalMaterialApi::class) // Correct OptIn
@Composable
fun SwipeToDismissExample() {
    var isVisible by remember { mutableStateOf(true) }

    if (isVisible) {
        val dismissState = rememberDismissState(DismissValue.Default)
        if (dismissState.isDismissed(DismissDirection.EndToStart) || dismissState.isDismissed(DismissDirection.StartToEnd)) {
            isVisible = false
        }
        SwipeToDismiss(
            state = dismissState,
            modifier = Modifier
                .padding(vertical = Dp(4f)),
            directions = setOf(
                DismissDirection.StartToEnd,
                DismissDirection.EndToStart
            ),
            background = {
                Card(
                    Modifier
                        .fillMaxSize()
                        .background(Color.Red)
                ) {
                    //Content for area behind swipe.
                    Text(text = "HELLO")
                }
            },
            dismissContent = {
                Card(
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = Dp(2f)
                    )
                ) {
                    Box(
                        Modifier
                            .fillMaxSize()
                            .background(Color.LightGray),
                        contentAlignment = Alignment.Center
                    ) {
                        //Content of swipe itself.
                        Text("Swipe me to dismiss", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        )
    }
}


@Composable
fun AngledSwipeToDismissSimplified() {
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }
    var rotation by remember { mutableStateOf(0f) }
    var isDismissed by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    if (!isDismissed) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .width(300.dp)
                    .height(400.dp)
                    .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                    .rotate(rotation)
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            offsetX += dragAmount.x
                            offsetY += dragAmount.y
                            rotation = calculateRotation(offsetX)
                        }
                    }
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDrag = { change: PointerInputChange, dragAmount: Offset -> // Correct onDrag lambda
                                change.consume()
                                offsetX += dragAmount.x
                                offsetY += dragAmount.y
                                rotation = calculateRotation(offsetX) },
                                onDragEnd = {
                                val threshold = 500f
                                if (offsetX > threshold || offsetX < -threshold || offsetY > threshold || offsetY < -threshold) {
                                    isDismissed = true
                                } else {
                                    coroutineScope.launch {
                                        // Animate back to original position (Simplified)
                                        val animSpec = tween<Float>(durationMillis = 300)
                                        launch {
                                            animate(initialValue = offsetX, targetValue = 0f, animationSpec = animSpec) { value, _ -> offsetX = value }
                                        }
                                        launch {
                                            animate(initialValue = offsetY, targetValue = 0f, animationSpec = animSpec) { value, _ -> offsetY = value }
                                        }
                                        launch {
                                            animate(initialValue = rotation, targetValue = 0f, animationSpec = animSpec) { value, _ -> rotation = value }
                                        }
                                    }
                                }
                            }
                        )
                    },
                backgroundColor = Color.LightGray
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Swipe Me!", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

private fun calculateRotation(offsetX: Float): Float {
    val maxRotation = 15f
    return (offsetX / 500f) * maxRotation
}