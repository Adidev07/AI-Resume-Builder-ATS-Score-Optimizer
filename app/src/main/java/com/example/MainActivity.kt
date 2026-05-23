package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val viewModel: MainViewModel = viewModel()
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                // Defines standard bottom navigation destinations
                val navItems = listOf(
                    NavigationItem("landing", "Concept", Icons.Filled.Stars, Icons.Outlined.Stars),
                    NavigationItem("dashboard", "Workspace", Icons.Filled.Folder, Icons.Outlined.FolderOpen),
                    NavigationItem("ats_scanner", "ATS Checker", Icons.Filled.Troubleshoot, Icons.Outlined.Troubleshoot),
                    NavigationItem("cover_letter", "AI Letter", Icons.Filled.RateReview, Icons.Outlined.RateReview)
                )

                // Only show Bottom Navigation if we are not inside full-screen workspaces (like Builder or JobTracker)
                val showBottomNav = currentRoute in navItems.map { it.route }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        if (showBottomNav) {
                            NavigationBar(
                                modifier = Modifier.testTag("bottom_nav"),
                                tonalElevation = 8.dp
                            ) {
                                val context = LocalContext.current
                                navItems.forEach { item ->
                                    val isSelected = currentRoute == item.route
                                    NavigationBarItem(
                                        selected = isSelected,
                                        onClick = {
                                            if (currentRoute != item.route) {
                                                navController.navigate(item.route) {
                                                    popUpTo(navController.graph.startDestinationId) {
                                                        saveState = true
                                                    }
                                                    launchSingleTop = true
                                                    restoreState = true
                                                }
                                            }
                                        },
                                        icon = {
                                            Icon(
                                                imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                                contentDescription = item.label,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        },
                                        label = {
                                            Text(
                                                text = item.label,
                                                fontSize = 10.sp,
                                                fontWeight = if (isSelected) androidx.compose.ui.text.font.FontWeight.Black else androidx.compose.ui.text.font.FontWeight.Bold
                                            )
                                        }
                                    )
                                }
                            }
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "landing", // Shows landing page product walkthrough first for strong conversion
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        // 1. LANDING SCREEN
                        composable("landing") {
                            val context = LocalContext.current
                            LandingScreen(
                                onNavigateToDashboard = {
                                    navController.navigate("dashboard") {
                                        popUpTo("landing") { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                onSimulatePremiumBuy = { planType ->
                                    viewModel.updatePlan("pro") // instantly upgrade state to unlocked PRO
                                    Toast.makeText(context, "🎉 Invoice paid! SaaS Account upgraded to Pro Lifetime successfully!", Toast.LENGTH_LONG).show()
                                    navController.navigate("dashboard") {
                                        launchSingleTop = true
                                    }
                                }
                            )
                        }

                        // 2. WORKSPACE / DASHBOARD
                        composable("dashboard") {
                            DashboardScreen(
                                viewModel = viewModel,
                                onNavigateToBuilder = { id ->
                                    navController.navigate("builder/$id")
                                },
                                onNavigateToPricing = {
                                    navController.navigate("landing")
                                },
                                onNavigateToJobTracker = {
                                    navController.navigate("job_tracker")
                                }
                            )
                        }

                        // 3. SPECIALIZED DOCUMENT BUILDER (DEEP LINK PATH ID)
                        composable(
                            route = "builder/{resumeId}",
                            arguments = listOf(navArgument("resumeId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val resumeId = backStackEntry.arguments?.getString("resumeId") ?: ""
                            ResumeBuilderScreen(
                                viewModel = viewModel,
                                resumeId = resumeId,
                                onNavigateBack = {
                                    navController.popBackStack()
                                }
                            )
                        }

                        // 4. ATS SCANNER BOARD PANEL
                        composable("ats_scanner") {
                            AtsCheckerScreen(
                                viewModel = viewModel
                            )
                        }

                        // 5. COVER LETTER GENERATOR
                        composable("cover_letter") {
                            CoverLetterScreen(
                                viewModel = viewModel
                            )
                        }

                        // 6. PIPELINE JOB TRACKER CRM
                        composable("job_tracker") {
                            JobTrackerScreen(
                                viewModel = viewModel,
                                onNavigateBack = {
                                    navController.popBackStack()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

data class NavigationItem(
    val route: String,
    val label: String,
    val selectedIcon: androidx.compose.ui.graphics.vector.ImageVector,
    val unselectedIcon: androidx.compose.ui.graphics.vector.ImageVector
)
