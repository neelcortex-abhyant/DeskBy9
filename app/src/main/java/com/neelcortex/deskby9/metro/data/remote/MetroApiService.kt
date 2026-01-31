package com.neelcortex.deskby9.metro.data.remote

import com.neelcortex.deskby9.metro.data.remote.dto.LineDto
import com.neelcortex.deskby9.metro.data.remote.dto.RouteResponseDto
import com.neelcortex.deskby9.metro.data.remote.dto.StationDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit API service for Delhi Metro API
 * Base URL: https://backend.delhimetrorail.com/api/v2/en/
 */
interface MetroApiService {
    
    /**
     * Get all metro stations
     */
    @GET("station_list")
    suspend fun getStations(): List<StationDto>
    
    /**
     * Get all metro lines
     */
    @GET("line_list")
    suspend fun getLines(): List<LineDto>
    
    /**
     * Calculate route between two stations
     * @param fromStationCode Station code of origin (e.g., "NDI")
     * @param toStationCode Station code of destination (e.g., "HCC")
     * @param filter Route filter: "least-distance" or "minimum-interchange"
     * @param timestamp Current timestamp in ISO format (e.g., "2026-01-31T13:47:28.596")
     */
    @GET("station_route/{from}/{to}/{filter}/{timestamp}")
    suspend fun calculateRoute(
        @Path("from") fromStationCode: String,
        @Path("to") toStationCode: String,
        @Path("filter") filter: String = "least-distance",
        @Path("timestamp") timestamp: String
    ): RouteResponseDto
}

