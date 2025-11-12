package com.example.chronicare.homeScreens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.chronicare.R
import com.example.chronicare.screens.SharedData

data class HealthSummary(
    val stepsWalked: String = "3,240",
    val medicationStatus: String = "Taken (2/2)",
    val mood: String = "Positive",
    val waterIntake: String = "1.8L",
    val sleepHours: String = "7.5h",
    val calories: String = "1,840",
    val heartRate: String = "72"
)

data class QuickAction(
    val title: String,
    val iconRes: Int,
    val color: Color,
    val route: String
)

object DashboardConstants {
    const val TAGLINE = "Your health, simplified"
    const val TODAY_SUMMARY_TITLE = "Health Overview"
    const val QUICK_ACTIONS_TITLE = "Quick Actions"


    // Color Palette
    val PRIMARY_COLOR = Color(0xFF006A6A)
    val PRIMARY_LIGHT = Color(0xFF4F9B9B)
    val SECONDARY_COLOR = Color(0xFF00C853)
    val BACKGROUND_COLOR = Color(0xFFF8F9FA)
    val SURFACE_COLOR = Color(0xFFFFFFFF)
    val TEXT_PRIMARY = Color(0xFF1E293B)
    val TEXT_SECONDARY = Color(0xFF64748B)
    val SUCCESS_COLOR = Color(0xFF10B981)
    val WARNING_COLOR = Color(0xFFF59E0B)
    val INFO_COLOR = Color(0xFF3B82F6)
    val ERROR_COLOR = Color(0xFFEF4444)

    // Gradients
    val PRIMARY_GRADIENT = Brush.linearGradient(
        colors = listOf(Color(0xFF006A6A), Color(0xFF008080))
    )

    val SECONDARY_GRADIENT = Brush.linearGradient(
        colors = listOf(Color(0xFF00C853), Color(0xFF00B14F))
    )

    val CARD_GRADIENT = Brush.linearGradient(
        colors = listOf(Color(0xFF006A6A), Color(0xFF00A699))
    )

    val QUICK_ACTIONS = listOf(
        QuickAction("Daily Log", R.drawable.calendar, INFO_COLOR, "daily_log"),
        QuickAction("Medication", R.drawable.pill, WARNING_COLOR, "medication"),
        QuickAction("Vitals", R.drawable.heart, ERROR_COLOR, "vitals"),
        QuickAction("Progress", R.drawable.chart, SUCCESS_COLOR, "progress"),
        QuickAction("Nutrition", R.drawable.food, Color(0xFF8B5CF6), "nutrition"),
        QuickAction("Reminders", R.drawable.bell, PRIMARY_COLOR, "reminders")
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(sharedData: SharedData,
    healthSummary: HealthSummary = HealthSummary(),
    onQuickActionClick: (String) -> Unit = {},
    onViewDetailsClick: () -> Unit = {}
) {
    Scaffold(
        containerColor = DashboardConstants.BACKGROUND_COLOR
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .background(DashboardConstants.BACKGROUND_COLOR)
        ) {
            // Header Section
            WelcomeSection(sharedData)
            Spacer(modifier = Modifier.height(24.dp))

            // Content Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                HealthOverviewCard(
                    healthSummary = healthSummary,
                    onViewDetailsClick = onViewDetailsClick
                )
                Spacer(modifier = Modifier.height(28.dp))
                QuickActionsSection(onQuickActionClick)
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
 fun WelcomeSection(sharedData: SharedData) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(20.dp),
                clip = true
            ),
        color = DashboardConstants.SURFACE_COLOR,
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Welcome! ${sharedData.username.ifBlank { "User" }}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DashboardConstants.TEXT_PRIMARY
                )
                Text(
                    text = DashboardConstants.TAGLINE,
                    fontSize = 14.sp,
                    color = DashboardConstants.TEXT_SECONDARY,
                    modifier = Modifier.padding(top = 4.dp)
                )

