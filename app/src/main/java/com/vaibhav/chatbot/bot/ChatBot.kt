package com.vaibhav.chatbot.bot

/**
 * ChatBot processes user messages and generates responses.
 *
 * All response generation is designed to be called from a background thread
 * (e.g. via Kotlin Coroutines with Dispatchers.Default) so the main thread
 * is never blocked, preventing ANR errors.
 */
class ChatBot {

    private val greetings = setOf("hi", "hello", "hey", "howdy", "greetings", "good morning", "good afternoon", "good evening")
    private val farewells = setOf("bye", "goodbye", "see you", "later", "take care", "cya", "farewell")
    private val helpKeywords = setOf("help", "assist", "support", "how", "what", "guide")
    private val thankKeywords = setOf("thanks", "thank you", "thank", "thx", "ty", "appreciate")
    private val jokeKeywords = setOf("joke", "funny", "laugh", "humor", "comedy")
    private val weatherKeywords = setOf("weather", "temperature", "rain", "sunny", "cloudy", "forecast")
    private val timeKeywords = setOf("time", "date", "day", "clock", "hour", "minute")
    private val nameKeywords = setOf("name", "who are you", "what are you", "identify")

    /**
     * Generates a bot response for the given user input.
     *
     * This method may perform string processing and pattern matching. It is
     * intended to be called on a background thread (Dispatchers.Default) to
     * avoid blocking the main thread and causing ANR.
     *
     * @param userInput The text message entered by the user.
     * @return A response string from the bot.
     */
    fun generateResponse(userInput: String): String {
        val input = userInput.trim().lowercase()

        if (input.isBlank()) {
            return "Please type a message so I can help you!"
        }

        return when {
            matchesAny(input, greetings) -> getGreetingResponse()
            matchesAny(input, farewells) -> getFarewellResponse()
            matchesAny(input, thankKeywords) -> getThankYouResponse()
            matchesAny(input, jokeKeywords) -> getJokeResponse()
            matchesAny(input, weatherKeywords) -> getWeatherResponse()
            matchesAny(input, timeKeywords) -> getTimeResponse()
            matchesAny(input, nameKeywords) -> getNameResponse()
            matchesAny(input, helpKeywords) -> getHelpResponse()
            else -> getDefaultResponse(input)
        }
    }

    private fun matchesAny(input: String, keywords: Set<String>): Boolean =
        keywords.any { input.contains(it) }

    private fun getGreetingResponse(): String = listOf(
        "Hello! How can I assist you today? 😊",
        "Hi there! What can I do for you?",
        "Hey! Great to see you. How can I help?",
        "Greetings! I'm ready to chat."
    ).random()

    private fun getFarewellResponse(): String = listOf(
        "Goodbye! Have a wonderful day! 👋",
        "See you later! Take care!",
        "Farewell! Come back anytime. 😊",
        "Bye! It was nice chatting with you!"
    ).random()

    private fun getThankYouResponse(): String = listOf(
        "You're welcome! 😊",
        "Happy to help!",
        "Anytime! Is there anything else I can assist with?",
        "My pleasure!"
    ).random()

    private fun getJokeResponse(): String = listOf(
        "Why do programmers prefer dark mode? Because light attracts bugs! 🐛",
        "Why did the developer go broke? Because he used up all his cache! 💸",
        "Why do Java developers wear glasses? Because they don't C#! 👓",
        "How many programmers does it take to change a light bulb? None — that's a hardware problem! 💡"
    ).random()

    private fun getWeatherResponse(): String =
        "I don't have access to live weather data right now, but you can check a weather app for the latest forecast! ☀️🌧️"

    private fun getTimeResponse(): String {
        val calendar = java.util.Calendar.getInstance()
        val hour = calendar.get(java.util.Calendar.HOUR_OF_DAY)
        val minute = calendar.get(java.util.Calendar.MINUTE)
        return "The current time is %02d:%02d. 🕐".format(hour, minute)
    }

    private fun getNameResponse(): String =
        "I'm ChatBot, your friendly virtual assistant! 🤖 I'm here to help answer your questions and have a conversation."

    private fun getHelpResponse(): String =
        "I can help you with:\n• General questions & conversation\n• Jokes & fun facts\n• Checking the current time\n• And much more!\n\nJust type your message and I'll do my best to assist! 😊"

    private fun getDefaultResponse(input: String): String = listOf(
        "That's interesting! Tell me more.",
        "I see! Could you elaborate on that?",
        "Hmm, I'm not sure about that one. Can you rephrase?",
        "I'm still learning! Could you try asking differently?",
        "Great point! Let me think about that... 🤔"
    ).random()
}
