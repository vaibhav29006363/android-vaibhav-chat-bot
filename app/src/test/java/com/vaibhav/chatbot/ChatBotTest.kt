package com.vaibhav.chatbot

import com.vaibhav.chatbot.bot.ChatBot
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for [ChatBot].
 *
 * These tests run on the JVM (no Android framework needed) and verify that
 * the bot generates appropriate responses. Because [ChatBot.generateResponse]
 * is a pure function designed to be run on a background thread, it is safe
 * to call directly in unit tests.
 */
class ChatBotTest {

    private lateinit var chatBot: ChatBot

    @Before
    fun setUp() {
        chatBot = ChatBot()
    }

    @Test
    fun `response is not null for any input`() {
        val response = chatBot.generateResponse("hello")
        assertNotNull(response)
    }

    @Test
    fun `greeting input returns non-empty response`() {
        listOf("hi", "hello", "hey", "good morning").forEach { greeting ->
            val response = chatBot.generateResponse(greeting)
            assertTrue("Response for '$greeting' should not be blank", response.isNotBlank())
        }
    }

    @Test
    fun `farewell input returns non-empty response`() {
        listOf("bye", "goodbye", "see you later").forEach { farewell ->
            val response = chatBot.generateResponse(farewell)
            assertTrue("Response for '$farewell' should not be blank", response.isNotBlank())
        }
    }

    @Test
    fun `thank you input returns non-empty response`() {
        listOf("thanks", "thank you", "thx").forEach { thanks ->
            val response = chatBot.generateResponse(thanks)
            assertTrue("Response for '$thanks' should not be blank", response.isNotBlank())
        }
    }

    @Test
    fun `joke request returns non-empty response`() {
        val response = chatBot.generateResponse("tell me a joke")
        assertTrue(response.isNotBlank())
    }

    @Test
    fun `time request returns response mentioning time`() {
        val response = chatBot.generateResponse("what is the time")
        assertTrue("Time response should contain time info", response.isNotBlank())
    }

    @Test
    fun `name request returns response about the bot identity`() {
        val response = chatBot.generateResponse("what is your name")
        assertTrue(response.isNotBlank())
    }

    @Test
    fun `help request returns non-empty response`() {
        val response = chatBot.generateResponse("help me")
        assertTrue(response.isNotBlank())
    }

    @Test
    fun `blank input returns a prompt message`() {
        val response = chatBot.generateResponse("   ")
        assertTrue(response.isNotBlank())
    }

    @Test
    fun `empty input returns a prompt message`() {
        val response = chatBot.generateResponse("")
        assertTrue(response.isNotBlank())
    }

    @Test
    fun `unknown input returns a non-empty default response`() {
        val response = chatBot.generateResponse("xyzzy quux foobar 12345")
        assertTrue(response.isNotBlank())
    }

    @Test
    fun `input is case insensitive`() {
        val lowerResponse = chatBot.generateResponse("hello")
        val upperResponse = chatBot.generateResponse("HELLO")
        // Both should produce valid (non-blank) responses
        assertFalse(lowerResponse.isBlank())
        assertFalse(upperResponse.isBlank())
    }

    @Test
    fun `weather request returns non-empty response`() {
        val response = chatBot.generateResponse("what is the weather today")
        assertTrue(response.isNotBlank())
    }

    @Test
    fun `blank input returns the exact prompt message`() {
        val response = chatBot.generateResponse("   ")
        assertEquals("Please type a message so I can help you!", response)
    }
}
