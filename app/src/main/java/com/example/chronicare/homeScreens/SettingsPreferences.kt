package com.example.chronicare.homeScreens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.chronicare.screens.SharedData

// Sample data for settings preferences
data class SettingsOption(
    val title: String,
    val description: String,
    val isEnabled: Boolean
)

private val sampleSettingsOptions = listOf(
    SettingsOption(
        title = "Notifications",
        description = "Enable notifications for reminders and updates.",
        isEnabled = true
    ),
    SettingsOption(
        title = "Dark Mode",
        description = "Toggle dark theme for the app.",
        isEnabled = false
    ),
    SettingsOption(
        title = "Auto-Login",
        description = "Enable auto-login to your account.",
        isEnabled = true
    )
)

val accentColorSetting = Color(0xFF007F7A)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsPreferences(sharedData: SharedData) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = 16.dp),
            color = accentColorSetting
        )

        //  Used data from SharedData ViewModel tikang sa login
        AccountDetailsCard(
            userEmail = sharedData.email.ifBlank { "Not set" },
            userPassword = sharedData.password.ifBlank { "Not set" },
            joinedDate = "Joined: Jan 2025"
        )

        Spacer(modifier = Modifier.height(20.dp))

        // List of settings options
        sampleSettingsOptions.forEach { setting ->
            SettingsOptionCard(settingOption = setting)
            Spacer(modifier = Modifier.height(12.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Action Button
        Button(
            onClick = { /* Navigate to Account Settings */ },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = accentColorSetting)
        ) {
            Text(text = "Go to Account Settings")
        }
    }
}

//  Account Details Card
@Composable
fun AccountDetailsCard(userEmail: String, userPassword: String, joinedDate: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Account Details",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = accentColorSetting
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(text = "Email: $userEmail", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Password: $userPassword", style = MaterialTheme.typography.bodyMedium)
            Text(text = joinedDate, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }
    }
}

//  Settings Option Card
@Composable
fun SettingsOptionCard(settingOption: SettingsOption) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = settingOption.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = settingOption.description,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(12.dp))

            Switch(
                checked = settingOption.isEnabled,
                onCheckedChange = { },
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}

