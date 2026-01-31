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
        id = stationCode, // Use station code as ID
        name = stationName,
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
        3 -> parts[0].toInt() * 60 + parts[1].toInt() // hours:minutes:seconds
        2 -> parts[0].toInt() // minutes:seconds
        else -> 0
    }
}

/**
 * Convert RouteResponseDto to domain Route model
 */
fun RouteResponseDto.toDomain(routeType: RouteType = RouteType.FASTEST): Route {
    // Create stations from route path
    val allStations = mutableListOf<Station>()
    val segments = mutableListOf<RouteSegment>()
    
    // Extract all stations from the route
    route.forEach { routeLine ->
        routeLine.path.forEach { pathStation ->
            allStations.add(
                Station(
                    id = pathStation.name.replace(" ", "_").uppercase(),
                    name = pathStation.name,
                    line = routeLine.line,
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
    
    route.forEach { routeLine ->
        routeLine.path.forEachIndexed { index, pathStation ->
            val currentStation = Station(
                id = pathStation.name.replace(" ", "_").uppercase(),
                name = pathStation.name,
                line = routeLine.line,
                latitude = 0.0,
                longitude = 0.0,
                isInterchange = false,
                interchangeLines = emptyList()
            )
            
            if (previousStation != null) {
                val isTransfer = currentLine != routeLine.line
                segments.add(
                    RouteSegment(
                        fromStation = previousStation!!,
                        toStation = currentStation,
                        line = routeLine.line,
                        durationMinutes = 2, // Approximate 2 minutes per station
                        distanceKm = 1.0, // Approximate 1 km per station
                        isTransfer = isTransfer
                    )
                )
            }
            
            previousStation = currentStation
            currentLine = routeLine.line
        }
    }
    
    val originStation = allStations.firstOrNull() ?: Station(
        id = "UNKNOWN",
        name = from,
        line = route.firstOrNull()?.line ?: "",
        latitude = 0.0,
        longitude = 0.0,
        isInterchange = false,
        interchangeLines = emptyList()
    )
    
    val destinationStation = allStations.lastOrNull() ?: Station(
        id = "UNKNOWN",
        name = to,
        line = route.lastOrNull()?.line ?: "",
        latitude = 0.0,
        longitude = 0.0,
        isInterchange = false,
        interchangeLines = emptyList()
    )
    
    // Count transfers (number of line changes)
    val numberOfTransfers = route.size - 1
    
    return Route(
        id = UUID.randomUUID().toString(),
        origin = originStation,
        destination = destinationStation,
        segments = segments,
        totalDurationMinutes = parseTimeToMinutes(totalTime),
        totalDistanceKm = stations * 1.0, // Approximate
        numberOfTransfers = numberOfTransfers.coerceAtLeast(0),
        routeType = routeType,
        fareRupees = fare.toDouble()
    )
}

