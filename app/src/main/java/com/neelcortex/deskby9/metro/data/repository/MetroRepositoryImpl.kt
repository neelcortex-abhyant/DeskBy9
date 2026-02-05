package com.neelcortex.deskby9.metro.data.repository

import com.neelcortex.deskby9.metro.data.remote.MetroApiService
import com.neelcortex.deskby9.metro.data.remote.dto.StationDto
import com.neelcortex.deskby9.metro.data.remote.dto.toDomain
import com.neelcortex.deskby9.metro.domain.model.Route
import com.neelcortex.deskby9.metro.domain.model.RouteType
import com.neelcortex.deskby9.metro.domain.model.Station
import com.neelcortex.deskby9.metro.domain.repository.MetroRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.*

/**
 * Implementation of MetroRepository using Delhi Metro API
 */
@Singleton
class MetroRepositoryImpl @Inject constructor(
    private val apiService: MetroApiService
) : MetroRepository {
    
    // Cache stations to avoid repeated API calls
    private var cachedStations: List<StationDto>? = null
    
    override fun getStations(): Flow<Result<List<Station>>> = flow {
        try {
            val stations = cachedStations ?: apiService.getStations().also { 
                cachedStations = it 
            }
            emit(Result.success(stations.map { it.toDomain() }))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.Default)
    
    override fun searchStations(query: String): Flow<Result<List<Station>>> = flow {
        try {
            val stations = cachedStations ?: apiService.getStations().also { 
                cachedStations = it 
            }
            
            // Filter stations locally by name
            val filtered = stations.filter { 
                (it.stationName?.contains(query, ignoreCase = true) == true) ||
                (it.stationCode?.contains(query, ignoreCase = true) == true)
            }
            
            emit(Result.success(filtered.map { it.toDomain() }))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.Default)
    
    override fun getStationById(stationId: String): Flow<Result<Station>> = flow {
        try {
            val stations = cachedStations ?: apiService.getStations().also { 
                cachedStations = it 
            }
            
            val station = stations.find { 
                (it.stationCode != null && it.stationCode == stationId) || 
                (it.id != null && it.id.toString() == stationId) 
            }
            
            if (station != null) {
                emit(Result.success(station.toDomain()))
            } else {
                emit(Result.failure(Exception("Station not found: $stationId")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.Default)
    
    override fun getNearestStation(
        latitude: Double,
        longitude: Double
    ): Flow<Result<Station>> = flow {
        try {
            val stations = cachedStations ?: apiService.getStations().also { 
                cachedStations = it 
            }
            
            // Calculate distance to each station and find the nearest
            val nearest = stations
                .filter { it.latitude != null && it.longitude != null }
                .minByOrNull { station ->
                    calculateDistance(
                        latitude, longitude,
                        station.latitude!!, station.longitude!!
                    )
                }
            
            if (nearest != null) {
                emit(Result.success(nearest.toDomain()))
            } else {
                emit(Result.failure(Exception("No stations found with coordinates")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.Default)
    
    override fun calculateRoutes(
        originId: String,
        destinationId: String
    ): Flow<Result<List<Route>>> = flow {
        try {
            if (originId == "UNKNOWN" || destinationId == "UNKNOWN" || originId.isBlank() || destinationId.isBlank()) {
                emit(Result.failure(IllegalArgumentException("Invalid station info. Please re-select stations.")))
                return@flow
            }

            // Use SimpleDateFormat for compatibility with older Android versions (minSdk 24)
            // that might not have java.time desugaring enabled
            val sdf = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", java.util.Locale.US)
            val timestamp = sdf.format(java.util.Date())
            
            // Calculate both route types independently to prevent total failure if one fails
            val routes = mutableListOf<Route>()
            
            // 1. Least Distance
            try {
                val leastDistanceRoute = apiService.calculateRoute(
                    fromStationCode = originId,
                    toStationCode = destinationId,
                    filter = "least-distance",
                    timestamp = timestamp
                ).toDomain(RouteType.SHORTEST_DISTANCE)
                routes.add(leastDistanceRoute)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            
            // 2. Minimum Interchange
            try {
                val minimumInterchangeRoute = apiService.calculateRoute(
                    fromStationCode = originId,
                    toStationCode = destinationId,
                    filter = "minimum-interchange",
                    timestamp = timestamp
                ).toDomain(RouteType.FEWEST_TRANSFERS)
                // Avoid adding duplicate route if it's the same as least distance
                if (routes.none { it.totalDistanceKm == minimumInterchangeRoute.totalDistanceKm && 
                                  it.numberOfTransfers == minimumInterchangeRoute.numberOfTransfers }) {
                    routes.add(minimumInterchangeRoute)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            
            if (routes.isNotEmpty()) {
                emit(Result.success(routes))
            } else {
                emit(Result.failure(Exception("Could not calculate any routes. Please try again.")))
            }
        } catch (e: Throwable) {
            // Catch Throwable to handle Errors (like NoClassDefFoundError) and Exceptions
            e.printStackTrace()
            emit(Result.failure(Exception(e.message ?: "Unknown error occurred", e)))
        }
    }.flowOn(Dispatchers.IO)
    
    override fun getRouteById(routeId: String): Flow<Result<Route>> = flow {
        // This endpoint doesn't exist in Delhi Metro API
        // We would need to cache routes or recalculate
        emit(Result.failure(Exception("Route retrieval by ID not supported")))
    }
    
    /**
     * Calculate distance between two coordinates using Haversine formula
     */
    private fun calculateDistance(
        lat1: Double, lon1: Double,
        lat2: Double, lon2: Double
    ): Double {
        val earthRadius = 6371.0 // km
        
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)
        
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return earthRadius * c
    }
}

