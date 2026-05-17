package com.alishanj.geminiide

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.alishanj.geminiide.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Settings"

        val prefs = getEncryptedPrefs()
        binding.etApiKey.setText(prefs.getString("api_key", ""))

        binding.btnSave.setOnClickListener {
            val key = binding.etApiKey.text.toString().trim()
            prefs.edit().putString("api_key", key).apply()
            Toast.makeText(this, "API Key saved!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    companion object {
        fun getEncryptedPrefs(context: Context) =
            EncryptedSharedPreferences.create(
                context,
                "gemini_prefs",
                MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build(),
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
    }

    private fun getEncryptedPrefs() = getEncryptedPrefs(this)
}
