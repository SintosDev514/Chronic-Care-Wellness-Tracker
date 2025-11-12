package com.example.chronicare.homeScreens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight

// Sample data for health insights
data class HealthInsight(
    val title: String,
    val value: String,
    val description: String
)

val accentColorHealth = Color(0xFF007F7A)

private val sampleHealthInsights = listOf(
    HealthInsight(
        title = "Steps Walked",
        value = "3,240",
        description = "Your daily steps goal is 10,000. Keep going!"
    ),
    HealthInsight(
        title = "Water Intake",
        value = "1.8L",
        description = "Drink at least 2L of water daily."
    ),
    HealthInsight(
        title = "Calories Burned",
        value = "1,840",
        description = "Keep tracking your calories burned to stay fit."
    )
)



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthInsightsScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Screen Title
        Text(
            text = "Health Insights",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = 16.dp),
            color = accentColorHealth
        )

        // Displaying each health insight in a card
        sampleHealthInsights.forEach { insight ->
            HealthInsightCard(healthInsight = insight)
            Spacer(modifier = Modifier.height(12.dp)) // Space between cards
        }
    }
}

// Composable to display each health insight in a card
@Composable
fun HealthInsightCard(healthInsight: HealthInsight) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            // Insight Title and Value
            Text(
                text = healthInsight.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = healthInsight.value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF00796B) // A teal color for emphasis
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Insight Description
            Text(
                text = healthInsight.description,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HealthInsightsScreenPreview() {
    HealthInsightsScreen()
}
