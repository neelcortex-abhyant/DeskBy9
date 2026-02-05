package com.neelcortex.deskby9.metro.presentation.chat

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

sealed class ChatNavigationEvent {
    object NavigateToHome : ChatNavigationEvent()
    object NavigateToLiveTracking : ChatNavigationEvent()
    object None : ChatNavigationEvent()
}

@HiltViewModel
class ChatBotViewModel @Inject constructor() : ViewModel() {

    private val _messages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(text = "Hello! I'm your Metro Assistant. How can I help you today?", isUser = false)
        )
    )
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _navigationEvent = MutableStateFlow<ChatNavigationEvent>(ChatNavigationEvent.None)
    val navigationEvent: StateFlow<ChatNavigationEvent> = _navigationEvent.asStateFlow()

    fun sendMessage(text: String) {
        val userMsg = ChatMessage(text = text, isUser = true)
        _messages.value = _messages.value + userMsg

        // Simple rule-based response
        when {
            text.contains("track", ignoreCase = true) || text.contains("live", ignoreCase = true) -> {
                respond("Taking you to live tracking feature...")
                _navigationEvent.value = ChatNavigationEvent.NavigateToLiveTracking
            }
            text.contains("plan", ignoreCase = true) || text.contains("journey", ignoreCase = true) -> {
                respond("Let's plan your journey. Navigating to planner...")
                _navigationEvent.value = ChatNavigationEvent.NavigateToHome
            }
            else -> {
                respond("I can help you plan a journey or track your location. Try saying 'Plan journey' or 'Live tracking'.")
            }
        }
    }

    private fun respond(text: String) {
        val botMsg = ChatMessage(text = text, isUser = false)
        _messages.value = _messages.value + botMsg
    }

    fun consumeNavigationEvent() {
        _navigationEvent.value = ChatNavigationEvent.None
    }
}
