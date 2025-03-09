package firebase.template
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.rememberDismissState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

private suspend fun startDismissWithExitAnimation(
    animateTrigger: MutableState<Boolean>,
    onDismissRequest: () -> Unit
) {
    animateTrigger.value = false
    //This is being applied to exit, not the value entered in animationExit, but it won't exceed what is in animationExit (e.g. 3000 here and 300 there will only use 300).
    delay(200)
    onDismissRequest()
}

@Composable
fun AnimatedComposable(
    disableBackHandler: Boolean = false,
    any: Any? = Unit,
    modifier: Modifier = Modifier,
    backHandler: () -> Unit,
    contentAnimated: @Composable () -> Unit = { },
){
    val coroutineScope: CoroutineScope = rememberCoroutineScope()
    val animateTrigger = remember { mutableStateOf(false) }

    BackHandler {
        if (!disableBackHandler) {
            coroutineScope.launch {
                startDismissWithExitAnimation(animateTrigger, backHandler)
            }
        }
    }

    LaunchedEffect(key1 = any) {
        launch {
            delay(200)
            animateTrigger.value = true
        }
    }

    Box(
        modifier = modifier
    ) {
        //Expand in/out cutting out halfway is behaving normally - it's what the animation is supposed to do.
        AnimatedScaleInTransition(
            animationEnter = fadeIn(animationSpec = tween(500)) + slideInHorizontally (
                animationSpec = tween(500)
            ),
            animationExit = fadeOut(animationSpec = tween(500)) + slideOutHorizontally(
                animationSpec = tween(500),
            ),
            visible = animateTrigger.value) {
            contentAnimated()
        }
    }
}

//Background color must be set in whichever columns/rows are being used in the content input, otherwise background will be the same as the Box here.
@Composable
fun AnimatedTransitionDialog(
    any: Any? = Unit,
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    content: @Composable () -> Unit,
) {
    val coroutineScope: CoroutineScope = rememberCoroutineScope()
    val animateTrigger = remember { mutableStateOf(false) }

    //This delays our animateTrigger value, meaning our Dialog box (the faded grey background) launches, but its children composables do not until the delay is over.
    LaunchedEffect(key1 = any) {
        launch {
            delay(0)
            animateTrigger.value = true
        }
    }

    Dialog(onDismissRequest = {
        coroutineScope.launch {
            startDismissWithExitAnimation(animateTrigger, onDismissRequest)
        }
    }
    ) {
        //Setting background to (Color.Transparent) through modifier prevents text from animating off first.
        Box(
            modifier = modifier
        ) {
            AnimatedScaleInTransition(
                animationEnter = fadeIn(animationSpec = tween(200)) + slideInHorizontally (
                    animationSpec = tween(200)
                ),
                animationExit = fadeOut(animationSpec = tween(200)) + slideOutHorizontally(
                    animationSpec = tween(200),
                ),
                visible = animateTrigger.value) {
                Row() {
                    Box() {
                        content()
                    }
                }
            }
        }
    }
}


@Composable
fun AnimatedDropDownMenu(
    expanded: Boolean,
    content: @Composable () -> Unit) {
    var isExpanded = expanded

    AnimatedScaleInTransition(
        animationEnter = fadeIn(animationSpec = tween(500)) + slideInHorizontally (
            animationSpec = tween(500)
        ),
        animationExit = fadeOut(animationSpec = tween(500)) + slideOutHorizontally(
            animationSpec = tween(500),
        ),
        visible = isExpanded) {
        DropdownMenu(expanded = expanded, onDismissRequest = { isExpanded = false }) {
            if (expanded) content()
        }
    }
}


@Composable
fun AnimatedScaleInTransition(
    animationEnter: EnterTransition,
    animationExit: ExitTransition,
    visible: Boolean,
    content: @Composable AnimatedVisibilityScope.() -> Unit
) {
    AnimatedVisibility(
        enter = animationEnter,
        exit = animationExit,
        visible = visible,
        content = content
    )
}

@OptIn(ExperimentalMaterialApi::class) // Correct OptIn
@Composable
fun SwipeToDismissExample() {
    var isVisible by remember { mutableStateOf(true) }

    if (isVisible) {
        val dismissState = rememberDismissState(DismissValue.Default)
        if (dismissState.isDismissed(DismissDirection.EndToStart) || dismissState.isDismissed(
                DismissDirection.StartToEnd)) {
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
fun NoteSwiper(emptyList: MutableList<NoteContents>) {
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
                            onDrag = { change: PointerInputChange, dragAmount: Offset ->
                                change.consume()
                                offsetX += dragAmount.x
                                offsetY += dragAmount.y
                                rotation = calculateRotation(offsetX)
                            },
                            onDragEnd = {
                                val threshold = 500f
                                if (offsetX > threshold || offsetX < -threshold || offsetY > threshold || offsetY < -threshold) {
                                    isDismissed = true
                                } else {
                                    coroutineScope.launch {
                                        // Animate back to original position
                                        val animSpec = tween<Float>(durationMillis = 300)
                                        launch {
                                            animate(
                                                initialValue = offsetX,
                                                targetValue = 0f,
                                                animationSpec = animSpec
                                            ) { value, _ -> offsetX = value }
                                        }
                                        launch {
                                            animate(
                                                initialValue = offsetY,
                                                targetValue = 0f,
                                                animationSpec = animSpec
                                            ) { value, _ -> offsetY = value }
                                        }
                                        launch {
                                            animate(
                                                initialValue = rotation,
                                                targetValue = 0f,
                                                animationSpec = animSpec
                                            ) { value, _ -> rotation = value }
                                        }
                                    }
                                }
                            }
                        )
                    },
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {

                }
            }
        }
    }
}

private fun calculateRotation(offsetX: Float): Float {
    val maxRotation = 15f
    return (offsetX / 500f) * maxRotation
}