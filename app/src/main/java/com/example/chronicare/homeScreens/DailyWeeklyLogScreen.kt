package com.example.chronicare.homeScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.example.chronicare.R

// Data class for log entries (Daily or Weekly log)
data class LogEntry(val date: String, val description: String, val status: String)

object DashboardConstantsOne {
    val PRIMARY_COLOR = Color(0xFF006A6A)
    val SECONDARY_COLOR = Color(0xFF00897B)
    val ACCENT_COLOR = Color(0xFF4DB6AC)
    val SURFACE_COLOR = Color(0xFFFFFFFF)
    val BACKGROUND_COLOR = Color(0xFFF8F9FA)
    val TEXT_PRIMARY = Color(0xFF1A1D21)
    val TEXT_SECONDARY = Color(0xFF6B7280)
    val SUCCESS_COLOR = Color(0xFF10B981)
    val WARNING_COLOR = Color(0xFFF59E0B)
    val INFO_COLOR = Color(0xFF3B82F6)
    val SECTION_SPACING = 24.dp
    val CARD_SPACING = 16.dp
    val ELEMENT_SPACING = 12.dp
}

// Enum to represent the selected tab (Daily or Weekly)
enum class TabType { Daily, Weekly }

// Sample data for daily and weekly logs
val dailyLogs = listOf(
    LogEntry("2023-10-21", "Took medication in the morning. Went for a 30-minute walk.", "Completed"),
    LogEntry("2023-10-22", "Did stretching exercises. Evening walk canceled due to rain.", "Pending"),
    LogEntry("2023-10-23", "Had a healthy breakfast. Took prescribed pills after breakfast.", "Completed")
)

val weeklyLogs = listOf(
    LogEntry("2023-10-15 - 2023-10-21", "5 out of 7 days medication taken on time. 1 missed exercise session.", "Completed"),
    LogEntry("2023-10-08 - 2023-10-14", "Consistently took medication daily. Walked 5 out of 7 days.", "Completed"),
    LogEntry("2023-10-01 - 2023-10-07", "Completed daily morning routine. Missing one exercise day.", "Pending")
)

val accentColor = Color(0xFF007F7A)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyWeeklyLogScreen() {
    var selectedTab by remember { mutableStateOf(TabType.Daily) }

    // Column container for overall layout
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(DashboardConstantsOne.BACKGROUND_COLOR)
    ) {
        Text(
            text = "Log Overview",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold),
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

        // Display content based on selected tab
        if (selectedTab == TabType.Daily) {
            DailyLogList(dailyLogs)
        } else {
            WeeklyLogList(weeklyLogs)
        }
    }
}

@Composable
fun DailyLogList(logs: List<LogEntry>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(DashboardConstantsOne.ELEMENT_SPACING)
    ) {
        items(logs.size) { index ->
            LogItem(log = logs[index])
        }
    }
}

@Composable
fun WeeklyLogList(logs: List<LogEntry>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(DashboardConstantsOne.ELEMENT_SPACING)
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
        colors = CardDefaults.cardColors(containerColor = DashboardConstantsOne.SURFACE_COLOR)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(
                text = log.date,
                style = MaterialTheme.typography.bodyMedium,
                color = DashboardConstantsOne.TEXT_PRIMARY
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
    DailyWeeklyLogScreen()
}
