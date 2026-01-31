package com.neelcortex.deskby9.metro.domain.model

/**
 * Represents a Metro Station in the Delhi Metro network
 */
data class Station(
    val id: String,
    val name: String,
    val line: String,
    val latitude: Double,
    val longitude: Double,
    val isInterchange: Boolean = false,
    val interchangeLines: List<String> = emptyList()
) {
    /**
     * Calculate distance to another station in kilometers
     */
    fun distanceTo(other: Station): Double {
        val earthRadius = 6371.0 // km
        val dLat = Math.toRadians(other.latitude - latitude)
        val dLon = Math.toRadians(other.longitude - longitude)
        
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(latitude)) * Math.cos(Math.toRadians(other.latitude)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)
        
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        return earthRadius * c
    }
}
