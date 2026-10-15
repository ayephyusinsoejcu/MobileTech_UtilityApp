package au.edu.jcu.assessment.utilityapp

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
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
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import au.edu.jcu.assessment.utilityapp.viewmodel.TimerViewModel

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

// ============================================
// APP PREVIEW
// ============================================

@Preview(showBackground = true)
@Composable
fun UtilityAppPreview() {
    UtilityApp()
}

// ============================================
// MAIN APPLICATION
// ============================================

@Composable
fun UtilityApp(
    timerViewModel: TimerViewModel = viewModel()
) {

    var selectedTab by rememberSaveable {
        mutableStateOf("Timer")
    }

    val uiState by timerViewModel.uiState
        .collectAsStateWithLifecycle()

    val timerScrollState = rememberScrollState()
    val calendarScrollState = rememberScrollState()
    val settingsScrollState = rememberScrollState()

    MobileTech_UtilityAppTheme(
        darkTheme = uiState.darkMode
    ) {

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
                                imageVector = Icons.Default.Home,
                                contentDescription = "Timer"
                            )
                        },
                        label = {
                            Text("Timer")
                        }
                    )

                    NavigationBarItem(
                        selected = selectedTab == "Calendar",
                        onClick = {
                            selectedTab = "Calendar"
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = "Calendar"
                            )
                        },
                        label = {
                            Text("Calendar")
                        }
                    )

                    NavigationBarItem(
                        selected = selectedTab == "Settings",
                        onClick = {
                            selectedTab = "Settings"
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Settings"
                            )
                        },
                        label = {
                            Text("Settings")
                        }
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

                    "Timer" -> {

                        UtilityScreen(
                            scrollState = timerScrollState,
                            sessionMode = uiState.sessionMode,
                            remainingMillis = uiState.remainingMillis,
                            totalMillis = uiState.totalMillis,
                            isRunning = uiState.isRunning,
                            completedSessions = uiState.completedSessions,

                            onModeChange = { newMode ->
                                timerViewModel.changeSessionMode(newMode)
                            },

                            onStartPause = {
                                timerViewModel.startPauseTimer()
                            },

                            onReset = {
                                timerViewModel.resetTimer()
                            }
                        )
                    }

                    "Calendar" -> {

                        CalendarScreen(
                            scrollState = calendarScrollState
                        )
                    }

                    "Settings" -> {

                        SettingsScreen(
                            scrollState = settingsScrollState,
                            focusMinutes = uiState.focusMinutes,
                            breakMinutes = uiState.breakMinutes,
                            darkMode = uiState.darkMode,

                            onFocusChange = { newDuration ->
                                timerViewModel.updateFocusDuration(newDuration)
                            },

                            onBreakChange = { newDuration ->
                                timerViewModel.updateBreakDuration(newDuration)
                            },

                            onDarkModeChange = { enabled ->
                                timerViewModel.updateDarkMode(enabled)
                            }
                        )
                    }
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

    val activity = LocalContext.current as? Activity

    var showExitDialog by remember {
        mutableStateOf(false)
    }

    val quoteState by quoteViewModel.uiState
        .collectAsStateWithLifecycle()

    val displaySeconds = (remainingMillis + 999L) / 1000L

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

        // DAILY MOTIVATION
        DailyMotivationCard(
            quote = quoteState.quote,
            author = quoteState.author,
            isLoading = quoteState.isLoading,
            error = quoteState.error,
            onRefresh = { quoteViewModel.fetchQuote() }
        )

        // FOCUS / BREAK SELECTION
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
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
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
                                remainingMillis == 0L -> "Session Complete!"
                                isRunning -> "Keep Going!"
                                else -> "Ready to Begin"
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

        // TIMER CONTROL BUTTONS
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

        Spacer(
            modifier = Modifier.height(16.dp)
        )
    }

    // EXIT CONFIRMATION DIALOG
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
// JCU ACADEMIC CALENDAR SCREEN
// ============================================

enum class EventType(
    val label: String,
    val containerColor: Color,
    val contentColor: Color
) {
    PUBLIC_HOLIDAY("Public Holiday", Color(0xFFFFF176), Color(0xFF574600)),
    ORIENTATION("Orientation", Color(0xFFFFF59D), Color(0xFF524800)),
    TEACHING("Teaching", Color(0xFF80DEEA), Color(0xFF004D40)),
    DEADLINE("Deadline", Color(0xFFA5D6A7), Color(0xFF1B5E20)),
    CENSUS("Census Date", Color(0xFFFFCC80), Color(0xFFE65100)),
    STUDY_WEEK("Study Week", Color(0xFFCFD8DC), Color(0xFF263238)),
    EXAM("Exam Period", Color(0xFFFF8A80), Color(0xFFB71C1C)),
    RESULTS("Results", Color(0xFFFFAB91), Color(0xFFBF360C)),
    SUPP_EXAM("Supp Exams", Color(0xFF9FA8DA), Color(0xFF1A237E))
}

