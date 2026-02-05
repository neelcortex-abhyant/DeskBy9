package com.neelcortex.deskby9.metro.presentation.journey.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.neelcortex.deskby9.metro.domain.model.Route

/**
 * Full-screen summary for a selected route, inspired by Delhi Metro web view.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteSummaryScreen(
    route: Route,
    onModifyClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "${route.origin.name} → ${route.destination.name}",
                        maxLines = 1
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onModifyClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Modify route"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            RouteSummaryHeaderCard(route = route, onModifyClick = onModifyClick)

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Journey details",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Reuse the same station timeline logic as the bottom sheet
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                itemsIndexed(route.stations) { index, station ->
                    StationTimelineItem(
                        station = station,
                        isFirst = index == 0,
                        isLast = index == route.stations.lastIndex,
                        isTransfer = route.transferStations.contains(station),
                        line = if (index < route.segments.size) {
                            route.segments[index].line
                        } else {
                            route.segments.lastOrNull()?.line ?: ""
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun RouteSummaryHeaderCard(
    route: Route,
    onModifyClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.fillMaxWidth()
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
                FilledTonalButton(onClick = onModifyClick) {
                    Text("Modify")
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "${route.origin.name} → ${route.destination.name}",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "${route.stations.size} stations",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Duration and fare row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Duration",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = route.formattedDuration,
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                if (route.fareRupees > 0) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Fare",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "₹${route.fareRupees.toInt()}",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

