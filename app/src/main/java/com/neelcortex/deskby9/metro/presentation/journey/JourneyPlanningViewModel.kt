package com.neelcortex.deskby9.metro.presentation.journey

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neelcortex.deskby9.metro.data.location.LocationService
import com.neelcortex.deskby9.metro.domain.model.Route
import com.neelcortex.deskby9.metro.domain.model.Station
import com.neelcortex.deskby9.metro.domain.repository.MetroRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI State for Journey Planning
 */
data class JourneyPlanningState(
    val originStation: Station? = null,
    val destinationStation: Station? = null,
    val availableStations: List<Station> = emptyList(),
    val searchResults: List<Station> = emptyList(),
    val availableRoutes: List<Route> = emptyList(),
    val selectedRoute: Route? = null,
    val isLoading: Boolean = false,
    val isLoadingLocation: Boolean = false,
    val isLoadingRoutes: Boolean = false,
    val error: String? = null,
    val locationPermissionGranted: Boolean = false,
    val showStationSearchDialog: Boolean = false,
    val searchDialogMode: StationSearchMode = StationSearchMode.DESTINATION
)

/**
 * Mode for station search dialog
 */
enum class StationSearchMode {
    ORIGIN,
    DESTINATION
}

/**
 * ViewModel for Journey Planning screen
 */
@HiltViewModel
class JourneyPlanningViewModel @Inject constructor(
    private val metroRepository: MetroRepository,
    private val locationService: LocationService
) : ViewModel() {
    
    private val _state = MutableStateFlow(JourneyPlanningState())
    val state: StateFlow<JourneyPlanningState> = _state.asStateFlow()
    
    init {
        loadStations()
        checkLocationPermission()
    }
    
    /**
     * Load all available stations
     */
    private fun loadStations() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            
            metroRepository.getStations().collect { result ->
                result.fold(
                    onSuccess = { stations ->
                        _state.update {
                            it.copy(
                                availableStations = stations,
                                isLoading = false
                            )
                        }
                    },
                    onFailure = { exception ->
                        _state.update {
                            it.copy(
                                isLoading = false,
                                error = exception.message ?: "Failed to load stations"
                            )
                        }
                    }
                )
            }
        }
    }
    
    /**
     * Check if location permission is granted
     */
    fun checkLocationPermission() {
        val hasPermission = locationService.hasLocationPermission()
        _state.update { it.copy(locationPermissionGranted = hasPermission) }
    }
    
    /**
     * Set destination station
     */
    fun setDestination(station: Station) {
        _state.update {
            it.copy(
                destinationStation = station,
                showStationSearchDialog = false,
                error = null
            )
        }
        calculateRoutesIfReady()
    }
    
    /**
     * Set origin station manually
     */
    fun setOriginManual(station: Station) {
        _state.update {
            it.copy(
                originStation = station,
                showStationSearchDialog = false,
                error = null
            )
        }
        calculateRoutesIfReady()
    }
    
    /**
     * Set origin station automatically using GPS
     */
    fun setOriginAutomatic() {
        if (!_state.value.locationPermissionGranted) {
            _state.update { it.copy(error = "Location permission not granted") }
            return
        }
        
        viewModelScope.launch {
            _state.update { it.copy(isLoadingLocation = true, error = null) }
            
            try {
                val location = locationService.getCurrentLocation()
                if (location != null) {
                    metroRepository.getNearestStation(
                        location.latitude,
                        location.longitude
                    ).collect { result ->
                        result.fold(
                            onSuccess = { station ->
                                _state.update {
                                    it.copy(
                                        originStation = station,
                                        isLoadingLocation = false
                                    )
                                }
                                calculateRoutesIfReady()
                            },
                            onFailure = { exception ->
                                _state.update {
                                    it.copy(
                                        isLoadingLocation = false,
                                        error = exception.message ?: "Failed to find nearest station"
                                    )
                                }
                            }
                        )
                    }
                } else {
                    _state.update {
                        it.copy(
                            isLoadingLocation = false,
                            error = "Unable to get current location"
                        )
                    }
                }
            } catch (e: SecurityException) {
                _state.update {
                    it.copy(
                        isLoadingLocation = false,
                        error = "Location permission denied"
                    )
                }
            }
        }
    }
    
    /**
     * Search stations by query
     */
    fun searchStations(query: String) {
        if (query.isBlank()) {
            _state.update { it.copy(searchResults = emptyList()) }
            return
        }
        
        viewModelScope.launch {
            metroRepository.searchStations(query).collect { result ->
                result.fold(
                    onSuccess = { stations ->
                        _state.update { it.copy(searchResults = stations) }
                    },
                    onFailure = { exception ->
                        _state.update {
                            it.copy(error = exception.message ?: "Search failed")
                        }
                    }
                )
            }
        }
    }
    
    /**
     * Calculate routes if both origin and destination are set
     */
    private fun calculateRoutesIfReady() {
        val origin = _state.value.originStation
        val destination = _state.value.destinationStation
        
        if (origin != null && destination != null) {
            calculateRoutes(origin.id, destination.id)
        }
    }
    
    /**
     * Calculate routes between origin and destination
     */
    private fun calculateRoutes(originId: String, destinationId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoadingRoutes = true, error = null) }
            
            metroRepository.calculateRoutes(originId, destinationId).collect { result ->
                result.fold(
                    onSuccess = { routes ->
                        _state.update {
                            it.copy(
                                availableRoutes = routes,
                                isLoadingRoutes = false,
                                selectedRoute = routes.firstOrNull()
                            )
                        }
                    },
                    onFailure = { exception ->
                        _state.update {
                            it.copy(
                                isLoadingRoutes = false,
                                error = exception.message ?: "Failed to calculate routes"
                            )
                        }
                    }
                )
            }
        }
    }
    
    /**
     * Select a route
     */
    fun selectRoute(route: Route) {
        _state.update { it.copy(selectedRoute = route) }
    }
    
    /**
     * Show station search dialog
     */
    fun showStationSearch(mode: StationSearchMode) {
        _state.update {
            it.copy(
                showStationSearchDialog = true,
                searchDialogMode = mode,
                searchResults = emptyList()
            )
        }
    }
    
    /**
     * Hide station search dialog
     */
    fun hideStationSearch() {
        _state.update {
            it.copy(
                showStationSearchDialog = false,
                searchResults = emptyList()
            )
        }
    }
    
    /**
     * Clear error message
     */
    fun clearError() {
        _state.update { it.copy(error = null) }
    }
    
    /**
     * Clear origin station
     */
    fun clearOrigin() {
        _state.update {
            it.copy(
                originStation = null,
                availableRoutes = emptyList(),
                selectedRoute = null
            )
        }
    }
    
    /**
     * Clear destination station
     */
    fun clearDestination() {
        _state.update {
            it.copy(
                destinationStation = null,
                availableRoutes = emptyList(),
                selectedRoute = null
            )
        }
    }
}
