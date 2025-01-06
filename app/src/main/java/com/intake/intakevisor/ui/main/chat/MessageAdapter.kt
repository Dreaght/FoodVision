package com.intake.intakevisor.ui.main.chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.intake.intakevisor.R

class MessageAdapter(private val messages: List<Message>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val MESSAGE_TYPE_SENT = 1
        private const val MESSAGE_TYPE_RECEIVED = 2
    }

    override fun getItemViewType(position: Int): Int {
        return when (messages[position].sender) {
            Message.SENDER_USER -> MESSAGE_TYPE_SENT
            Message.SENDER_ASSISTANT -> MESSAGE_TYPE_RECEIVED
            else -> throw IllegalArgumentException("Invalid message sender type")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            MESSAGE_TYPE_SENT -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_message_sent, parent, false)
                SentMessageViewHolder(view)
            }
            MESSAGE_TYPE_RECEIVED -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_message_received, parent, false)
                ReceivedMessageViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]

        when (holder) {
            is SentMessageViewHolder -> holder.bind(message.text)
            is ReceivedMessageViewHolder -> holder.bind(message.text)
        }
    }

    override fun getItemCount(): Int = messages.size

    // ViewHolder for sent messages
    inner class SentMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val sentText: TextView = itemView.findViewById(R.id.sentMessageText)

        fun bind(messageText: String) {
            sentText.text = messageText
        }
    }

    // ViewHolder for received messages
    inner class ReceivedMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val receivedText: TextView = itemView.findViewById(R.id.receivedMessageText)

        fun bind(messageText: String) {
            receivedText.text = messageText
        }
    }
}
