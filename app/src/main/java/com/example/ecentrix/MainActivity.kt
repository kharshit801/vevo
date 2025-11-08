// File: app/src/main/java/com/example/ecentrix/MainActivity.kt
package com.example.ecentrix

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var statusTextView: TextView
    private lateinit var enableButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        statusTextView = findViewById(R.id.statusTextView)
        enableButton = findViewById(R.id.enableButton)

        enableButton.setOnClickListener {
            promptForAccessibilityPermission()
        }

        checkServiceStatus()
    }

    override fun onResume() {
        super.onResume()
        checkServiceStatus()
    }

    private fun checkServiceStatus() {
        if (isAccessibilityServiceEnabled(this, AssistantService::class.java)) {
            statusTextView.text = "✓ Accessibility Service is Enabled"
            enableButton.isEnabled = false
        } else {
            statusTextView.text = "✗ Accessibility Service is Disabled"
            enableButton.isEnabled = true
        }
    }

    private fun isAccessibilityServiceEnabled(
        context: Context,
        serviceClass: Class<out AccessibilityService>
    ): Boolean {
        val expectedComponentName = "${context.packageName}/${serviceClass.name}"
        val enabledServicesSetting = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        ) ?: return false

        val colonSplitter = TextUtils.SimpleStringSplitter(':')
        colonSplitter.setString(enabledServicesSetting)

        while (colonSplitter.hasNext()) {
            val componentName = colonSplitter.next()
            if (componentName.equals(expectedComponentName, ignoreCase = true)) {
                return true
            }
        }

        return false
    }

    private fun promptForAccessibilityPermission() {
        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
        startActivity(intent)
    }
}