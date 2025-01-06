package com.intake.intakevisor.ui.main.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.intake.intakevisor.databinding.ChatFragmentBinding
import com.intake.intakevisor.ui.main.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChatFragment : Fragment() {

    private var _binding: ChatFragmentBinding? = null
    private val binding get() = _binding!!

    lateinit var mainActivity: MainActivity

    private val messages = mutableListOf<Message>()
    private lateinit var adapter: MessageAdapter
    private val bot = ReceiverBot()
    private var isNewResponseNeeded = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ChatFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivity = requireActivity() as MainActivity

        setupUI()
        setupRecyclerView(binding.messagesRecyclerView, messages)
    }

    private fun setupUI() {
        binding.sendButton.setOnClickListener {
            val message = binding.messageInput.text.toString()
            if (message.isNotEmpty()) {
                binding.messageInput.text.clear()
                messages.add(Message(Message.Companion.SENDER_USER, message))
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
                    messages.add(Message(Message.Companion.SENDER_ASSISTANT, fragment))
                    isNewResponseNeeded = false
                } else {
                    // Update the existing assistant message with the new fragment
                    val lastMessageIndex = messages.indexOfLast { it.sender == Message.Companion.SENDER_ASSISTANT }
                    if (lastMessageIndex != -1) {
                        val updatedMessage = messages[lastMessageIndex].text + " $fragment"
                        messages[lastMessageIndex] =
                            Message(Message.Companion.SENDER_ASSISTANT, updatedMessage)
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
        val layoutManager = LinearLayoutManager(mainActivity)
        layoutManager.stackFromEnd = true  // Start from the bottom of the RecyclerView
        recyclerView.layoutManager = layoutManager
        adapter = MessageAdapter(foodItems)
        recyclerView.adapter = adapter
    }
}
