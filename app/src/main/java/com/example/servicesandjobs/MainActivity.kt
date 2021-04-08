package com.example.servicesandjobs

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.servicesandjobs.databinding.ActivityMainBinding
import com.example.servicesandjobs.services.CustomIntentService
import com.example.servicesandjobs.services.CustomService

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding) {
            btnStartService.setOnClickListener {
                // Запускаем сервис
                Intent(this@MainActivity, CustomIntentService::class.java).also {
                    startService(it)
                    tvServiceStatus.text = "Service is running"
                }
            }

            btnStopService.setOnClickListener {
                CustomIntentService.stopService() // Останавливаем через функцию companion object
                tvServiceStatus.text = "Service is stopped"
            }
        }

//        startCustomService()
    }

    private fun startCustomService() {
        Intent(this, CustomService::class.java).also { intent ->
            startService(intent)
        }
    }
}