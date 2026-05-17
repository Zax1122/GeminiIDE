package com.alishanj.geminiide.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.alishanj.geminiide.adapters.ChatAdapter
import com.alishanj.geminiide.api.GeminiApi
import com.alishanj.geminiide.databinding.FragmentChatBinding
import com.alishanj.geminiide.models.ChatMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChatFragment : Fragment() {

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!
    private val messages = mutableListOf<ChatMessage>()
    private lateinit var adapter: ChatAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ChatAdapter(messages)
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = adapter

        binding.btnSend.setOnClickListener {
            val text = binding.inputMessage.text.toString().trim()
            if (text.isNotEmpty()) {
                sendMessage(text)
                binding.inputMessage.setText("")
            }
        }

        binding.btnClearChat.setOnClickListener {
            messages.clear()
            GeminiApi.clearHistory()
            adapter.notifyDataSetChanged()
        }
    }

    private fun sendMessage(text: String) {
        messages.add(ChatMessage(text, true))
        adapter.notifyItemInserted(messages.size - 1)
        binding.recyclerView.scrollToPosition(messages.size - 1)

        val prefs = com.alishanj.geminiide.SettingsActivity.getEncryptedPrefs(requireContext())
        val apiKey = prefs.getString("api_key", "") ?: ""

        if (apiKey.isEmpty()) {
            messages.add(ChatMessage("Please set your Gemini API key in Settings.", false))
            adapter.notifyItemInserted(messages.size - 1)
            return
        }

        binding.progressBar.visibility = View.VISIBLE
        binding.btnSend.isEnabled = false

        // Use viewLifecycleOwner.lifecycleScope — auto-cancelled when view is destroyed
        viewLifecycleOwner.lifecycleScope.launch {
            val response = withContext(Dispatchers.IO) {
                GeminiApi.sendMessage(apiKey, text)
            }
            if (_binding != null) {
                binding.progressBar.visibility = View.GONE
                binding.btnSend.isEnabled = true
                messages.add(ChatMessage(response, false))
                adapter.notifyItemInserted(messages.size - 1)
                binding.recyclerView.scrollToPosition(messages.size - 1)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
