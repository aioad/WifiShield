package com.example

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.data.NetworkRepository
import com.example.data.database.AppDatabase
import com.example.ui.WifiShieldViewModel
import com.example.ui.WifiShieldViewModelFactory
import com.example.ui.screens.*
import com.example.ui.theme.*

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize SQLite Room Database and Repository Pattern elements
        val database = AppDatabase.getDatabase(applicationContext)
        val repository = NetworkRepository(database.networkDao())

        // Request vital Fine Location & Notification authorities if compatible
        val permissionsToRequest = mutableListOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ).apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                add(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        val requestPermissionsLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { _ ->
            // Permissions finished. The ViewModel will attempt to read the active SSID automatically.
        }

        requestPermissionsLauncher.launch(permissionsToRequest.toTypedArray())

        setContent {
            MyApplicationTheme {
                val viewModel: WifiShieldViewModel = viewModel(
                    factory = WifiShieldViewModelFactory(repository)
                )

                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                // Overlay warning triggers globally across state variables
                DangerWarningModal(viewModel = viewModel)

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        if (currentRoute != "splash") {
                            NavigationBar(
                                containerColor = CyberSlate,
                                modifier = Modifier.testTag("cyber_bottom_navigation_bar")
                            ) {
                                NavigationBarItem(
                                    selected = currentRoute == "dashboard",
                                    onClick = {
                                        navController.navigate("dashboard") {
                                            popUpTo("dashboard") { inclusive = false }
                                        }
                                    },
                                    icon = { Icon(imageVector = Icons.Filled.Shield, contentDescription = "Dashboard") },
                                    label = { Text("Shield", fontFamily = FontFamily.Monospace, fontSize = 10.sp) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = CyberBlue,
                                        selectedTextColor = CyberBlue,
                                        unselectedIconColor = Color.LightGray,
                                        unselectedTextColor = Color.LightGray,
                                        indicatorColor = CyberBlack
                                    ),
                                    modifier = Modifier.testTag("nav_btn_shield")
                                )

                                NavigationBarItem(
                                    selected = currentRoute == "trusted",
                                    onClick = { navController.navigate("trusted") },
                                    icon = { Icon(imageVector = Icons.Filled.VerifiedUser, contentDescription = "Trusted") },
                                    label = { Text("Exclusions", fontFamily = FontFamily.Monospace, fontSize = 10.sp) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = CyberBlue,
                                        selectedTextColor = CyberBlue,
                                        unselectedIconColor = Color.LightGray,
                                        unselectedTextColor = Color.LightGray,
                                        indicatorColor = CyberBlack
                                    ),
                                    modifier = Modifier.testTag("nav_btn_trusted")
                                )

                                NavigationBarItem(
                                    selected = currentRoute == "history",
                                    onClick = { navController.navigate("history") },
                                    icon = { Icon(imageVector = Icons.Filled.History, contentDescription = "History") },
                                    label = { Text("Audit Logs", fontFamily = FontFamily.Monospace, fontSize = 10.sp) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = CyberBlue,
                                        selectedTextColor = CyberBlue,
                                        unselectedIconColor = Color.LightGray,
                                        unselectedTextColor = Color.LightGray,
                                        indicatorColor = CyberBlack
                                    ),
                                    modifier = Modifier.testTag("nav_btn_history")
                                )

                                NavigationBarItem(
                                    selected = currentRoute == "settings",
                                    onClick = { navController.navigate("settings") },
                                    icon = { Icon(imageVector = Icons.Filled.Settings, contentDescription = "Settings") },
                                    label = { Text("System", fontFamily = FontFamily.Monospace, fontSize = 10.sp) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = CyberBlue,
                                        selectedTextColor = CyberBlue,
                                        unselectedIconColor = Color.LightGray,
                                        unselectedTextColor = Color.LightGray,
                                        indicatorColor = CyberBlack
                                    ),
                                    modifier = Modifier.testTag("nav_btn_settings")
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "splash",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("splash") {
                            SplashScreen(
                                onNavigateToDashboard = {
                                    navController.navigate("dashboard") {
                                        popUpTo("splash") { inclusive = true }
                                    }
                                }
                            )
                        }

                        composable("dashboard") {
                            DashboardScreen(viewModel = viewModel)
                        }

                        composable("trusted") {
                            TrustedNetworksScreen(viewModel = viewModel)
                        }

                        composable("history") {
                            HistoryLogsScreen(viewModel = viewModel)
                        }

                        composable("settings") {
                            SettingsScreen(viewModel = viewModel)
                        }
                    }
                }
            }
        }
    }
}
