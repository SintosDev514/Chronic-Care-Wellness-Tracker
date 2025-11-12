package com.example.chronicare.homeScreens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chronicare.ui.theme.ChroniCareTheme

@Composable
fun HealthTrackingApp() {
    var sleepInput by remember { mutableStateOf("") }
    var waterInput by remember { mutableStateOf("") }
    var currentSleep by remember { mutableStateOf(0.0f) }
    var currentWater by remember { mutableStateOf(0f) }

    val sleepGoal = 8f
    val waterGoal = 2000f
    val context = LocalContext.current
    val accentColor = Color(0xFF007F7A)

    ChroniCareTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // App Title
                Text(
                    text = "Daily Health Tracker",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = accentColor,
                    modifier = Modifier

                        .padding(vertical = 8.dp)
                )

                // Sleep Tracker Card
                TrackerCard(
                    title = "Sleep Tracker",
                    goalText = "Goal: 8 hours",
                    currentValueText = "Current: ${currentSleep} hrs",
                    progress = currentSleep / sleepGoal,
                    progressColor = accentColor,
                    inputValue = sleepInput,
                    onInputChange = { sleepInput = it },
                    buttonLabel = "Log Sleep",
                    onButtonClick = {
                        val sleepHours = sleepInput.toFloatOrNull()
                        if (sleepHours != null && sleepHours in 0f..sleepGoal) {
                            currentSleep = sleepHours
                            Toast.makeText(
                                context,
                                "Sleep logged: $sleepHours hours",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                context,
                                "Enter valid sleep hours (0–8)",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    inputLabel = "Enter sleep hours (0–8)"
                )

                // Water Tracker Card
                TrackerCard(
                    title = "Water Intake Tracker",
                    goalText = "Goal: 2000 ml (2 liters)",
                    currentValueText = "Current: ${currentWater} ml",
                    progress = currentWater / waterGoal,
                    progressColor = accentColor,
                    inputValue = waterInput,
                    onInputChange = { waterInput = it },
                    buttonLabel = "Log Water",
                    onButtonClick = {
                        val waterAmount = waterInput.toFloatOrNull()
                        if (waterAmount != null && waterAmount > 0) {
                            currentWater = (currentWater + waterAmount).coerceAtMost(waterGoal)
                            Toast.makeText(
                                context,
                                "Water logged: $waterAmount ml",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                context,
                                "Please enter a valid water amount",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    inputLabel = "Enter water (ml)"
                )
            }
        }
    }
}

@Composable
fun TrackerCard(
    title: String,
    goalText: String,
    currentValueText: String,
    progress: Float,
    progressColor: Color,
    inputValue: String,
    onInputChange: (String) -> Unit,
    buttonLabel: String,
    onButtonClick: () -> Unit,
    inputLabel: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = goalText,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            LinearProgressIndicator(
                progress = { progress.coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = progressColor
            )

            Text(
                text = currentValueText,
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = progressColor,
                    fontWeight = FontWeight.Medium
                )
            )

            OutlinedTextField(
                value = inputValue,
                onValueChange = onInputChange,
                label = { Text(inputLabel) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            )

            Button(
                onClick = onButtonClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = progressColor)
            ) {
                Text(buttonLabel, color = Color.White)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ChroniCareTheme {
        HealthTrackingApp()
    }
}
