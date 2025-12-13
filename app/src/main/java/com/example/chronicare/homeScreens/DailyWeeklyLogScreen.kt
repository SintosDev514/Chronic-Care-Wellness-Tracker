package com.example.chronicare.homeScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel // Import the viewModel composable

// NOTE: You can now delete the old `dailyLogs` sample data list.
// The `LogEntry` data class and `weeklyLogs` are kept for now.

object DashboardConstantsOne {
    val PRIMARY_COLOR = Color(0xFF006A6A)
    // ... (rest of your constants are unchanged)
    val TEXT_SECONDARY = Color(0xFF6B7280)
    val SUCCESS_COLOR = Color(0xFF10B981)
    val WARNING_COLOR = Color(0xFFF59E0B)

    val BACKGROUND_COLOR = Color(0xFFF8F9FA)
    val SECTION_SPACING = 24.dp
}

enum class TabType { Daily, Weekly }

// Sample data for weekly logs (can be replaced with Firebase data later)
val weeklyLogs = listOf(
    LogEntry("Oct 15-21", "5 out of 7 days medication taken on time. 1 missed exercise session.", "Completed"),
    LogEntry("Oct 08-14", "Consistently took medication daily. Walked 5 out of 7 days.", "Completed")
)

val accentColor = Color(0xFF007F7A)

@Composable
fun DailyWeeklyLogScreen(logViewModel: LogViewModel = viewModel()) { // 1. Inject the ViewModel
    var selectedTab by remember { mutableStateOf(TabType.Daily) }

    // 2. Observe the state from the ViewModel
    val dailyLogsFromDb = logViewModel.dailyLogs.value
    val isLoading = logViewModel.isLoading.value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(DashboardConstantsOne.BACKGROUND_COLOR)
    ) {
        Text(
            text = "Log Overview",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = 16.dp),
            color = accentColor
        )

        TabRow(
            selectedTabIndex = selectedTab.ordinal,
            modifier = Modifier.fillMaxWidth(),
            containerColor = DashboardConstantsOne.PRIMARY_COLOR.copy(alpha = 0.1f)
        ) {
            Tab(
                selected = selectedTab == TabType.Daily,
                onClick = { selectedTab = TabType.Daily },
                text = { Text("Daily Logs") }
            )
            Tab(
                selected = selectedTab == TabType.Weekly,
                onClick = { selectedTab = TabType.Weekly },
                text = { Text("Weekly Summary") }
            )
        }

        Spacer(modifier = Modifier.height(DashboardConstantsOne.SECTION_SPACING))

        // 3. Display content based on the selected tab and loading state
        if (selectedTab == TabType.Daily) {
            if (isLoading) {
                // Show a loading spinner while data is being fetched
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                // Pass the live data from the database to your list
                DailyLogList(logs = dailyLogsFromDb)
            }
        } else {
            WeeklyLogList(logs = weeklyLogs) // Weekly logs still use sample data for now
        }
    }
}

@Composable
fun DailyLogList(logs: List<LogEntry>) {
    if (logs.isEmpty()) {
        // Show a message if there are no logs to display
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No daily step logs found.", color = DashboardConstantsOne.TEXT_SECONDARY)
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(logs.size) { index ->
                LogItem(log = logs[index])
            }
        }
    }
}

// The rest of your file (WeeklyLogList, LogItem, Preview) remains exactly the same.
// ...

@Composable
fun WeeklyLogList(logs: List<LogEntry>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(logs.size) { index ->
            LogItem(log = logs[index])
        }
    }
}

@Composable
fun LogItem(log: LogEntry) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = log.date,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = DashboardConstantsOne.PRIMARY_COLOR
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = log.description,
                style = MaterialTheme.typography.bodySmall,
                color = DashboardConstantsOne.TEXT_SECONDARY
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Status: ${log.status}",
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                color = if (log.status == "Completed") DashboardConstantsOne.SUCCESS_COLOR else DashboardConstantsOne.WARNING_COLOR
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DailyWeeklyLogScreenPreview() {
    // The preview will now show the empty state or a mocked ViewModel state.
    // For a simple preview, this is fine.
    DailyWeeklyLogScreen()
}
