package firebase.template

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import firebase.template.ui.theme.FirebaseTemplateTheme
import firebase.template.ui.theme.NotePad
import firebase.template.ui.theme.ViewModel

private lateinit var viewModel : ViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModel()
        val notePad = NotePad(viewModel)

        setContent {
            FirebaseTemplateTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainSurface {
                        notePad.NoteBoard()
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