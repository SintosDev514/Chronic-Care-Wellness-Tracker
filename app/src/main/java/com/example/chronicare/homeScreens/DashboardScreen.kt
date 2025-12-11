package com.example.chronicare.homeScreens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.chronicare.screens.SharedData

// Data classes for HealthSummary are still needed
data class HealthSummary(
    val stepsWalked: String = "3,240",
    val medicationStatus: String = "Taken (2/2)",
    val mood: String = "Positive",
    val waterIntake: String = "1.8L",
    val sleepHours: String = "7.5h"
)

object DashboardConstants {
    const val TAGLINE = "Your health, simplified"
    const val TODAY_SUMMARY_TITLE = "Health Overview"
    val PRIMARY_COLOR = Color(0xFF006A6A)
    val BACKGROUND_COLOR = Color(0xFFF8F9FA)
    val SURFACE_COLOR = Color(0xFFFFFFFF)
    val TEXT_PRIMARY = Color(0xFF1E293B)
    val TEXT_SECONDARY = Color(0xFF64748B)
}




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    navController: NavController,
    sharedData: SharedData,
    healthSummary: HealthSummary = HealthSummary(),
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
            WelcomeSection(sharedData)
            Spacer(modifier = Modifier.height(24.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                HealthOverviewCard(
                    healthSummary = healthSummary,
                    onViewDetailsClick = onViewDetailsClick
                )
                // The QuickActionsSection has been permanently removed.
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
            .shadow(elevation = 8.dp, shape = RoundedCornerShape(20.dp), clip = true),
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
                    text = "Welcome, ${sharedData.username.ifBlank { "User" }}!",
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
            .shadow(elevation = 6.dp, shape = RoundedCornerShape(24.dp), clip = true),
        color = DashboardConstants.SURFACE_COLOR,
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
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
            // You can add other health metrics here later if you wish
        }
    }
}
