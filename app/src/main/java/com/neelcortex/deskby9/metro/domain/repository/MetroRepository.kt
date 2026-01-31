package com.neelcortex.deskby9.metro.domain.repository

import com.neelcortex.deskby9.metro.domain.model.Route
import com.neelcortex.deskby9.metro.domain.model.Station
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for Metro data operations
 */
interface MetroRepository {
    
    /**
     * Get all metro stations
     * @return Flow of Result containing list of stations
     */
    fun getStations(): Flow<Result<List<Station>>>
    
    /**
     * Search stations by name
     * @param query Search query
     * @return Flow of Result containing filtered list of stations
     */
    fun searchStations(query: String): Flow<Result<List<Station>>>
    
    /**
     * Get station by ID
     * @param stationId Station identifier
     * @return Flow of Result containing the station
     */
    fun getStationById(stationId: String): Flow<Result<Station>>
    
    /**
     * Find nearest station to given coordinates
     * @param latitude Latitude coordinate
     * @param longitude Longitude coordinate
     * @return Flow of Result containing the nearest station
     */
    fun getNearestStation(latitude: Double, longitude: Double): Flow<Result<Station>>
    
    /**
     * Calculate routes between origin and destination
     * @param originId Origin station ID
     * @param destinationId Destination station ID
     * @return Flow of Result containing list of possible routes
     */
    fun calculateRoutes(originId: String, destinationId: String): Flow<Result<List<Route>>>
    
    /**
     * Get route details by ID
     * @param routeId Route identifier
     * @return Flow of Result containing the route
     */
    fun getRouteById(routeId: String): Flow<Result<Route>>
}
