package com.vaibhav.chatbot.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vaibhav.chatbot.bot.ChatBot
import com.vaibhav.chatbot.model.Message
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * ViewModel for the chat screen.
 *
 * ANR Fix: All bot response generation is performed on [Dispatchers.Default]
 * (a background thread pool) via Kotlin Coroutines and [viewModelScope].
 * Results are posted back to [Dispatchers.Main] only when the UI needs to be
 * updated. This ensures the main thread is never blocked by heavy processing,
 * eliminating ANR errors.
 */
class ChatViewModel : ViewModel() {

    private val chatBot = ChatBot()

    private val _messages = MutableLiveData<List<Message>>(emptyList())
    val messages: LiveData<List<Message>> get() = _messages

    private val _isTyping = MutableLiveData(false)
    val isTyping: LiveData<Boolean> get() = _isTyping

    /**
     * Sends a user message and generates a bot response asynchronously.
     *
     * The bot response is computed on [Dispatchers.Default] to prevent
     * blocking the main thread. A simulated typing delay is added for a
     * natural conversation feel.
     *
     * @param userText The text typed by the user.
     */
    fun sendMessage(userText: String) {
        val trimmed = userText.trim()
        if (trimmed.isBlank()) return

        val userMessage = Message(text = trimmed, isFromUser = true)
        appendMessage(userMessage)

        // ANR Fix: launch a coroutine so bot processing runs off the main thread
        viewModelScope.launch {
            _isTyping.value = true

            // Generate the bot response on the Default dispatcher (background thread)
            val botResponse = withContext(Dispatchers.Default) {
                // Simulate a short processing delay to mimic a real async operation
                delay(BOT_RESPONSE_DELAY_MS)
                chatBot.generateResponse(trimmed)
            }

            _isTyping.value = false

            // Back on Main dispatcher — safe to update LiveData
            val botMessage = Message(text = botResponse, isFromUser = false)
            appendMessage(botMessage)
        }
    }

    private fun appendMessage(message: Message) {
        val current = _messages.value.orEmpty().toMutableList()
        current.add(message)
        _messages.value = current
    }

    companion object {
        private const val BOT_RESPONSE_DELAY_MS = 800L
    }
}
