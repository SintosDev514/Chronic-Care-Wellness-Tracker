package com.example.chronicare.homeScreens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight

// Sample data for progress tracking
data class ProgressMetric(
    val title: String,
    val goal: Int,
    val current: Int
)

private val sampleProgressMetrics = listOf(
    ProgressMetric(
        title = "Steps Walked",
        goal = 10000,
        current = 3240
    ),
    ProgressMetric(
        title = "Calories Burned",
        goal = 2500,
        current = 1840
    ),
    ProgressMetric(
        title = "Water Intake (in Liters)",
        goal = 2,
        current = 1
    )
)

val accentColorProgress = Color(0xFF007F7A)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressTrackingScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Screen Title
        Text(
            text = "Progress Tracking",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = 16.dp),
            color = accentColorProgress

        )

        // List of progress metrics (e.g., Steps, Calories, Water Intake)
        sampleProgressMetrics.forEach { metric ->
            ProgressMetricCard(progressMetric = metric)
            Spacer(modifier = Modifier.height(12.dp)) // Space between cards
        }
    }
}

// Composable to display each progress metric in a card
@Composable
fun ProgressMetricCard(progressMetric: ProgressMetric) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            // Metric Title and Current Progress
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = progressMetric.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${progressMetric.current} / ${progressMetric.goal}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFF00796B) // A teal color for emphasis
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Progress Bar
            val progress = progressMetric.current.toFloat() / progressMetric.goal.toFloat()

            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    ,
                color = Color(0xFF10B981), // Green for progress
                trackColor = Color.Gray.copy(alpha = 0.2f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Display current status
            Text(
                text = "Goal Progress: ${(progress * 100).toInt()}%",
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                color = if (progress == 1f) Color(0xFF10B981) else Color.Gray
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProgressTrackingScreenPreview() {
    ProgressTrackingScreen()
}
