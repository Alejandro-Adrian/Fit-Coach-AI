package com.example.firstaiapp

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.Content
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val eTPrompt = findViewById<EditText>(R.id.eTPrompt)
        val scrollView = findViewById<ScrollView>(R.id.scrollView)
        val btnSubmit = findViewById<Button>(R.id.btnSubmit)
        val loadingProgressBar = findViewById<ProgressBar>(R.id.loadingProgressBar)  // Reference to ProgressBar

        val layout = scrollView.getChildAt(0) as LinearLayout

        val welcomeMessage = TextView(this@MainActivity).apply {
            text = "Welcome, I am 'Fit Coach!' I'm your exclusive fitness coach."
            setBackgroundResource(R.drawable.received_message_bg)
            setTextColor(getColor(R.color.received_message_text))
            setPadding(20, 20, 20, 20)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                marginStart = 20
                marginEnd = 100
                topMargin = 10
            }
        }

        layout.addView(welcomeMessage)

        btnSubmit.setOnClickListener {
            val prompt = eTPrompt.text.toString()

            // Show the loading ProgressBar
            loadingProgressBar.visibility = View.VISIBLE

            val generativeModel = GenerativeModel(
                modelName = "gemini-2.0-flash-exp",
                apiKey = "AIzaSyDUUUPAHnu2Num6Sm_HYSpxTDGh3GgmxzE", // Replace with your API key
                generationConfig = generationConfig {
                    temperature = 1f
                    topK = 40
                    topP = 0.95f
                    maxOutputTokens = 8192
                    responseMimeType = "text/plain"
                },
                systemInstruction = content {
                    text(
                        "You are a fitness coach, you can give all information and explain as long as it is relevant to fitness, workout, exercise, nutrients, drugs, supplements, make workout plans, etc. Make it concise focusing on keywords. Absolutely don't give any information not related.you are a fitness coach, you can give all information and explain as long as it is relevant to fitness, workout, exercise, nutrients, drugs, suppliments, make workout plan, etc. make it concise focusing on keywords, absolutely don't give any information not related, just focus on being a coach, trainer, mentor, nothing else, if they ask anything unreleated just tell them \"Fuck off, I'm not google I'm a coach (made using google)\""
                    )
                }
            )

            // Define the chat history, including some example interactions
            val chatHistory = listOf(
                content("user") {
                    text("Who is Jose Rizal?")
                },
                content("model") {
                    text("Jose Rizal is **not relevant** to fitness, workout, exercise, nutrients, drugs, or supplements. I cannot provide information on him.\n")
                },
                content("user") {
                    text("What can you do?")
                },
                content("model") {
                    text("I can provide information and guidance related to:\n\n**Fitness & Exercise:**\n\n* Workout planning\n* Exercise explanations\n* Training principles\n\n**Nutrition:**\n* Macronutrients\n* Micronutrients\n* Meal planning\n\n**Supplements:**\n* Common supplements\n* Dosage guidance\n\n...and much more!")
                },
                content("user") {
                    text("Make a workout routine for biceps")
                },
                content("model") {
                    text("Here's a biceps-focused workout routine:\n\n* **Barbell Curls:** 3 sets of 8-12 reps\n* **Dumbbell Hammer Curls:** 3 sets of 10-15 reps\n...")
                }
            )

            CoroutineScope(Dispatchers.Main).launch {
                try {
                    val chat = generativeModel.startChat(chatHistory)

                    val response = withContext(Dispatchers.IO) {
                        chat.sendMessage(prompt)
                    }

                    val userMessage = TextView(this@MainActivity).apply {
                        text = "User: $prompt"
                        setBackgroundResource(R.drawable.sent_message_bg)
                        setTextColor(getColor(R.color.sent_message_text))
                        setPadding(20, 20, 20, 20)
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        ).apply {
                            gravity = Gravity.END
                            marginEnd = 20
                            topMargin = 10
                        }
                    }

                    val aiMessage = TextView(this@MainActivity).apply {
                        text = "Fit Coach: ${response.text}"
                        setBackgroundResource(R.drawable.received_message_bg)
                        setTextColor(getColor(R.color.received_message_text))
                        setPadding(20, 20, 20, 20)
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        ).apply {
                            marginStart = 20
                            marginEnd = 100
                            topMargin = 10
                        }
                    }

                    layout.addView(userMessage)
                    layout.addView(aiMessage)

                    // Hide the loading ProgressBar after receiving the response
                    loadingProgressBar.visibility = View.GONE

                    eTPrompt.setText("") // Clear input field

                    scrollView.post {
                        scrollView.fullScroll(ScrollView.FOCUS_DOWN) // Scroll to bottom
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    loadingProgressBar.visibility = View.GONE // Hide the progress bar on error
                }
            }
        }

        // For adjusting the layout with window insets (e.g., status bar height)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}
