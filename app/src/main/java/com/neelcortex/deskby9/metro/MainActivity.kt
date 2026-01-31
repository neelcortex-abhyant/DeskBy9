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
                    JourneyPlanningScreen()
                }
            }
        }
    }
}
