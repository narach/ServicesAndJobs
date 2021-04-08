package com.example.services

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.services.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            btnStart.setOnClickListener {
                Intent(this@MainActivity, CustomService::class.java).also {
                    startService(it)
                    tvStatus.text = "Setvice running"
                }
            }

            btnStop.setOnClickListener {
                // Stop service wia intent
                Intent(this@MainActivity, CustomService::class.java).also {
                    stopService(it)
                    tvStatus.text = "Setvice stopped"
                }
            }

            btnSend.setOnClickListener {
                // Stop service via intent
                Intent(this@MainActivity, CustomService::class.java).also {
                    val dataString = etData.text.toString()
                    it.putExtra("EXTRA_DATA", dataString)
                    startService(it) // Starting service with sending some data
                }
            }
        }
    }
}