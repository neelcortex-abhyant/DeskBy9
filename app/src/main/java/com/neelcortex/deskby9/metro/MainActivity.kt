package com.neelcortex.deskby9.metro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.neelcortex.deskby9.metro.presentation.journey.JourneyPlanningScreen
import com.neelcortex.deskby9.metro.ui.theme.DeskBy9Theme
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.neelcortex.deskby9.metro.presentation.chat.ChatBotScreen
import com.neelcortex.deskby9.metro.presentation.live.LiveJourneyScreen
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main activity hosting the Metro app
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DeskBy9Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = "journey"
                    ) {
                        composable("journey") {
                            JourneyPlanningScreen(
                                onChatClick = {
                                    navController.navigate("chat")
                                },
                                onLiveTrackingClick = {
                                    navController.navigate("live_tracking")
                                }
                            )
                        }

                        composable("chat") {
                            ChatBotScreen(
                                onNavigateBack = {
                                    navController.popBackStack()
                                },
                                onNavigateToLiveTracking = {
                                    navController.navigate("live_tracking")
                                },
                                onNavigateToHome = {
                                    navController.navigate("journey") {
                                        popUpTo("journey") { inclusive = true }
                                    }
                                }
                            )
                        }
                        
                        composable("live_tracking") {
                            LiveJourneyScreen(
                                onStopJourney = {
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
