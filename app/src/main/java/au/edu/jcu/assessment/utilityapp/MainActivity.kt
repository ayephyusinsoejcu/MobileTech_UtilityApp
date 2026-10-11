
package au.edu.jcu.assessment.utilityapp

import android.os.Bundle
import android.os.SystemClock
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import au.edu.jcu.assessment.utilityapp.ui.theme.MobileTech_UtilityAppTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { UtilityApp() }
    }
}

@Composable
fun UtilityApp() {
    var selectedTab by remember { mutableStateOf("Timer") }
    var sessionMode by remember { mutableStateOf("Focus") }
    var focusMinutes by remember { mutableIntStateOf(25) }
    var breakMinutes by remember { mutableIntStateOf(5) }
    var remainingMillis by remember { mutableLongStateOf(25L * 60_000L) }
    var isRunning by remember { mutableStateOf(false) }
    var deadlineMillis by remember { mutableLongStateOf(0L) }
    var completedSessions by remember { mutableIntStateOf(0) }

    LaunchedEffect(isRunning, deadlineMillis) {
        if (isRunning) {
            while (true) {
                val left = (
                        deadlineMillis - SystemClock.elapsedRealtime()
                        ).coerceAtLeast(0L)

                remainingMillis = left

                if (left == 0L) {
                    isRunning = false

                    if (sessionMode == "Focus") {
                        completedSessions++
                    }
                    break
                }

                delay(100L)
            }
        }
    }

    val totalMillis = (
            if (sessionMode == "Focus") focusMinutes
            else breakMinutes
            ) * 60_000L

    MobileTech_UtilityAppTheme {
        Scaffold(
            bottomBar = {
                NavigationBar {
                    NavigationBarItem(
                        selected = selectedTab == "Timer",
                        onClick = { selectedTab = "Timer" },
                        icon = {
                            Icon(
                                Icons.Default.Home,
                                contentDescription = "Timer"
                            )
                        },
                        label = { Text("Timer") }
                    )

                    NavigationBarItem(
                        selected = selectedTab == "Settings",
                        onClick = { selectedTab = "Settings" },
                        icon = {
                            Icon(
                                Icons.Default.Settings,
                                contentDescription = "Settings"
                            )
                        },
                        label = { Text("Settings") }
                    )
                }
            }
        ) { innerPadding ->
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (selectedTab == "Timer") {
                    TimerScreen(
                        remainingMillis = remainingMillis,
                        isRunning = isRunning,
                        sessionMode = sessionMode,
                        totalMillis = totalMillis,
                        completedSessions = completedSessions,

                        onModeChange = { mode ->
                            sessionMode = mode
                            isRunning = false

                            remainingMillis = (
                                    if (mode == "Focus") focusMinutes
                                    else breakMinutes
                                    ) * 60_000L
                        },

                        onStartPause = {
                            if (isRunning) {
                                remainingMillis = (
                                        deadlineMillis -
                                                SystemClock.elapsedRealtime()
                                        ).coerceAtLeast(0L)

                                isRunning = false
                            } else if (remainingMillis > 0L) {
                                deadlineMillis =
                                    SystemClock.elapsedRealtime() +
                                            remainingMillis

                                isRunning = true
                            }
                        },

                        onReset = {
                            isRunning = false
                            remainingMillis = totalMillis
                        }
                    )
                } else {
                    SettingsScreen(
                        focusMinutes = focusMinutes,
                        breakMinutes = breakMinutes,

                        onFocusChange = { value ->
                            focusMinutes = value

                            if (sessionMode == "Focus") {
                                isRunning = false
                                remainingMillis = value * 60_000L
                            }
                        },

                        onBreakChange = { value ->
                            breakMinutes = value

                            if (sessionMode == "Break") {
                                isRunning = false
                                remainingMillis = value * 60_000L
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun TimerScreen(
    remainingMillis: Long,
    isRunning: Boolean,
    sessionMode: String,
    onModeChange: (String) -> Unit,
    totalMillis: Long,
    completedSessions: Int,
    onStartPause: () -> Unit,
    onReset: () -> Unit
) {
    val displaySeconds = (remainingMillis + 999L) / 1000L

    val formattedTime = "%02d:%02d".format(
        displaySeconds / 60,
        displaySeconds % 60
    )

    val progress = if (totalMillis > 0L) {
        (remainingMillis.toFloat() / totalMillis.toFloat())
            .coerceIn(0f, 1f)
    } else {
        0f
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "StudyFlow",
            style = MaterialTheme.typography.headlineMedium
        )

        Text("Make every minute count.")

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(onClick = { onModeChange("Focus") }) {
                Text(
                    if (sessionMode == "Focus") "✓ Focus"
                    else "Focus"
                )
            }

            Button(onClick = { onModeChange("Break") }) {
                Text(
                    if (sessionMode == "Break") "✓ Break"
                    else "Break"
                )
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    if (sessionMode == "Focus")
                        "FOCUS SESSION"
                    else
                        "BREAK SESSION"
                )

                // Circular progress timer
                Box(
                    modifier = Modifier.size(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.fillMaxSize(),
                        strokeWidth = 12.dp
                    )

                    Text(
                        text = formattedTime,
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    when {
                        remainingMillis == 0L -> "Session Complete!"
                        isRunning -> "Keep Going!"
                        else -> "Ready to Begin"
                    }
                )
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onStartPause,
                enabled = remainingMillis > 0L
            ) {
                Text(if (isRunning) "Pause" else "Start")
            }

            OutlinedButton(onClick = onReset) {
                Text("Reset")
            }
        }

        // Completed focus sessions
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    "Session Progress",
                    style = MaterialTheme.typography.titleMedium
                )

                Text("$completedSessions completed focus sessions")
            }
        }

        Text("Small steps every day lead to big results.")
    }
}

@Composable
fun SettingsScreen(
    focusMinutes: Int,
    breakMinutes: Int,
    onFocusChange: (Int) -> Unit,
    onBreakChange: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Text(
            "Settings",
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            "Study duration",
            style = MaterialTheme.typography.titleMedium
        )

        listOf(15, 25, 30, 45, 60).forEach { duration ->
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = { onFocusChange(duration) }
            ) {
                Text(
                    "$duration minutes" +
                            if (focusMinutes == duration) " ✓" else ""
                )
            }
        }

        Text(
            "Break duration",
            style = MaterialTheme.typography.titleMedium
        )

        listOf(5, 10, 15).forEach { duration ->
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = { onBreakChange(duration) }
            ) {
                Text(
                    "$duration minutes" +
                            if (breakMinutes == duration) " ✓" else ""
                )
            }
        }
    }
}
