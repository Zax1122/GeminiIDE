package com.alishanj.geminiide.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.alishanj.geminiide.R
import com.alishanj.geminiide.models.ChatMessage

class ChatAdapter(private val messages: List<ChatMessage>) :
    RecyclerView.Adapter<ChatAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val userMessage: TextView = view.findViewById(R.id.tvUserMessage)
        val aiMessage: TextView = view.findViewById(R.id.tvAiMessage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_chat_message, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val message = messages[position]
        if (message.isUser) {
            holder.userMessage.visibility = View.VISIBLE
            holder.aiMessage.visibility = View.GONE
            holder.userMessage.text = message.text
        } else {
            holder.userMessage.visibility = View.GONE
            holder.aiMessage.visibility = View.VISIBLE
            holder.aiMessage.text = message.text
        }
    }

    override fun getItemCount() = messages.size
}
