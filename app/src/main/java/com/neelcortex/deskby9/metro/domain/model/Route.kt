package com.neelcortex.deskby9.metro.domain.model

/**
 * Represents different types of routes available
 */
enum class RouteType {
    FASTEST,           // Quickest route by time
    FEWEST_TRANSFERS,  // Route with minimum number of transfers
    SHORTEST_DISTANCE  // Route with minimum distance
}

/**
 * Represents a single segment of a journey between two stations
 */
data class RouteSegment(
    val fromStation: Station,
    val toStation: Station,
    val line: String,
    val durationMinutes: Int,
    val distanceKm: Double,
    val isTransfer: Boolean = false
)

/**
 * Represents a complete route from origin to destination
 */
data class Route(
    val id: String,
    val origin: Station,
    val destination: Station,
    val segments: List<RouteSegment>,
    val totalDurationMinutes: Int,
    val totalDistanceKm: Double,
    val numberOfTransfers: Int,
    val routeType: RouteType,
    val fareRupees: Double = 0.0
) {
    /**
     * Get list of all stations in the route
     */
    val stations: List<Station>
        get() = buildList {
            if (segments.isNotEmpty()) {
                add(segments.first().fromStation)
                segments.forEach { add(it.toStation) }
            }
        }
    
    /**
     * Get list of transfer stations
     */
    val transferStations: List<Station>
        get() = segments.filter { it.isTransfer }.map { it.fromStation }
    
    /**
     * Get formatted duration string
     */
    val formattedDuration: String
        get() {
            val hours = totalDurationMinutes / 60
            val minutes = totalDurationMinutes % 60
            return if (hours > 0) {
                "${hours}h ${minutes}m"
            } else {
                "${minutes}m"
            }
        }
}
