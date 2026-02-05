package com.neelcortex.deskby9.metro.presentation.live

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neelcortex.deskby9.metro.data.location.LocationService
import com.neelcortex.deskby9.metro.presentation.util.AudioService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LiveJourneyViewModel @Inject constructor(
    private val locationService: LocationService,
    private val audioService: AudioService
) : ViewModel() {

    private val _currentLocation = MutableStateFlow<Location?>(null)
    val currentLocation: StateFlow<Location?> = _currentLocation.asStateFlow()

    private val _statusMessage = MutableStateFlow("Waiting for location...")
    val statusMessage: StateFlow<String> = _statusMessage.asStateFlow()

    init {
        startLocationUpdates()
    }

    private fun startLocationUpdates() {
        viewModelScope.launch {
            audioService.speak("Starting live journey tracking")
            try {
                locationService.getLocationUpdates(5000L).collect { location ->
                    _currentLocation.value = location
                    val message = "Location updated: Lat ${location.latitude}, Lng ${location.longitude}"
                    _statusMessage.value = message
                    
                    // Audio update on location change
                    // In a real app, this would check against route waypoints
                    audioService.speak("Location updated")
                }
            } catch (e: Exception) {
                _statusMessage.value = "Error: ${e.message}"
                audioService.speak("Error getting location updates")
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        audioService.shutdown()
    }
}