                // Health Score Chip
                Surface(
                    color = DashboardConstants.SUCCESS_COLOR.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.padding(top = 12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "🏆",
                            fontSize = 14.sp,
                            modifier = Modifier.padding(end = 4.dp)
                        )
                        Text(
                            text = "Health Score: 86",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = DashboardConstants.SUCCESS_COLOR
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Profile/Score Card
            Surface(
                color = DashboardConstants.PRIMARY_COLOR,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.size(80.dp),
                shadowElevation = 4.dp
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text(
                        text = "86",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Score",
                        fontSize = 10.sp,
                        color = Color.White.copy(alpha = 0.9f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun HealthOverviewCard(
    healthSummary: HealthSummary,
    onViewDetailsClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(24.dp),
                clip = true
            ),
        color = DashboardConstants.SURFACE_COLOR,
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = DashboardConstants.TODAY_SUMMARY_TITLE,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DashboardConstants.TEXT_PRIMARY
                )

                TextButton(
                    onClick = onViewDetailsClick,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = DashboardConstants.PRIMARY_COLOR
                    )
                ) {
                    Text(
                        text = "View Details",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Metrics Grid
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    HealthMetricItem(
                        title = "Steps",
                        value = healthSummary.stepsWalked,
                        subtitle = "of 10,000 goal",
                        icon = "👣",
                        progress = 0.32f,
                        progressColor = DashboardConstants.SUCCESS_COLOR,
                        modifier = Modifier.weight(1f)
                    )
                    HealthMetricItem(
                        title = "Medication",
                        value = healthSummary.medicationStatus,
                        subtitle = "On track",
                        icon = "💊",
                        progress = 1.0f,
                        progressColor = DashboardConstants.SUCCESS_COLOR,
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    HealthMetricItem(
                        title = "Water",
                        value = healthSummary.waterIntake,
                        subtitle = "of 2.0L goal",
                        icon = "💧",
                        progress = 0.9f,
                        progressColor = DashboardConstants.INFO_COLOR,
                        modifier = Modifier.weight(1f)
                    )
                    HealthMetricItem(
                        title = "Sleep",
                        value = healthSummary.sleepHours,
                        subtitle = "Quality: Good",
                        icon = "😴",
                        progress = 0.75f,
                        progressColor = DashboardConstants.PRIMARY_COLOR,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun HealthMetricItem(
    title: String,
    value: String,
    subtitle: String,
    icon: String,
    progress: Float,
    progressColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = DashboardConstants.BACKGROUND_COLOR,
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header Row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = icon,
                    fontSize = 18.sp
                )
                Text(
                    text = value,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = DashboardConstants.TEXT_PRIMARY
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Content
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = DashboardConstants.TEXT_PRIMARY
            )
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = DashboardConstants.TEXT_SECONDARY,
                modifier = Modifier.padding(top = 2.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Progress Bar
            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = progressColor,
                trackColor = progressColor.copy(alpha = 0.2f)
            )

            // Progress Percentage
            Text(
                text = "${(progress * 100).toInt()}%",
                fontSize = 10.sp,
                color = DashboardConstants.TEXT_SECONDARY,
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(top = 4.dp)
            )
        }
    }
}

@Composable
private fun QuickActionsSection(onQuickActionClick: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = DashboardConstants.QUICK_ACTIONS_TITLE,
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = DashboardConstants.TEXT_PRIMARY,
            modifier = Modifier.padding(bottom = 20.dp)
        )

        // 2x3 Grid
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            DashboardConstants.QUICK_ACTIONS.chunked(3).forEach { rowActions ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    rowActions.forEach { action ->
                        QuickActionButton(
                            action = action,
                            onClick = { onQuickActionClick(action.route) },
                            modifier = Modifier.weight(1f)
                        )
                    }


                    repeat(3 - rowActions.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun QuickActionButton(
    action: QuickAction,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .aspectRatio(1f)
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(16.dp),
                clip = true
            ),
        color = DashboardConstants.SURFACE_COLOR,
        shape = RoundedCornerShape(16.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Icon Container
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = action.color.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(action.iconRes),
                    contentDescription = action.title,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = action.title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = DashboardConstants.TEXT_PRIMARY,
                textAlign = TextAlign.Center,
                maxLines = 2
            )
        }
    }
}

