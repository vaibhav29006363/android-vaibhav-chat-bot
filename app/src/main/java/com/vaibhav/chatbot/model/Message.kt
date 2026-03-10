package com.vaibhav.chatbot.model

/**
 * Represents a single chat message.
 *
 * @param text The message text content.
 * @param isFromUser True if the message was sent by the user, false if from the bot.
 * @param timestamp Unix timestamp (milliseconds) when the message was created.
 */
data class Message(
    val text: String,
    val isFromUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)
