package firebase.template

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.unit.Dp

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
                                            SwipeToDismissExample()
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
                    //Background for the swipe
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
                            .fillMaxWidth()
                            .height(Dp(100f))
                            .background(Color.LightGray),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Swipe me to dismiss", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        )
    }
}