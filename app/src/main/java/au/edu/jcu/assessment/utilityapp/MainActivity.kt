package au.edu.jcu.assessment.utilityapp

import android.app.Activity
import android.os.Bundle
import android.os.SystemClock
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import au.edu.jcu.assessment.utilityapp.ui.theme.MobileTech_UtilityAppTheme
import au.edu.jcu.assessment.utilityapp.viewmodel.QuoteViewModel
import kotlinx.coroutines.delay

// ============================================
// SESSION MODES
// ============================================

enum class SessionMode {
    FOCUS,
    BREAK
}

// ============================================
// MAIN ACTIVITY
// ============================================

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            UtilityApp()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UtilityAppPreview() {
    UtilityApp()
}

// ============================================
// MAIN APPLICATION
// ============================================

@Composable
fun UtilityApp() {

    // Navigation
    var selectedTab by rememberSaveable {
        mutableStateOf("Timer")
    }

    // Settings
    var focusMinutes by rememberSaveable {
        mutableIntStateOf(25)
    }

    var breakMinutes by rememberSaveable {
        mutableIntStateOf(5)
    }

    var darkMode by rememberSaveable {
        mutableStateOf(false)
    }

    // Timer state
    var sessionMode by rememberSaveable {
        mutableStateOf(SessionMode.FOCUS)
    }

    var remainingMillis by rememberSaveable {
        mutableLongStateOf(25L * 60_000L)
    }

    var isRunning by rememberSaveable {
        mutableStateOf(false)
    }

    var deadlineMillis by rememberSaveable {
        mutableLongStateOf(0L)
    }

    var completedSessions by rememberSaveable {
        mutableIntStateOf(0)
    }

    // Scroll positions
    val timerScrollState = rememberScrollState()
    val settingsScrollState = rememberScrollState()

    val selectedMinutes =
        if (sessionMode == SessionMode.FOCUS) {
            focusMinutes
        } else {
            breakMinutes
        }

    val totalMillis = selectedMinutes * 60_000L

    // Countdown
    LaunchedEffect(isRunning, deadlineMillis) {
        if (isRunning) {
            while (true) {
                val timeLeft = (
                        deadlineMillis -
                                SystemClock.elapsedRealtime()
                        ).coerceAtLeast(0L)

                remainingMillis = timeLeft

                if (timeLeft == 0L) {
                    isRunning = false

                    if (sessionMode == SessionMode.FOCUS) {
                        completedSessions++
                    }
                    break
                }

                delay(100L)
            }
        }
    }

    MobileTech_UtilityAppTheme(darkTheme = darkMode) {

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = MaterialTheme.colorScheme.background,

            bottomBar = {
                NavigationBar {

                    NavigationBarItem(
                        selected = selectedTab == "Timer",
                        onClick = {
                            selectedTab = "Timer"
                        },
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
                        onClick = {
                            selectedTab = "Settings"
                        },
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
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (selectedTab) {

                    "Timer" -> UtilityScreen(
                        scrollState = timerScrollState,
                        sessionMode = sessionMode,
                        remainingMillis = remainingMillis,
                        totalMillis = totalMillis,
                        isRunning = isRunning,
                        completedSessions = completedSessions,

                        onModeChange = { newMode ->
                            if (newMode != sessionMode) {
                                isRunning = false
                                sessionMode = newMode

                                val duration =
                                    if (newMode == SessionMode.FOCUS) {
                                        focusMinutes
                                    } else {
                                        breakMinutes
                                    }

                                remainingMillis = duration * 60_000L
                            }
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

                    "Settings" -> SettingsScreen(
                        scrollState = settingsScrollState,
                        focusMinutes = focusMinutes,
                        breakMinutes = breakMinutes,
                        darkMode = darkMode,

                        onFocusChange = { newDuration ->
                            if (focusMinutes != newDuration) {
                                focusMinutes = newDuration

                                if (sessionMode == SessionMode.FOCUS) {
                                    isRunning = false
                                    remainingMillis =
                                        newDuration * 60_000L
                                }
                            }
                        },

                        onBreakChange = { newDuration ->
                            if (breakMinutes != newDuration) {
                                breakMinutes = newDuration

                                if (sessionMode == SessionMode.BREAK) {
                                    isRunning = false
                                    remainingMillis =
                                        newDuration * 60_000L
                                }
                            }
                        },

                        onDarkModeChange = { enabled ->
                            darkMode = enabled
                        }
                    )
                }
            }
        }
    }
}

// ============================================
// TIMER SCREEN
// ============================================

@Composable
fun UtilityScreen(
    scrollState: ScrollState,
    sessionMode: SessionMode,
    remainingMillis: Long,
    totalMillis: Long,
    isRunning: Boolean,
    completedSessions: Int,
    onModeChange: (SessionMode) -> Unit,
    onStartPause: () -> Unit,
    onReset: () -> Unit,
    quoteViewModel: QuoteViewModel = viewModel(factory = QuoteViewModel.Factory)
) {
    val quoteState by quoteViewModel.uiState.collectAsStateWithLifecycle()
    val activity = LocalContext.current as? Activity

    var showExitDialog by remember {
        mutableStateOf(false)
    }

    val displaySeconds =
        (remainingMillis + 999L) / 1000L

    val formattedTime = "%02d:%02d".format(
        displaySeconds / 60L,
        displaySeconds % 60L
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
            .verticalScroll(scrollState)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {

        // HEADER
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "StudyFlow",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Make every minute count.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Text(
                    text = "SF",
                    modifier = Modifier.padding(14.dp),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        // DAILY MOTIVATION (MOVED TO TOP)
        DailyMotivationCard(
            quote = quoteState.quote,
            author = quoteState.author,
            isLoading = quoteState.isLoading,
            error = quoteState.error,
            onRefresh = { quoteViewModel.fetchQuote() }
        )

        // FOCUS / BREAK
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            if (sessionMode == SessionMode.FOCUS) {
                Button(
                    onClick = {
                        onModeChange(SessionMode.FOCUS)
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Focus")
                }
            } else {
                OutlinedButton(
                    onClick = {
                        onModeChange(SessionMode.FOCUS)
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Focus")
                }
            }

            if (sessionMode == SessionMode.BREAK) {
                Button(
                    onClick = {
                        onModeChange(SessionMode.BREAK)
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Break")
                }
            } else {
                OutlinedButton(
                    onClick = {
                        onModeChange(SessionMode.BREAK)
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Break")
                }
            }
        }

        // CIRCULAR TIMER CARD
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 4.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {

                Text(
                    text = if (sessionMode == SessionMode.FOCUS) {
                        "FOCUS SESSION"
                    } else {
                        "BREAK SESSION"
                    },
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                Box(
                    modifier = Modifier.size(200.dp),
                    contentAlignment = Alignment.Center
                ) {

                    CircularProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.fillMaxSize(),
                        strokeWidth = 12.dp,
                        color = MaterialTheme.colorScheme.primary,
                        trackColor =
                            MaterialTheme.colorScheme.surfaceVariant
                    )

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = formattedTime,
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = when {
                                remainingMillis == 0L ->
                                    "Session Complete!"

                                isRunning ->
                                    "Keep Going!"

                                else ->
                                    "Ready to Begin"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Text(
                    text = if (sessionMode == SessionMode.FOCUS) {
                        "Stay focused on one task at a time."
                    } else {
                        "Take a moment to rest and recharge."
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }

        // TIMER BUTTONS
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onStartPause,
                enabled = remainingMillis > 0L,
                modifier = Modifier
                    .weight(2f)
                    .height(54.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = if (isRunning) "Pause" else "Start",
                    fontWeight = FontWeight.SemiBold
                )
            }

            OutlinedButton(
                onClick = onReset,
                modifier = Modifier
                    .weight(1f)
                    .height(54.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Reset")
            }
        }

        // SESSION PROGRESS
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Session Progress",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = "$completedSessions completed focus sessions",
                    style = MaterialTheme.typography.bodyLarge
                )

                Text(
                    text = if (completedSessions == 0) {
                        "Complete your first focus session!"
                    } else {
                        "Great work! Keep building your study habit."
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // EXIT BUTTON
        OutlinedButton(
            onClick = {
                showExitDialog = true
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.error
            )
        ) {
            Text("Exit App")
        }

        Spacer(modifier = Modifier.height(16.dp))
    }

    // EXIT CONFIRMATION
    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = {
                showExitDialog = false
            },
            title = {
                Text("Exit StudyFlow?")
            },
            text = {
                Text(
                    "Are you sure you want to exit? " +
                            "Your current study session will end."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showExitDialog = false
                        activity?.finish()
                    }
                ) {
                    Text("Exit")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showExitDialog = false
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

// ============================================
// DAILY MOTIVATION CARD
// ============================================

@Composable
fun DailyMotivationCard(
    quote: String,
    author: String,
    isLoading: Boolean,
    error: String?,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.85f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header Row: Badge + Title + Refresh Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "✨",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelMedium
                        )
                    }

                    Text(
                        text = "DAILY MOTIVATION",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 1.2.sp
                    )
                }

                IconButton(
                    onClick = onRefresh,
                    enabled = !isLoading,
                    modifier = Modifier.size(36.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Get Another Quote",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Quote Text
            Text(
                text = "“$quote”",
                style = MaterialTheme.typography.bodyLarge,
                fontStyle = FontStyle.Italic,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                lineHeight = 22.sp
            )

            // Author or Error Row
            if (error != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = error,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                    TextButton(onClick = onRefresh) {
                        Text("Retry", style = MaterialTheme.typography.labelMedium)
                    }
                }
            } else {
                Text(
                    text = "— $author",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}

// ============================================
// NEW SETTINGS SCREEN DESIGN
// ============================================

@Composable
fun SettingsScreen(
    scrollState: ScrollState,
    focusMinutes: Int,
    breakMinutes: Int,
    darkMode: Boolean,
    onFocusChange: (Int) -> Unit,
    onBreakChange: (Int) -> Unit,
    onDarkModeChange: (Boolean) -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {

        // SETTINGS HEADER
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Settings",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Personalize your focus experience",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = null,
                    modifier = Modifier.padding(14.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        // WELCOME CARD
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Your focus, your pace",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )

                Text(
                    text = "Set up a study routine that works best for you.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        // STUDY DURATION
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Study duration",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = "Choose your study session duration",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(15, 25, 30).forEach { minutes ->
                        Box(modifier = Modifier.weight(1f)) {
                            DurationOption(
                                minutes = minutes,
                                selected = focusMinutes == minutes,
                                onClick = {
                                    onFocusChange(minutes)
                                }
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(45, 60).forEach { minutes ->
                        Box(modifier = Modifier.weight(1f)) {
                            DurationOption(
                                minutes = minutes,
                                selected = focusMinutes == minutes,
                                onClick = {
                                    onFocusChange(minutes)
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))
                }

                Text(
                    text = "Selected: $focusMinutes minutes of focus time",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        // BREAK DURATION
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Break duration",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = "Take a little time to recharge",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(5, 10, 15).forEach { minutes ->
                        Box(modifier = Modifier.weight(1f)) {
                            DurationOption(
                                minutes = minutes,
                                selected = breakMinutes == minutes,
                                onClick = {
                                    onBreakChange(minutes)
                                }
                            )
                        }
                    }
                }
            }
        }

        // APPEARANCE
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Dark mode",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    Text(
                        text = "A comfortable viewing experience",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Switch(
                    checked = darkMode,
                    onCheckedChange = onDarkModeChange
                )
            }
        }

        // SESSION SUMMARY
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Your session setup",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )

                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "$focusMinutes min",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "Focus time",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "$breakMinutes min",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "Break time",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        }

        Text(
            text = "Changing the duration resets the corresponding timer.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}

// ============================================
// REUSABLE DURATION BUTTON
// ============================================

@Composable
fun DurationOption(
    minutes: Int,
    selected: Boolean,
    onClick: () -> Unit
) {

    val background = if (selected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }

    val foreground = if (selected) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = background
    ) {
        Column(
            modifier = Modifier.padding(
                horizontal = 4.dp,
                vertical = 14.dp
            ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "$minutes min",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = foreground
            )

            if (selected) {
                Text(
                    text = "✓",
                    style = MaterialTheme.typography.bodySmall,
                    color = foreground
                )
            }
        }
    }
}
