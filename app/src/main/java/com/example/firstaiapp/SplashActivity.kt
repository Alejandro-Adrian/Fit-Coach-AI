package com.example.firstaiapp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        val statusMessage = findViewById<TextView>(R.id.statusMessage)

        // Simulate readiness check (e.g., initialization, network check, etc.)
        statusMessage.text = "Initializing the app..."
        progressBar.progress = 0

        // Simulate a delay for initialization (3 seconds)
        Handler(Looper.getMainLooper()).postDelayed({
            // Once the app is ready, start MainActivity
            val isReady = checkAppReadiness()
            if (isReady) {
                startActivity(Intent(this, MainActivity::class.java))
                finish() // Close the splash screen
            } else {
                statusMessage.text = "Initialization failed. Please restart the app."
            }
        }, 3000) // Adjust delay as needed
    }

    private fun checkAppReadiness(): Boolean {
        // Example check: Replace with actual readiness logic
        return true // Assume app is ready
    }
}
