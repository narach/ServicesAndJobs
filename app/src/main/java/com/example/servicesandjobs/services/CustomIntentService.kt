package com.example.servicesandjobs.services

import android.app.IntentService
import android.content.Intent
import android.content.Context
import android.util.Log

class CustomIntentService : IntentService("CustomIntentService") {

    val logTag = "CustomIntentService"

    init {
        instance = this
    }

    companion object {

        val logTag = "CustomIntentService"

        private lateinit var instance : CustomIntentService
        var isRunning = false

        fun stopService() {
            Log.d(logTag, "Service is stopping...")
            isRunning = false
            instance.stopSelf() // Realy stops the service
        }
    }

    override fun onHandleIntent(intent: Intent?) {
        try {
            isRunning = true
            while(isRunning) {
                Log.d(logTag,"Service is running...")
                Thread.sleep(1000)
            }
        } catch (e: InterruptedException) {
            Thread.currentThread().interrupt()
        }
    }


}