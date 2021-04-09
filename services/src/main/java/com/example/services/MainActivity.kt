package com.example.services

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.example.services.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var isTracking = false
    private var currentTimeInMillis = 0L

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

            btnTimerStart.setOnClickListener {
                toggleRun()
            }

            subscribeToObservers()
        }
    }

    private fun toggleRun() {
        if(isTracking) {
            sendCommandToService(Constants.ACTION_PAUSE_SERVICE)
        } else {
            sendCommandToService(Constants.ACTION_START_OR_RESUME_SERVICE)
        }
    }

    private fun updateTracking(isTracking: Boolean) {
        this.isTracking = isTracking
        if(!isTracking) {
            binding.btnTimerStop.visibility = View.GONE
        } else {
            binding.btnTimerStop.visibility = View.VISIBLE
        }
    }

    private fun subscribeToObservers() {
        TimerService.isTracking.observe(this, Observer {
            updateTracking(it)
        })

        TimerService.timeRunInMillis.observe(this, Observer {
            currentTimeInMillis = it
            val formattedTime = TimerUtility.getFormattedStopWatchTime(currentTimeInMillis, true)
            binding.tvTimer.text = formattedTime
        })
    }

    private fun sendCommandToService(action: String) {
        Intent(applicationContext, TimerService::class.java).also {
            it.action = action
            applicationContext.startService(it)
        }
    }
}