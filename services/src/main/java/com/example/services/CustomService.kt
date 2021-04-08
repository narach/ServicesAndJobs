package com.example.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log

class CustomService : Service() {

    val logTag = "CustomService"

    init {
        Log.d(logTag, "Service is running...")
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val dataString = intent?.getStringExtra("EXTRA_DATA")
        dataString?.let {
            Log.d(logTag, dataString)
        }

//        while(true) { // Locks UI(Main Thread) }

        // For complex actions - start a new Thread(or coroutine)
        Thread {
            while (true) {}
        }.start()

//        return START_NOT_STICKY // Service won't be recreated after it's killed
        return START_STICKY // Service will be recreated when possible
//        return START_REDELIVER_INTENT // Service is scheduled to restart
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(logTag, "Service is being killed")
    }
}