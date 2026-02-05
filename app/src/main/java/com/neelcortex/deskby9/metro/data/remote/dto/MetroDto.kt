package com.neelcortex.deskby9.metro.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object for Station from Delhi Metro API
 */
data class StationDto(
    @SerializedName("id")
    val id: Int? = null,
    
    @SerializedName("station_name")
    val stationName: String? = null,
    
    @SerializedName("station_code")
    val stationCode: String? = null,
    
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
    val name: String? = null,
    
    @SerializedName("status")
    val status: String? = null
)

/**
 * Route line segment
 */
data class RouteLineDto(
    @SerializedName("line")
    val line: String? = null,
    
    @SerializedName("line_no")
    val lineNo: Int? = null,
    
    @SerializedName("path")
    val path: List<RoutePathStationDto>? = null
)

/**
 * Station status information
 */
data class StationStatusDto(
    @SerializedName("status")
    val status: String? = null,
    
    @SerializedName("title")
    val title: String? = null,
    
    @SerializedName("note")
    val note: String? = null
)

/**
 * Route calculation response from Delhi Metro API
 */
data class RouteResponseDto(
    @SerializedName("stations")
    val stations: Int? = null,
    
    @SerializedName("from")
    val from: String? = null,
    
    @SerializedName("to")
    val to: String? = null,
    
    @SerializedName("from_station_status")
    val fromStationStatus: StationStatusDto? = null,
    
    @SerializedName("to_station_status")
    val toStationStatus: StationStatusDto? = null,
    
    @SerializedName("total_time")
    val totalTime: String? = null, // Format: "0:52:30"
    
    @SerializedName("fare")
    val fare: Int? = null,
    
    @SerializedName("route")
    val route: List<RouteLineDto>? = null
)

