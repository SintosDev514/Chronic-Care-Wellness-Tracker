package com.example.chronicare.screens

import androidx.compose.foundation.clickable
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.chronicare.R
import kotlinx.coroutines.launch
import com.example.chronicare.NavRoutes
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.chronicare.AppNavGraph
import com.example.chronicare.homeScreens.*
import com.example.chronicare.screens.SharedData

//  Accept sharedData in HomeScreenMain
@Composable
fun HomeScreenMain(sharedData: SharedData) {
    DrawerApp(sharedData)
}

data class DrawerItem(
    val title: String,
    val route: String,
    val icon: ImageVector
)

val drawerItems = listOf(
    DrawerItem("Dashboard", NavRoutes.Dashboard.route, Icons.Filled.Dashboard),
    DrawerItem("Daily & Weekly Log", NavRoutes.DailyLog.route, Icons.Filled.CalendarToday),
    DrawerItem("Health Insights", NavRoutes.HealthInsights.route, Icons.Filled.FitnessCenter),
    DrawerItem("Medication & Treatment Reminder", NavRoutes.MedicationReminder.route, Icons.Filled.LocalPharmacy),
    DrawerItem("Daily Health Tracker ", NavRoutes.HydrationAndSleepTrackerScreen.route, Icons.Filled.AccessTime),
    DrawerItem("Progress Tracking", NavRoutes.ProgressTracking.route, Icons.Filled.ShowChart),
    DrawerItem("Setting & Preferences", NavRoutes.Settings.route, Icons.Filled.Settings)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawerApp(sharedData: SharedData) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val navController = rememberNavController()
    var selectedRoute by remember { mutableStateOf(NavRoutes.Dashboard.route) }
    val context = LocalContext.current

    var isLoggedOut by remember { mutableStateOf(false) }

    LaunchedEffect(isLoggedOut) {
        if (isLoggedOut) {
            Toast.makeText(context, "Logged out", Toast.LENGTH_SHORT).show()
            isLoggedOut = false
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text(
                    "Chronic Care Wellness Tracker",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(16.dp)
                )
                drawerItems.forEach { item ->
                    NavigationDrawerItem(
                        label = { Text(item.title) },
                        icon = { Icon(item.icon, contentDescription = item.title) },
                        selected = (item.route == selectedRoute),
                        onClick = {
                            selectedRoute = item.route
                            navController.navigate(item.route) {
                                launchSingleTop = true
                                restoreState = true
                                popUpTo(NavRoutes.Dashboard.route) { inclusive = false }
                            }
                            scope.launch { drawerState.close() }
                        }
                    )
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(id = R.drawable.logo),
                                contentDescription = "App Logo",
                                modifier = Modifier
                                    .size(40.dp)
                                    .padding(end = 8.dp)
                            )
                            Text(
                                text = "Chronic Care Wellness Tracker",
                                color = Color.White,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFF007F7A),
                        titleContentColor = Color.White
                    ),
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color.White)
                        }
                    },
                    actions = {
                        var expanded by remember { mutableStateOf(false) }

                        IconButton(onClick = { expanded = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "More options", tint = Color.White)
                        }

                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Settings") },
                                onClick = {
                                    expanded = false
                                    navController.navigate(NavRoutes.Settings.route)
                                }
                            )
                        }

                        IconButton(onClick = {
                            Toast.makeText(context, "Logged out", Toast.LENGTH_SHORT).show()

                            // Perform the navigation directly to login screen
                            navController.navigate("login") {
                                // Clear the back stack to ensure the user cannot navigate back to the previous screen
                                popUpTo("dashboard") { inclusive = true }
                            }
                        }) {
                            Icon(Icons.Default.ExitToApp, contentDescription = "Logout", tint = Color.White)
                        }


                    }
                )
            }
        ) { innerPadding ->
            //  Pass sharedData didi titkang sa drawerapp()
            NavigationHost(navController, Modifier.padding(innerPadding), sharedData)
        }
    }
}

//  Accept sharedData sa sulod sa NavigationHost
@Composable
fun NavigationHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    sharedData: SharedData
) {
    NavHost(
        navController = navController,
        startDestination = NavRoutes.Dashboard.route,
        modifier = modifier
    ) {
        composable(NavRoutes.Dashboard.route) { DashboardScreen(sharedData) }
        composable(NavRoutes.DailyLog.route) { DailyWeeklyLogScreen() }
        composable(NavRoutes.HealthInsights.route) { HealthInsightsScreen() }
        composable(NavRoutes.MedicationReminder.route) { MedicationTreatmentReminderScreen() }
        composable(NavRoutes.ProgressTracking.route) { ProgressTrackingScreen() }
        composable(NavRoutes.HydrationAndSleepTrackerScreen.route) { HealthTrackingApp() }

        // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
        composable(NavRoutes.Settings.route) { SettingsPreferences(sharedData) }

        composable(
            route = NavRoutes.Content.route,
            arguments = listOf(navArgument("itemId") { nullable = true })
        ) { backStackEntry ->
            val itemId = backStackEntry.arguments?.getString("itemId")
            ContentScreen(itemId)
        }
    }
}
