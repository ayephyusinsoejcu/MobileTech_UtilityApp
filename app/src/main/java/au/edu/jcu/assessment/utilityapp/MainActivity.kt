
package au.edu.jcu.assessment.utilityapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import au.edu.jcu.assessment.utilityapp.ui.theme.MobileTech_UtilityAppTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MobileTech_UtilityAppTheme {
                StudyTimerScreen()
            }
        }
    }
}

@Composable
fun StudyTimerScreen() {
    var remainingSeconds by remember { mutableIntStateOf(25 * 60) }
    var isRunning by remember { mutableStateOf(false) }

    LaunchedEffect(isRunning, remainingSeconds) {
        if (isRunning && remainingSeconds > 0) {
            delay(1000L)
            remainingSeconds--
        } else if (remainingSeconds == 0) {
            isRunning = false
        }
    }

    val minutes = remainingSeconds / 60
    val seconds = remainingSeconds % 60
    val formattedTime = "%02d:%02d".format(minutes, seconds)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Study Timer",
            style = MaterialTheme.typography.headlineMedium
        )

        Text("Stay focused and achieve your goals!")

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("FOCUS SESSION")

                Text(
                    formattedTime,
                    style = MaterialTheme.typography.displayLarge
                )

                Text(
                    when {
                        remainingSeconds == 0 -> "Session complete!"
                        isRunning -> "Keep focusing!"
                        else -> "Ready to study?"
                    }
                )
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = {
                    if (remainingSeconds > 0) {
                        isRunning = !isRunning
                    }
                },
                enabled = remainingSeconds > 0
            ) {
                Text(if (isRunning) "Pause" else "Start")
            }

            OutlinedButton(
                onClick = {
                    isRunning = false
                    remainingSeconds = 25 * 60
                }
            ) {
                Text("Reset")
            }
        }

        Text("Small steps every day lead to big results.")
    }
}

@Preview(showBackground = true)
@Composable
fun StudyTimerPreview() {
    MobileTech_UtilityAppTheme {
        StudyTimerScreen()
    }
}
