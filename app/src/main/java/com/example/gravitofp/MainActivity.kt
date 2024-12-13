package com.example.gravitofp

import android.os.Bundle
import android.os.StrictMode
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import android.content.Context
import android.content.SharedPreferences
import android.widget.EditText
import org.json.JSONObject
import java.util.UUID

class MainActivity : AppCompatActivity() {
    private lateinit var textView: TextView
    private lateinit var idEditText: EditText
    private  lateinit var idJsonEditText: TextView
    private lateinit var apiClient: GravitoFPApiClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Add StrictMode policy
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

//        Initialize Api Client
     apiClient = GravitoFPApiClient(this, "your-domain.com", "app.your-domain.com")


        idEditText = findViewById(R.id.idEditText)
        idJsonEditText = findViewById(R.id.jsonTextView)
        val getButton: Button = findViewById(R.id.getButton)
        getButton.setOnClickListener {
            onGetButtonClick()
        }
        val postButton :Button  = findViewById(R.id.postButton)
        postButton.setOnClickListener {
            onPostButtonClick()
        }


    }



    private fun onPostButtonClick() {

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiClient.updateConsentData(idEditText.text.toString())
                runOnUiThread {
                    idJsonEditText.text = "your Current consent data is: ${response}"
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this@MainActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }



    private fun onGetButtonClick() {

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiClient.getConsentData()
                runOnUiThread {
                    idJsonEditText.text = "your Current consent data is: ${response}"
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this@MainActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }



}