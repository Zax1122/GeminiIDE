package com.alishanj.geminiide.fragments

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.alishanj.geminiide.databinding.FragmentEditorBinding
import kotlinx.coroutines.*
import java.io.*

class EditorFragment : Fragment() {

    private var _binding: FragmentEditorBinding? = null
    private val binding get() = _binding!!
    private var currentUri: android.net.Uri? = null

    // Use Storage Access Framework — works on all API levels including 10+
    private val openFileLauncher = registerForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri ?: return@registerForActivityResult
        currentUri = uri
        try {
            requireContext().contentResolver.openInputStream(uri)?.use { stream ->
                val text = stream.bufferedReader().readText()
                binding.editorContent.setText(text)
                binding.fileName.text = uri.lastPathSegment ?: "file"
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Could not open file: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentEditorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnOpen.setOnClickListener {
            openFileLauncher.launch(arrayOf("*/*"))
        }
        binding.btnSave.setOnClickListener { saveFile() }
    }

    private fun saveFile() {
        val uri = currentUri ?: run {
            Toast.makeText(context, "No file open", Toast.LENGTH_SHORT).show()
            return
        }
        try {
            requireContext().contentResolver.openOutputStream(uri, "wt")?.use { stream ->
                stream.writer().use { it.write(binding.editorContent.text.toString()) }
            }
            Toast.makeText(context, "Saved!", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, "Could not save: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
