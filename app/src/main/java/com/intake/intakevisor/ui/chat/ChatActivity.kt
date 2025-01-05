package com.intake.intakevisor.ui.chat

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.intake.intakevisor.BaseMenuActivity
import com.intake.intakevisor.databinding.ActivityChatBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChatActivity : BaseMenuActivity() {
    private lateinit var binding: ActivityChatBinding

    private val messages = mutableListOf<Message>()
    private lateinit var adapter: MessageAdapter

    private val bot = ReceiverBot()

    // Flag to track if a new response from bot is needed after each user message
    private var isNewResponseNeeded = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        setupRecyclerView(binding.messagesRecyclerView, messages)
    }

    private fun setupUI() {
        binding.sendButton.setOnClickListener {
            val message = binding.messageInput.text.toString()
            if (message.isNotEmpty()) {
                binding.messageInput.text.clear()
                messages.add(Message(Message.SENDER_USER, message))
                adapter.notifyItemInserted(messages.size - 1)
                // After inserting the new message, scroll to the last position to keep the chat at the bottom
                binding.messagesRecyclerView.scrollToPosition(messages.size - 1)

                // Mark that a new response is needed
                isNewResponseNeeded = true

                sendMessage(message)
            }
        }
    }

    private fun sendMessage(message: String) {
        // Use Coroutine to start streaming the response
        CoroutineScope(Dispatchers.Main).launch {
            bot.getResponseFragments(message) { fragment ->
                // If we need a new response (which means the last message is from the user)
                if (isNewResponseNeeded) {
                    // Add a new assistant message for the response
                    messages.add(Message(Message.SENDER_ASSISTANT, fragment))
                    isNewResponseNeeded = false
                } else {
                    // Update the existing assistant message with the new fragment
                    val lastMessageIndex = messages.indexOfLast { it.sender == Message.SENDER_ASSISTANT }
                    if (lastMessageIndex != -1) {
                        val updatedMessage = messages[lastMessageIndex].text + " $fragment"
                        messages[lastMessageIndex] = Message(Message.SENDER_ASSISTANT, updatedMessage)
                    }
                }

                binding.chatTitle.visibility = View.GONE

                // Notify the adapter to update the RecyclerView
                adapter.notifyItemChanged(messages.size - 1)

                // Scroll to the last position to keep the chat at the bottom
                binding.messagesRecyclerView.scrollToPosition(messages.size - 1)
            }
        }
    }

    private fun setupRecyclerView(recyclerView: RecyclerView, foodItems: MutableList<Message>) {
        val layoutManager = LinearLayoutManager(this)
        layoutManager.stackFromEnd = true  // Start from the bottom of the RecyclerView
        recyclerView.layoutManager = layoutManager
        adapter = MessageAdapter(foodItems)
        recyclerView.adapter = adapter
    }
}
