package com.neelcortex.deskby9.metro.data.remote.dto

import com.neelcortex.deskby9.metro.domain.model.Route
import com.neelcortex.deskby9.metro.domain.model.RouteSegment
import com.neelcortex.deskby9.metro.domain.model.RouteType
import com.neelcortex.deskby9.metro.domain.model.Station
import java.util.UUID

/**
 * Extension functions to map DTOs to domain models
 */

fun StationDto.toDomain(): Station {
    return Station(
        id = stationCode ?: id?.toString() ?: "UNKNOWN", // Use station code as ID, fallback to numeric ID
        name = stationName ?: "Unknown Station",
        line = "", // Line info not directly available in station list
        latitude = latitude ?: 0.0,
        longitude = longitude ?: 0.0,
        isInterchange = false, // Will be determined from route data
        interchangeLines = emptyList()
    )
}

/**
 * Parse time string from format "0:52:30" to minutes
 */
private fun parseTimeToMinutes(timeString: String): Int {
    val parts = timeString.split(":")
    return when (parts.size) {
        3 -> (parts[0].toIntOrNull() ?: 0) * 60 + (parts[1].toIntOrNull() ?: 0) // hours:minutes:seconds
        2 -> parts[0].toIntOrNull() ?: 0 // minutes:seconds
        else -> 0
    }
}

/**
 * Convert RouteResponseDto to domain Route model
 */
fun RouteResponseDto.toDomain(routeType: RouteType = RouteType.FASTEST): Route {
    // Safety check for null route
    val safeRoute = route ?: emptyList()
    
    // Create stations from route path
    val allStations = mutableListOf<Station>()
    val segments = mutableListOf<RouteSegment>()
    
    // Extract all stations from the route
    safeRoute.forEach { routeLine ->
        routeLine.path?.forEach { pathStation ->
            val stationName = pathStation.name ?: "Unknown Station"
            allStations.add(
                Station(
                    id = stationName.replace(" ", "_").uppercase(),
                    name = stationName,
                    line = routeLine.line ?: "",
                    latitude = 0.0, // Not provided in route response
                    longitude = 0.0,
                    isInterchange = false,
                    interchangeLines = emptyList()
                )
            )
        }
    }
    
    // Create segments between consecutive stations
    var previousStation: Station? = null
    var currentLine = ""
    
    safeRoute.forEach { routeLine ->
        val safePath = routeLine.path ?: emptyList()
        val safeLineName = routeLine.line ?: ""
        
        safePath.forEachIndexed { index, pathStation ->
            val stationName = pathStation.name ?: "Unknown Station"
            val currentStation = Station(
                id = stationName.replace(" ", "_").uppercase(),
                name = stationName,
                line = safeLineName,
                latitude = 0.0,
                longitude = 0.0,
                isInterchange = false,
                interchangeLines = emptyList()
            )
            
            if (previousStation != null) {
                val isTransfer = currentLine != safeLineName
                segments.add(
                    RouteSegment(
                        fromStation = previousStation!!,
                        toStation = currentStation,
                        line = safeLineName,
                        durationMinutes = 2, // Approximate 2 minutes per station
                        distanceKm = 1.0, // Approximate 1 km per station
                        isTransfer = isTransfer
                    )
                )
            }
            
            previousStation = currentStation
            currentLine = safeLineName
        }
    }
    
    val safeFrom = from ?: "Unknown Origin"
    val safeTo = to ?: "Unknown Destination"
    
    val originStation = allStations.firstOrNull() ?: Station(
        id = "UNKNOWN_ORIGIN",
        name = safeFrom,
        line = safeRoute.firstOrNull()?.line ?: "",
        latitude = 0.0,
        longitude = 0.0,
        isInterchange = false,
        interchangeLines = emptyList()
    )
    
    val destinationStation = allStations.lastOrNull() ?: Station(
        id = "UNKNOWN_DEST",
        name = safeTo,
        line = safeRoute.lastOrNull()?.line ?: "",
        latitude = 0.0,
        longitude = 0.0,
        isInterchange = false,
        interchangeLines = emptyList()
    )
    
    // Count transfers (number of line changes)
    val numberOfTransfers = (safeRoute.size - 1).coerceAtLeast(0)
    val safeTotalTime = totalTime ?: "0:00"
    
    return Route(
        id = "${originStation.id}-${destinationStation.id}-${routeType.name}",
        origin = originStation,
        destination = destinationStation,
        segments = segments,
        totalDurationMinutes = parseTimeToMinutes(safeTotalTime),
        totalDistanceKm = (stations ?: 0).toDouble(), // Approximate
        numberOfTransfers = numberOfTransfers,
        routeType = routeType,
        fareRupees = fare?.toDouble() ?: 0.0
    )
}

