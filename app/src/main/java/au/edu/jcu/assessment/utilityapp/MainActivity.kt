
package au.edu.jcu.assessment.utilityapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
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
                UtilityApp()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UtilityAppPreview() {
    MobileTech_UtilityAppTheme {
        UtilityApp()
    }
}

@Composable
fun UtilityApp() {
    var selectedTab by remember { mutableStateOf("Utility") }

    var remainingSeconds by remember {
        mutableIntStateOf(25 * 60)
    }
    var isRunning by remember { mutableStateOf(false) }

    LaunchedEffect(isRunning, remainingSeconds) {
        if (isRunning && remainingSeconds > 0) {
            delay(1000L)
            remainingSeconds--
        } else if (remainingSeconds == 0) {
            isRunning = false
        }
    }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = {
                        Icon(
                            Icons.Default.Home,
                            contentDescription = "Timer"
                        )
                    },
                    label = { Text("Timer") },
                    selected = selectedTab == "Utility",
                    onClick = { selectedTab = "Utility" }
                )

                NavigationBarItem(
                    icon = {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = "Settings"
                        )
                    },
                    label = { Text("Settings") },
                    selected = selectedTab == "Settings",
                    onClick = { selectedTab = "Settings" }
                )
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (selectedTab) {
                "Utility" -> UtilityScreen(
                    remainingSeconds = remainingSeconds,
                    isRunning = isRunning,
                    onStartPause = {
                        if (remainingSeconds > 0) {
                            isRunning = !isRunning
                        }
                    },
                    onReset = {
                        isRunning = false
                        remainingSeconds = 25 * 60
                    }
                )

                "Settings" -> SettingsScreen()
            }
        }
    }
}

@Composable
fun UtilityScreen(
    remainingSeconds: Int,
    isRunning: Boolean,
    onStartPause: () -> Unit,
    onReset: () -> Unit
) {
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
            text = "Study Timer",
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = "Stay focused and achieve your goals!",
            style = MaterialTheme.typography.bodyMedium
        )

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "FOCUS SESSION",
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = formattedTime,
                    style = MaterialTheme.typography.displayLarge
                )

                Text(
                    text = when {
                        remainingSeconds == 0 -> "Session complete!"
                        isRunning -> "Keep focusing!"
                        else -> "Ready to study?"
                    },
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = onStartPause,
                enabled = remainingSeconds > 0
            ) {
                Text(if (isRunning) "Pause" else "Start")
            }

            OutlinedButton(onClick = onReset) {
                Text("Reset")
            }
        }

        Text(
            text = "Small steps every day lead to big results.",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun SettingsScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = "Choose your study session duration",
            style = MaterialTheme.typography.titleMedium
        )

        Text(
            text = "Current duration: 25 minutes",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