data class CalendarEvent(
    val dateText: String,
    val description: String,
    val type: EventType
)

data class MonthEvents(
    val monthYear: String,
    val events: List<CalendarEvent>
)

data class DetailedTrimesterInfo(
    val code: String,
    val studyPeriod: String,
    val title: String,
    val durationText: String,
    val orientationSummary: String,
    val teachingSummary: String,
    val censusSummary: String,
    val examSummary: String,
    val resultsSummary: String,
    val months: List<MonthEvents>
)

val detailedTrimesters = listOf(
    DetailedTrimesterInfo(
        code = "TR1S",
        studyPeriod = "SP51",
        title = "Trimester 1 2026 (TR1S)",
        durationText = "March 2026 – July 2026",
        orientationSummary = "Mon 9 Mar – Fri 20 Mar 2026",
        teachingSummary = "Mon 23 Mar – Sat 13 Jun 2026",
        censusSummary = "Thu 16 Apr 2026",
        examSummary = "Thu 11 Jun – Sat 13 Jun 2026",
        resultsSummary = "Wed 24 Jun 2026",
        months = listOf(
            MonthEvents(
                monthYear = "March 2026",
                events = listOf(
                    CalendarEvent("MON 9 - FRI 20", "Orientation", EventType.ORIENTATION),
                    CalendarEvent("MON 23", "Trimester 1 Begins", EventType.TEACHING),
                    CalendarEvent("SUN 29", "Enrolments Close", EventType.DEADLINE)
                )
            ),
            MonthEvents(
                monthYear = "April 2026",
                events = listOf(
                    CalendarEvent("FRI 3", "Public Holiday: Good Friday", EventType.PUBLIC_HOLIDAY),
                    CalendarEvent("THU 16", "Census Date", EventType.CENSUS),
                    CalendarEvent("MON 27 - THU 30", "Mid-Trimester Recess / Study Week", EventType.STUDY_WEEK)
                )
            ),
            MonthEvents(
                monthYear = "May 2026",
                events = listOf(
                    CalendarEvent("FRI 1", "Public Holiday: Labour Day", EventType.PUBLIC_HOLIDAY),
                    CalendarEvent("FRI 1", "Last Withdrawal without Academic Penalty", EventType.DEADLINE),
                    CalendarEvent("SAT 23", "Last Date to Withdraw in MyStudent Portal", EventType.DEADLINE)
                )
            ),
            MonthEvents(
                monthYear = "June 2026",
                events = listOf(
                    CalendarEvent("SAT 6 - WED 10", "Study Vacation", EventType.STUDY_WEEK),
                    CalendarEvent("THU 11 - SAT 13", "Exam Period", EventType.EXAM),
                    CalendarEvent("SAT 13", "Trimester 1 Ends", EventType.TEACHING),
                    CalendarEvent("WED 24", "Results Published", EventType.RESULTS)
                )
            ),
            MonthEvents(
                monthYear = "July 2026",
                events = listOf(
                    CalendarEvent("MON 13 - SAT 18", "Deferred & Supplementary Exams", EventType.SUPP_EXAM)
                )
            )
        )
    ),
    DetailedTrimesterInfo(
        code = "TR2",
        studyPeriod = "SP52",
        title = "Trimester 2 2026 (TR2)",
        durationText = "July 2026 – November 2026",
        orientationSummary = "Mon 13 Jul – Fri 24 Jul 2026",
        teachingSummary = "Mon 27 Jul – Sat 17 Oct 2026",
        censusSummary = "Thu 20 Aug 2026",
        examSummary = "Thu 15 Oct – Sat 17 Oct 2026",
        resultsSummary = "Wed 28 Oct 2026",
        months = listOf(
            MonthEvents(
                monthYear = "July 2026",
                events = listOf(
                    CalendarEvent("MON 13 - FRI 24", "Orientation", EventType.ORIENTATION),
                    CalendarEvent("MON 27", "Trimester 2 Begins", EventType.TEACHING)
                )
            ),
            MonthEvents(
                monthYear = "August 2026",
                events = listOf(
                    CalendarEvent("SUN 2", "Enrolments Close", EventType.DEADLINE),
                    CalendarEvent("MON 10", "Public Holiday: National Day (Observed)", EventType.PUBLIC_HOLIDAY),
                    CalendarEvent("THU 20", "Census Date", EventType.CENSUS),
                    CalendarEvent("MON 31", "Mid-Trimester Recess / Study Week", EventType.STUDY_WEEK)
                )
            ),
            MonthEvents(
                monthYear = "September 2026",
                events = listOf(
                    CalendarEvent("TUE 1 - FRI 4", "Mid-Trimester Recess / Study Week", EventType.STUDY_WEEK),
                    CalendarEvent("FRI 4", "Last Withdrawal without Academic Penalty", EventType.DEADLINE),
                    CalendarEvent("SAT 26", "Last Date to Withdraw in MyStudent Portal", EventType.DEADLINE)
                )
            ),
            MonthEvents(
                monthYear = "October 2026",
                events = listOf(
                    CalendarEvent("SAT 10 - WED 14", "Study Vacation", EventType.STUDY_WEEK),
                    CalendarEvent("THU 15 - SAT 17", "Exam Period", EventType.EXAM),
                    CalendarEvent("SAT 17", "Trimester 2 Ends", EventType.TEACHING),
                    CalendarEvent("WED 28", "Results Published", EventType.RESULTS)
                )
            ),
            MonthEvents(
                monthYear = "November 2026",
                events = listOf(
                    CalendarEvent("MON 9 - SAT 14", "Deferred & Supplementary Exams", EventType.SUPP_EXAM)
                )
            )
        )
    ),
    DetailedTrimesterInfo(
        code = "TR3",
        studyPeriod = "SP53",
        title = "Trimester 3 2026 (TR3)",
        durationText = "August 2026 – January 2027",
        orientationSummary = "Mon 31 Aug – Fri 11 Sep 2026",
        teachingSummary = "Mon 14 Sep – Sat 5 Dec 2026",
        censusSummary = "Thu 8 Oct 2026",
        examSummary = "Thu 3 Dec – Sat 5 Dec 2026",
        resultsSummary = "Wed 18 Dec 2026",
        months = listOf(
            MonthEvents(
                monthYear = "August 2026",
                events = listOf(
                    CalendarEvent("MON 10", "Public Holiday: National Day (Observed)", EventType.PUBLIC_HOLIDAY),
                    CalendarEvent("MON 31", "Orientation", EventType.ORIENTATION)
                )
            ),
            MonthEvents(
                monthYear = "September 2026",
                events = listOf(
                    CalendarEvent("TUE 1 - FRI 11", "Orientation", EventType.ORIENTATION),
                    CalendarEvent("MON 14", "Trimester 3 Begins", EventType.TEACHING),
                    CalendarEvent("SUN 20", "Enrolments Close", EventType.DEADLINE)
                )
            ),
            MonthEvents(
                monthYear = "October 2026",
                events = listOf(
                    CalendarEvent("THU 8", "Census Date", EventType.CENSUS),
                    CalendarEvent("MON 19 - FRI 23", "Study Week", EventType.STUDY_WEEK),
                    CalendarEvent("FRI 23", "Last Withdrawal without Academic Penalty", EventType.DEADLINE)
                )
            ),
            MonthEvents(
                monthYear = "November 2026",
                events = listOf(
                    CalendarEvent("MON 9", "Public Holiday: Deepavali (Observed)", EventType.PUBLIC_HOLIDAY),
                    CalendarEvent("SAT 21", "Last Date to Withdraw in MyStudent Portal", EventType.DEADLINE),
                    CalendarEvent("SAT 28 - MON 30", "Study Vacation", EventType.STUDY_WEEK)
                )
            ),
            MonthEvents(
                monthYear = "December 2026",
                events = listOf(
                    CalendarEvent("TUE 1 - WED 2", "Study Vacation", EventType.STUDY_WEEK),
                    CalendarEvent("THU 3 - SAT 5", "Exam Period", EventType.EXAM),
                    CalendarEvent("SAT 5", "Trimester 3 Ends", EventType.TEACHING),
                    CalendarEvent("WED 18", "Results Published", EventType.RESULTS),
                    CalendarEvent("FRI 25", "Public Holiday: Christmas Day", EventType.PUBLIC_HOLIDAY)
                )
            ),
            MonthEvents(
                monthYear = "January 2027",
                events = listOf(
                    CalendarEvent("FRI 1", "Public Holiday: New Year's Day", EventType.PUBLIC_HOLIDAY),
                    CalendarEvent("MON 11 - SAT 16", "Deferred & Supplementary Exams", EventType.SUPP_EXAM)
                )
            )
        )
    )
)

