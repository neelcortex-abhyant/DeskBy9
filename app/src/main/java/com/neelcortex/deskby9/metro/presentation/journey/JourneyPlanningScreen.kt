package com.neelcortex.deskby9.metro.presentation.journey

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator as MaterialCircularProgressIndicator
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.neelcortex.deskby9.metro.presentation.journey.components.RouteDetailsBottomSheet
import com.neelcortex.deskby9.metro.presentation.journey.components.RouteSummaryScreen
import com.neelcortex.deskby9.metro.presentation.journey.components.RouteOptionCard
import com.neelcortex.deskby9.metro.presentation.journey.components.StationSearchDialog

/**
 * Main screen for journey planning
 */
@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun JourneyPlanningScreen(
    viewModel: JourneyPlanningViewModel = hiltViewModel(),
    onChatClick: () -> Unit = {},
    onLiveTrackingClick: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()
    var showRouteDetails by remember { mutableStateOf(false) }
    var showRouteSummary by remember { mutableStateOf(false) }
    
    // Location permissions
    val locationPermissions = rememberMultiplePermissionsState(
        permissions = listOf(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        )
    ) { permissions ->
        if (permissions.values.any { it }) {
            viewModel.checkLocationPermission()
        }
    }
    
    if (showRouteSummary && state.selectedRoute != null) {
        RouteSummaryScreen(
            route = state.selectedRoute!!,
            onModifyClick = {
                showRouteSummary = false
            }
        )
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Plan Your Journey") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onChatClick,
                containerColor = MaterialTheme.colorScheme.tertiaryContainer
            ) {
                Icon(
                    imageVector = Icons.Default.Chat,
                    contentDescription = "Chat Assistant"
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Origin selection
                item {
                    OriginSelectionCard(
                        originStation = state.originStation,
                        isLoadingLocation = state.isLoadingLocation,
                        onManualSelect = {
                            viewModel.showStationSearch(StationSearchMode.ORIGIN)
                        },
                        onAutoDetect = {
                            if (locationPermissions.allPermissionsGranted) {
                                viewModel.setOriginAutomatic()
                            } else {
                                locationPermissions.launchMultiplePermissionRequest()
                            }
                        },
                        onClear = { viewModel.clearOrigin() }
                    )
                }
                
                // Destination selection
                item {
                    DestinationSelectionCard(
                        destinationStation = state.destinationStation,
                        onClick = {
                            viewModel.showStationSearch(StationSearchMode.DESTINATION)
                        },
                        onClear = { viewModel.clearDestination() }
                    )
                }
                
                // Routes section
                if (state.isLoadingRoutes) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            MaterialCircularProgressIndicator()
                        }
                    }
                } else if (state.availableRoutes.isNotEmpty()) {
                    item {
                        Text(
                            text = "Available Routes",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    
                    items(
                        items = state.availableRoutes
                    ) { route ->
                        RouteOptionCard(
                            route = route,
                            isSelected = route.id == state.selectedRoute?.id,
                            onClick = {
                                viewModel.selectRoute(route)
                                showRouteSummary = true
                            }
                        )
                    }
                }
            }
            
            // Error snackbar
            state.error?.let { error ->
                Snackbar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
                    action = {
                        TextButton(onClick = { viewModel.clearError() }) {
                            Text("Dismiss")
                        }
                    }
                ) {
                    Text(error)
                }
            }
        }
    }
    
    // Station search dialog
    if (state.showStationSearchDialog) {
        StationSearchDialog(
            stations = state.availableStations,
            searchResults = state.searchResults,
            onSearch = { query -> viewModel.searchStations(query) },
            onStationSelected = { station ->
                when (state.searchDialogMode) {
                    StationSearchMode.ORIGIN -> viewModel.setOriginManual(station)
                    StationSearchMode.DESTINATION -> viewModel.setDestination(station)
                }
            },
            onDismiss = { viewModel.hideStationSearch() }
        )
    }
    
    // Route details bottom sheet
    if (showRouteDetails && state.selectedRoute != null) {
        RouteDetailsBottomSheet(
            route = state.selectedRoute!!,
            onDismiss = { showRouteDetails = false },
            onConfirm = {
                showRouteDetails = false
                onLiveTrackingClick()
            }
        )
    }
}

/**
 * Card for origin selection
 */
@Composable
private fun OriginSelectionCard(
    originStation: com.neelcortex.deskby9.metro.domain.model.Station?,
    isLoadingLocation: Boolean,
    onManualSelect: () -> Unit,
    onAutoDetect: () -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "From",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                if (originStation != null) {
                    IconButton(onClick = onClear) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear origin"
                        )
                    }
                }
            }
            
            if (originStation != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = originStation.name,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = originStation.line,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            } else {
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onAutoDetect,
                        modifier = Modifier.weight(1f),
                        enabled = !isLoadingLocation
                    ) {
                        if (isLoadingLocation) {
                            MaterialCircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.MyLocation,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Use GPS")
                    }
                    
                    Button(
                        onClick = onManualSelect,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Place,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Select")
                    }
                }
            }
        }
    }
}

/**
 * Card for destination selection
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DestinationSelectionCard(
    destinationStation: com.neelcortex.deskby9.metro.domain.model.Station?,
    onClick: () -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "To",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                
                if (destinationStation != null) {
                    IconButton(onClick = onClear) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear destination"
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            if (destinationStation != null) {
                Text(
                    text = destinationStation.name,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = destinationStation.line,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            } else {
                Text(
                    text = "Select destination station",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
            }
        }
    }
}
