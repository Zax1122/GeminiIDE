package com.alishanj.geminiide

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.alishanj.geminiide.databinding.ActivityMainBinding
import com.alishanj.geminiide.fragments.ChatFragment
import com.alishanj.geminiide.fragments.EditorFragment
import com.alishanj.geminiide.fragments.TerminalFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // Cache fragment instances so bottom nav tabs don't restart on every tap
    private val terminalFragment by lazy { TerminalFragment() }
    private val editorFragment by lazy { EditorFragment() }
    private val chatFragment by lazy { ChatFragment() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        // Default to terminal
        if (savedInstanceState == null) {
            loadFragment(terminalFragment)
        }

        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_terminal -> loadFragment(terminalFragment)
                R.id.nav_editor -> loadFragment(editorFragment)
                R.id.nav_chat -> loadFragment(chatFragment)
            }
            true
        }
    }

    private fun loadFragment(fragment: Fragment) {
        val tag = fragment::class.java.simpleName
        val fm = supportFragmentManager
        val tx = fm.beginTransaction()

        // Hide all other fragments, show or add this one
        fm.fragments.forEach { if (it != fragment) tx.hide(it) }
        if (!fragment.isAdded) tx.add(R.id.fragmentContainer, fragment, tag)
        else tx.show(fragment)

        tx.commit()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