@Composable
fun CalendarScreen(
    scrollState: ScrollState
) {
    val context = LocalContext.current
    var selectedTrimesterCode by rememberSaveable { mutableStateOf("TR3") }

    val selectedTrimester = detailedTrimesters.find { it.code == selectedTrimesterCode }
        ?: detailedTrimesters.last()

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
                    text = "JCU Calendar",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "James Cook University Singapore",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = null,
                    modifier = Modifier.padding(14.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        // TRIMESTER SELECTOR
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Select Trimester (TRI)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    detailedTrimesters.forEach { trimester ->
                        val isSelected = trimester.code == selectedTrimesterCode
                        if (isSelected) {
                            Button(
                                onClick = { selectedTrimesterCode = trimester.code },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(trimester.code, fontWeight = FontWeight.Bold)
                            }
                        } else {
                            OutlinedButton(
                                onClick = { selectedTrimesterCode = trimester.code },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(trimester.code)
                            }
                        }
                    }
                }
            }
        }

        // TRIMESTER SUMMARY OVERVIEW CARD
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = selectedTrimester.title,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )

                        Text(
                            text = "Study Period: ${selectedTrimester.studyPeriod}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.primary
                    ) {
                        Text(
                            text = selectedTrimester.durationText,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f))

                // Schedule items
                CalendarItem(icon = "🎓", title = "Orientation", dateText = selectedTrimester.orientationSummary)
                CalendarItem(icon = "📚", title = "Teaching Period", dateText = selectedTrimester.teachingSummary)
                CalendarItem(icon = "📌", title = "Census Date", dateText = selectedTrimester.censusSummary)
                CalendarItem(icon = "📝", title = "Exam Period", dateText = selectedTrimester.examSummary)
                CalendarItem(icon = "📊", title = "Results Published", dateText = selectedTrimester.resultsSummary)
            }
        }

        // EVENT LEGEND
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Calendar Legend",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    EventTypeBadge(EventType.PUBLIC_HOLIDAY)
                    EventTypeBadge(EventType.ORIENTATION)
                    EventTypeBadge(EventType.TEACHING)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    EventTypeBadge(EventType.CENSUS)
                    EventTypeBadge(EventType.STUDY_WEEK)
                    EventTypeBadge(EventType.EXAM)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    EventTypeBadge(EventType.RESULTS)
                    EventTypeBadge(EventType.SUPP_EXAM)
                    EventTypeBadge(EventType.DEADLINE)
                }
            }
        }

        // IMPORTANT DATES LIST MONTH BY MONTH
        Text(
            text = "Important Dates (${selectedTrimester.code})",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        selectedTrimester.months.forEach { month ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = month.monthYear,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    month.events.forEach { event ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.width(110.dp)
                            ) {
                                Text(
                                    text = event.dateText,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                )
                            }

                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                EventTypeBadge(event.type)

                                Text(
                                    text = event.description,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        if (event != month.events.last()) {
                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
                            )
                        }
                    }
                }
            }
        }

        // OFFICIAL LINK CARD
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Official Academic Calendar",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = "For official PDF downloads and full year-by-year details, visit the JCU Singapore official portal.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Button(
                    onClick = {
                        val urlIntent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://www.jcu.edu.sg/courses-and-study/admissions/academic-calendar")
                        )
                        context.startActivity(urlIntent)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Open JCU Academic Calendar Portal")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun EventTypeBadge(type: EventType) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = type.containerColor
    ) {
        Text(
            text = type.label,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            style = MaterialTheme.typography.labelSmall,
            color = type.contentColor,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun CalendarItem(
    icon: String,
    title: String,
    dateText: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = icon, fontSize = 20.sp)

        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Text(
                text = dateText,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
            )
        }
    }
}

// ============================================
// SETTINGS SCREEN
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

                        Box(
                            modifier = Modifier.weight(1f)
                        ) {

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

                        Box(
                            modifier = Modifier.weight(1f)
                        ) {

                            DurationOption(
                                minutes = minutes,
                                selected = focusMinutes == minutes,
                                onClick = {
                                    onFocusChange(minutes)
                                }
                            )
                        }
                    }

                    Spacer(
                        modifier = Modifier.weight(1f)
                    )
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

                        Box(
                            modifier = Modifier.weight(1f)
                        ) {

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

        Spacer(
            modifier = Modifier.height(16.dp)
        )
    }
}

// ============================================
// REUSABLE DURATION OPTION
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
