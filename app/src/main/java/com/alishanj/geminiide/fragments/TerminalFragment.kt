package com.alishanj.geminiide.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.alishanj.geminiide.databinding.FragmentTerminalBinding
import kotlinx.coroutines.*
import java.io.*

class TerminalFragment : Fragment() {

    private var _binding: FragmentTerminalBinding? = null
    private val binding get() = _binding!!
    private var process: Process? = null
    private var outputWriter: BufferedWriter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentTerminalBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startShell()

        binding.btnSend.setOnClickListener {
            val cmd = binding.inputCommand.text.toString()
            if (cmd.isNotEmpty()) {
                sendCommand(cmd)
                binding.inputCommand.setText("")
            }
        }
    }

    private fun startShell() {
        // Use viewLifecycleOwner.lifecycleScope — auto-cancelled when view is destroyed
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            try {
                val pb = ProcessBuilder("/system/bin/sh")
                pb.redirectErrorStream(true)
                process = pb.start()
                outputWriter = BufferedWriter(OutputStreamWriter(process!!.outputStream))

                val reader = BufferedReader(InputStreamReader(process!!.inputStream))
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    val output = line
                    withContext(Dispatchers.Main) {
                        if (_binding != null) appendOutput(output + "\n")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    if (_binding != null) appendOutput("Error: ${e.message}\n")
                }
            }
        }
    }

    private fun sendCommand(cmd: String) {
        appendOutput("$ $cmd\n")
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            try {
                outputWriter?.write("$cmd\n")
                outputWriter?.flush()
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    if (_binding != null) appendOutput("Error: ${e.message}\n")
                }
            }
        }
    }

    private fun appendOutput(text: String) {
        binding.terminalOutput.append(text)
        binding.scrollView.post {
            binding.scrollView.fullScroll(ScrollView.FOCUS_DOWN)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        process?.destroy()
        _binding = null
    }
}
