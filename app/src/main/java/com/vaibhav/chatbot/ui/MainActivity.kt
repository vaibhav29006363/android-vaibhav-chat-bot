package com.vaibhav.chatbot.ui

import android.os.Bundle
import android.view.inputmethod.EditorInfo
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.vaibhav.chatbot.databinding.ActivityMainBinding

/**
 * Main chat screen.
 *
 * The Activity itself stays lightweight — it only handles UI events and
 * observes [ChatViewModel]. All heavy work (bot response generation) is
 * handled off the main thread by the ViewModel via Kotlin Coroutines,
 * which prevents ANR errors.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: ChatViewModel by viewModels()
    private lateinit var chatAdapter: ChatAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupInput()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        chatAdapter = ChatAdapter()
        binding.rvMessages.apply {
            adapter = chatAdapter
            layoutManager = LinearLayoutManager(this@MainActivity).apply {
                stackFromEnd = true
            }
        }
    }

    private fun setupInput() {
        binding.btnSend.setOnClickListener { sendMessage() }

        binding.etMessage.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                sendMessage()
                true
            } else {
                false
            }
        }
    }

    private fun sendMessage() {
        val text = binding.etMessage.text?.toString() ?: return
        if (text.isBlank()) return

        viewModel.sendMessage(text)
        binding.etMessage.setText("")
    }

    private fun observeViewModel() {
        viewModel.messages.observe(this) { messages ->
            chatAdapter.submitList(messages.toList()) {
                // Scroll to the latest message after the list is updated
                if (messages.isNotEmpty()) {
                    binding.rvMessages.smoothScrollToPosition(messages.size - 1)
                }
            }
        }

        viewModel.isTyping.observe(this) { isTyping ->
            binding.tvTypingIndicator.visibility =
                if (isTyping) android.view.View.VISIBLE else android.view.View.GONE
        }
    }
}
