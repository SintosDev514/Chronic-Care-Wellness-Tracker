package com.example.chronicare

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.chronicare.screens.LoginScreen
import com.example.chronicare.screens.SignUpScreen
import com.example.chronicare.screens.HomeScreenMain
import com.example.chronicare.screens.SharedData
import com.example.chronicare.homeScreens.SettingsPreferences
import com.example.chronicare.homeScreens.DashboardScreen

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()

    val sharedData : SharedData = viewModel()

    NavHost(navController = navController, startDestination = "login") {
        composable("login") { LoginScreen(navController, sharedData) }
        composable("SettingsPreferences") {SettingsPreferences(sharedData)}
        composable("signup") { SignUpScreen(navController, sharedData) }
        composable("dashboard"){DashboardScreen(sharedData)}
        composable("home") { HomeScreenMain(sharedData) }

    }
}
