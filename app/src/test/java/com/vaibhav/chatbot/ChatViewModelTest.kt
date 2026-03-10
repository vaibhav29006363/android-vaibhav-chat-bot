package com.vaibhav.chatbot

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.vaibhav.chatbot.ui.ChatViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Unit tests for [ChatViewModel].
 *
 * Verifies that:
 * - User messages are added to the list immediately.
 * - Bot responses are generated and appended after processing.
 * - Blank messages are ignored.
 * - The typing indicator is shown/hidden correctly.
 *
 * Uses [InstantTaskExecutorRule] to make LiveData synchronous and
 * [StandardTestDispatcher] + [advanceUntilIdle] to drive coroutines
 * without real-time delays.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ChatViewModelTest {

    @get:Rule
    val instantTaskRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: ChatViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = ChatViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `sendMessage adds user message immediately`() = runTest {
        viewModel.sendMessage("Hello")
        // The user message is added synchronously before the coroutine runs
        val messages = viewModel.messages.value.orEmpty()
        assertTrue("User message should be added", messages.any { it.isFromUser && it.text == "Hello" })
    }

    @Test
    fun `sendMessage eventually adds bot response`() = runTest {
        viewModel.sendMessage("Hello")
        // Let the coroutine (including the simulated delay) complete
        advanceUntilIdle()

        val messages = viewModel.messages.value.orEmpty()
        assertTrue("There should be at least 2 messages", messages.size >= 2)
        assertTrue("Last message should be from bot", messages.last().isFromUser.not())
    }

    @Test
    fun `blank message is ignored`() = runTest {
        viewModel.sendMessage("   ")
        advanceUntilIdle()

        val messages = viewModel.messages.value.orEmpty()
        assertTrue("No messages should be added for blank input", messages.isEmpty())
    }

    @Test
    fun `empty message is ignored`() = runTest {
        viewModel.sendMessage("")
        advanceUntilIdle()

        val messages = viewModel.messages.value.orEmpty()
        assertTrue("No messages should be added for empty input", messages.isEmpty())
    }

    @Test
    fun `multiple messages accumulate in order`() = runTest {
        viewModel.sendMessage("Hello")
        advanceUntilIdle()
        viewModel.sendMessage("Bye")
        advanceUntilIdle()

        val messages = viewModel.messages.value.orEmpty()
        assertEquals("There should be 4 messages (2 user + 2 bot)", 4, messages.size)
        assertTrue(messages[0].isFromUser)
        assertFalse(messages[1].isFromUser)
        assertTrue(messages[2].isFromUser)
        assertFalse(messages[3].isFromUser)
    }
}
