
package au.edu.jcu.assessment.utilityapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import au.edu.jcu.assessment.utilityapp.ui.theme.MobileTech_UtilityAppTheme

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
                Text("FOCUS SESSION")
                Text(
                    text = "25:00",
                    style = MaterialTheme.typography.displayLarge
                )
                Text("Ready to study?")
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
