package com.neelcortex.deskby9.metro.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object for Station from Delhi Metro API
 */
data class StationDto(
    @SerializedName("id")
    val id: Int,
    
    @SerializedName("station_name")
    val stationName: String,
    
    @SerializedName("station_code")
    val stationCode: String,
    
    @SerializedName("station_facility")
    val stationFacility: List<StationFacilityDto>? = null,
    
    // These fields might not be in the station_list but we'll add them for completeness
    @SerializedName("latitude")
    val latitude: Double? = null,
    
    @SerializedName("longitude")
    val longitude: Double? = null
)

/**
 * Station facility information
 */
data class StationFacilityDto(
    @SerializedName("name")
    val name: String,
    
    @SerializedName("class_name")
    val className: String,
    
    @SerializedName("image")
    val image: ImageDto?
)

/**
 * Image information
 */
data class ImageDto(
    @SerializedName("title")
    val title: String,
    
    @SerializedName("file")
    val file: String
)

/**
 * Metro Line information
 */
data class LineDto(
    @SerializedName("id")
    val id: Int,
    
    @SerializedName("name")
    val name: String,
    
    @SerializedName("line_color")
    val lineColor: String,
    
    @SerializedName("line_code")
    val lineCode: String,
    
    @SerializedName("primary_color_code")
    val primaryColorCode: String,
    
    @SerializedName("start_station")
    val startStation: String,
    
    @SerializedName("end_station")
    val endStation: String,
    
    @SerializedName("status")
    val status: String
)

/**
 * Route path station
 */
data class RoutePathStationDto(
    @SerializedName("name")
    val name: String,
    
    @SerializedName("status")
    val status: String
)

/**
 * Route line segment
 */
data class RouteLineDto(
    @SerializedName("line")
    val line: String,
    
    @SerializedName("line_no")
    val lineNo: Int,
    
    @SerializedName("path")
    val path: List<RoutePathStationDto>
)

/**
 * Station status information
 */
data class StationStatusDto(
    @SerializedName("status")
    val status: String,
    
    @SerializedName("title")
    val title: String,
    
    @SerializedName("note")
    val note: String
)

/**
 * Route calculation response from Delhi Metro API
 */
data class RouteResponseDto(
    @SerializedName("stations")
    val stations: Int,
    
    @SerializedName("from")
    val from: String,
    
    @SerializedName("to")
    val to: String,
    
    @SerializedName("from_station_status")
    val fromStationStatus: StationStatusDto,
    
    @SerializedName("to_station_status")
    val toStationStatus: StationStatusDto,
    
    @SerializedName("total_time")
    val totalTime: String, // Format: "0:52:30"
    
    @SerializedName("fare")
    val fare: Int,
    
    @SerializedName("route")
    val route: List<RouteLineDto>
)

